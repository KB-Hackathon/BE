package hackathon.kb.chakchak.domain.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtIssuer {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity-seconds}")
    private long accessTtl;

    @Value("${jwt.refresh-token-validity-seconds}")
    private long refreshTtl;

    /** access 토큰 발급 */
    public String createAccessToken(Long memberId, String role) {
        Instant now = Instant.now();
        Date exp = Date.from(now.plus(accessTtl, ChronoUnit.SECONDS)); // 10분

        return Jwts.builder()
                .claim("typ", "access")
                .setSubject(String.valueOf(memberId))
                .claim("role", role)
                .setIssuedAt(Date.from(now))
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    /** refresh 토큰 발급 */
    public String createRefreshToken(Long memberId) {
        Instant now = Instant.now();
        Date exp = Date.from(now.plus(refreshTtl, ChronoUnit.SECONDS)); // 14일

        return Jwts.builder()
                .claim("typ", "refresh")
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(Date.from(now))
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    /** 카카오 회원가입 진행용 토큰(10분) */
    public String createSignupToken(Long kakaoId) {
        Instant now = Instant.now();
        Date exp = Date.from(now.plus(10, ChronoUnit.MINUTES)); // 10분

        return Jwts.builder()
                .claim("typ", "signup")
                .claim("kakaoId", kakaoId)
                .setIssuedAt(Date.from(now))
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    /** claims 파서 */
    public Jws<Claims> parseJws(String token) {
        return Jwts.parserBuilder()
                .setAllowedClockSkewSeconds(60)
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token);
    }
}

