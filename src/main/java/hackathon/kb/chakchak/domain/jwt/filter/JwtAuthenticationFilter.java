package hackathon.kb.chakchak.domain.jwt.filter;

import hackathon.kb.chakchak.domain.jwt.util.JwtIssuer;
import hackathon.kb.chakchak.domain.jwt.util.CookieIssuer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtIssuer jwtIssuer;

    private static final String ATTR_ACCESS_ERR   = "JWT_ACCESS_ERROR_CODE";
    private static final String ATTR_ACCESS_ERR_MSG = "JWT_ACCESS_ERROR_MSG";
    private static final String ATTR_REFRESH_ERR  = "JWT_REFRESH_ERROR_CODE";
    private static final String ATTR_REFRESH_ERR_MSG = "JWT_REFRESH_ERROR_MSG";
    private static final String ATTR_SUB  = "JWT_SUBJECT";    // memberId
    private static final String ATTR_ROLE = "JWT_ROLE";       // role(선택)

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String accessToken = parseBearerToken(req);
        log.info("검증할 access token 값 입니다: {}", accessToken);

        if (accessToken != null) {
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
                    markError(req, true, JwtErrorCode.INVALID, "Not an access token (typ mismatch)");
                }

            } catch (ExpiredJwtException e) {
                log.debug("ACCESS expired", e);
                markError(req, true, JwtErrorCode.EXPIRED, e.getMessage());
                SecurityContextHolder.clearContext();
            } catch (MalformedJwtException e) {
                log.debug("ACCESS malformed", e);
                markError(req, true, JwtErrorCode.MALFORMED, e.getMessage());
                SecurityContextHolder.clearContext();
            } catch (SignatureException e) {
                log.debug("ACCESS bad signature", e);
                markError(req, true, JwtErrorCode.INVALID_SIGNATURE, e.getMessage());
                SecurityContextHolder.clearContext();
            } catch (UnsupportedJwtException e) {
                log.debug("ACCESS unsupported", e);
                markError(req, true, JwtErrorCode.UNSUPPORTED, e.getMessage());
                SecurityContextHolder.clearContext();
            } catch (IllegalArgumentException e) {
                log.debug("ACCESS illegal arg", e);
                markError(req, true, JwtErrorCode.ILLEGAL_ARGUMENT, e.getMessage());
                SecurityContextHolder.clearContext();
            } catch (JwtException e) {
                log.debug("ACCESS invalid", e);
                markError(req, true, JwtErrorCode.INVALID, e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        String refreshToken = extractRefreshCookie(req);
        if (refreshToken != null) {
            try {
                Claims claims = jwtIssuer.parseJws(refreshToken).getBody();

                if (!"refresh".equals(claims.get("typ", String.class))) {
                    markError(req, false, JwtErrorCode.INVALID, "Not a refresh token (typ mismatch)");
                } else {
                    // 컨트롤러(/auth/refresh)에서 쓰라고 속성에 심어둠
                    req.setAttribute(ATTR_SUB, claims.getSubject());
                    req.setAttribute(ATTR_ROLE, claims.get("role", String.class)); // 없을 수도
                }
            } catch (ExpiredJwtException e) {
                log.debug("REFRESH expired", e);
                markError(req, false, JwtErrorCode.EXPIRED, e.getMessage());
            } catch (MalformedJwtException e) {
                log.debug("REFRESH malformed", e);
                markError(req, false, JwtErrorCode.MALFORMED, e.getMessage());
            } catch (SignatureException e) {
                log.debug("REFRESH bad signature", e);
                markError(req, false, JwtErrorCode.INVALID_SIGNATURE, e.getMessage());
            } catch (UnsupportedJwtException e) {
                log.debug("REFRESH unsupported", e);
                markError(req, false, JwtErrorCode.UNSUPPORTED, e.getMessage());
            } catch (IllegalArgumentException e) {
                log.debug("REFRESH illegal arg", e);
                markError(req, false, JwtErrorCode.ILLEGAL_ARGUMENT, e.getMessage());
            } catch (JwtException e) {
                log.debug("REFRESH invalid", e);
                markError(req, false, JwtErrorCode.INVALID, e.getMessage());
            }
        }
        chain.doFilter(req, res);
    }

    private String extractRefreshCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        log.info("cookies: {}", cookies);

        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (CookieIssuer.REFRESH_TOKEN.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    private String parseBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        boolean hasAuthorization = StringUtils.hasText(authorization);
        if (!hasAuthorization) return null;

        boolean isBearer = authorization.startsWith("Bearer ");
        if(!isBearer) return null;

        return authorization.substring(7); // return token
    }

    private void markError(HttpServletRequest req, boolean accessToken, JwtErrorCode code, String msg) {
        if (accessToken) {
            req.setAttribute(ATTR_ACCESS_ERR, code);
            if (msg != null) req.setAttribute(ATTR_ACCESS_ERR_MSG, msg);
        } else {
            req.setAttribute(ATTR_REFRESH_ERR, code);
            if (msg != null) req.setAttribute(ATTR_REFRESH_ERR_MSG, msg);
        }
    }
}

