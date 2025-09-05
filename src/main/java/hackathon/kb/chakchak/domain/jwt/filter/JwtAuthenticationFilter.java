package hackathon.kb.chakchak.domain.jwt.filter;

import hackathon.kb.chakchak.domain.auth.MemberPrincipal;
import hackathon.kb.chakchak.domain.jwt.util.JwtIssuer;
import hackathon.kb.chakchak.global.redis.util.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtIssuer jwtIssuer;
    private final RedisUtil redisUtil;

    private static final String ACCESS_ERR  = "JWT_ACCESS_ERROR_CODE";
    private static final String ACCESS_ERR_MSG = "JWT_ACCESS_ERROR_MSG";

    private static final String[] WHITELIST = {
            "/", "/index.html", "/favicon.ico", "/health/", "/health/**",
            "/api/oauth/**", "/login/**", "/oauth2/**", "/oauth2/authorization/kakao/**", "/api/oauth/signup/additional",
            "/api/auth/**", "/api/auth/signup", "/api/auth/refresh",
            // Swagger
            "/swagger-ui.html", "/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        return PatternMatchUtils.simpleMatch(WHITELIST, uri);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String accessToken = parseBearerToken(req);

        // 토큰 없으면 바로 다음 필터로
        if (!StringUtils.hasText(accessToken)) {
            chain.doFilter(req, res);
            return;
        }

        log.info("검증할 access token 값: {}", accessToken);

        // 블랙리스트(로그아웃) 방어
        if (redisUtil.hasKeyBlackList(accessToken)) {
            // 전역 예외 처리기가 있다면 예외 던지기
            // throw new CustomException("로그아웃된 사용자 입니다.");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write("{\"code\":\"LOGGED_OUT\"}");
            return;
        }

        try {
            Claims claims = jwtIssuer.parseJws(accessToken).getBody();
            log.info("access token을 통해 검증한 사용자의 값입니다: {}", claims);

            if ("access".equals(claims.get("typ", String.class))) {
                Long memberId = Long.valueOf(claims.getSubject());
                String role = claims.get("role", String.class);
                log.info("추출한 memberId: {}, role: {}", memberId, role);

                MemberPrincipal principal = new MemberPrincipal(memberId, role);
                log.info("principal: {}", principal);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))); // AuthorityUtils.NO_AUTHORITIES

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                markError(req,  JwtErrorCode.INVALID, "Not an access token (typ mismatch)");
                SecurityContextHolder.clearContext();
            }

        } catch (ExpiredJwtException e) {
            log.debug("ACCESS expired", e);
            markError(req, JwtErrorCode.EXPIRED, e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (MalformedJwtException e) {
            log.debug("ACCESS malformed", e);
            markError(req, JwtErrorCode.MALFORMED, e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (SignatureException e) {
            log.debug("ACCESS bad signature", e);
            markError(req,JwtErrorCode.INVALID_SIGNATURE, e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (UnsupportedJwtException e) {
            log.debug("ACCESS unsupported", e);
            markError(req, JwtErrorCode.UNSUPPORTED, e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (IllegalArgumentException e) {
            log.debug("ACCESS illegal arg", e);
            markError(req, JwtErrorCode.ILLEGAL_ARGUMENT, e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (JwtException e) {
            log.debug("ACCESS invalid", e);
            markError(req, JwtErrorCode.INVALID, e.getMessage());
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(req, res);
    }

    private String parseBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        boolean hasAuthorization = StringUtils.hasText(authorization);
        if (!hasAuthorization) return null;

        boolean isBearer = authorization.startsWith("Bearer ");
        if(!isBearer) return null;

        return authorization.substring(7); // return token
    }

    private void markError(HttpServletRequest req, JwtErrorCode code, String msg) {
        req.setAttribute(ACCESS_ERR, code);
        if (msg != null) req.setAttribute(ACCESS_ERR_MSG, msg);
    }
}

