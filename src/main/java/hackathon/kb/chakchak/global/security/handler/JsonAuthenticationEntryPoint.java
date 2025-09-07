package hackathon.kb.chakchak.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import hackathon.kb.chakchak.domain.jwt.filter.JwtAuthenticationFilter;
import hackathon.kb.chakchak.global.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String code = (String) request.getAttribute(JwtAuthenticationFilter.ACCESS_ERR_CODE);
        String msg  = (String) request.getAttribute(JwtAuthenticationFilter.ACCESS_ERR_MSG);
        HttpStatus status = (HttpStatus) request.getAttribute(JwtAuthenticationFilter.ACCESS_ERR_STATUS);

        if (code == null) code = "UNAUTHORIZED";
        if (msg  == null) msg  = "Authentication required";
        if (status == null) status = HttpStatus.UNAUTHORIZED;

        response.setStatus(status.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        BaseResponse<Object> body = BaseResponse.ERROR(code, status, msg, null);
        om.writeValue(response.getWriter(), body);
    }
}

