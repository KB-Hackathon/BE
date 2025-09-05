package hackathon.kb.chakchak.domain.jwt.filter;

public enum JwtErrorCode {
    EXPIRED,              // 만료됨
    MALFORMED,            // 포맷 오류
    INVALID_SIGNATURE,    // 서명 불일치
    UNSUPPORTED,          // 지원하지 않는 JWT
    ILLEGAL_ARGUMENT,     // 파싱 인자 문제
    LOGGED_OUT,
    INVALID               // 그 외 JwtException
}

