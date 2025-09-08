package hackathon.kb.chakchak.global.health.root;

import hackathon.kb.chakchak.global.response.BaseResponse;
import hackathon.kb.chakchak.global.response.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RootController {

    @GetMapping()
    public BaseResponse<String> rootCheck() {
        return BaseResponse.OK("API Server Running");
    }

    @RequestMapping("/**")
    public BaseResponse<String> fallback(HttpServletRequest request) {
        return BaseResponse.ERROR(ResponseCode.NOT_FOUND, "No static resource: " + request.getRequestURI());
    }
}
