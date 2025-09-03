package hackathon.kb.chakchak.domain.jwt.filter;

public enum JwtErrorCode {
    MISSING,              // Authorization 헤더 없음
    EXPIRED,              // 만료됨
    MALFORMED,            // 포맷 오류
    INVALID_SIGNATURE,    // 서명 불일치
    UNSUPPORTED,          // 지원하지 않는 JWT
    ILLEGAL_ARGUMENT,     // 파싱 인자 문제
    INVALID               // 그 외 JwtException
}

