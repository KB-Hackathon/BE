package hackathon.kb.chakchak.domain.auth.api.controller;

import hackathon.kb.chakchak.domain.auth.service.AuthService;
import hackathon.kb.chakchak.domain.auth.service.dto.TokensResponse;
import hackathon.kb.chakchak.domain.jwt.util.CookieIssuer;
import hackathon.kb.chakchak.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "AUTH API", description = "사용자 관리 API")
public class AuthController {

    private final AuthService authService;
    private final CookieIssuer cookieIssuer;

    @Operation(summary = "토큰 refresh", description = "access token이 만료되면 access token을 refresh 합니다.")
    @PostMapping("/refresh")
    public BaseResponse<SuccessRefreshResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        TokensResponse tokens = authService.refresh(request, response);

        response.addHeader(HttpHeaders.SET_COOKIE, cookieIssuer.build(tokens.getRefreshToken()).toString());

        return BaseResponse.OK(new SuccessRefreshResponse(tokens.getAccessToken()));
    }

    public record SuccessRefreshResponse(String accessToken) {}
}