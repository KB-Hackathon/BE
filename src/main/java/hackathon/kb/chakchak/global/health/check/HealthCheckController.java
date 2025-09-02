package hackathon.kb.chakchak.global.health.check;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.kafka.dto.Common;
import hackathon.kb.chakchak.global.kafka.dto.LogLevel;
import hackathon.kb.chakchak.global.response.BaseResponse;
import hackathon.kb.chakchak.global.response.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

	@GetMapping
	public BaseResponse<String> healthCheck() {
		return BaseResponse.OK("health check");
	}

	@GetMapping("/error")
	public BaseResponse<String> healthCheckError(HttpServletRequest request) {
		throw new BusinessException(ResponseCode.SUCCESS, LogLevel.ERROR, null,
			Common.builder()
				.srcIp(request.getRemoteAddr())
				.apiMethod(request.getMethod())
				.deviceInfo(request.getHeader("user-agent"))
				.callApiPath(request.getRequestURI())
				.retryCount(0)
				.build());
	}
}
