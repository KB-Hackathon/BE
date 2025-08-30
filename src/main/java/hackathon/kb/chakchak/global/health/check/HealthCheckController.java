package hackathon.kb.chakchak.global.health.check;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hackathon.kb.chakchak.global.response.BaseResponse;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

	@GetMapping
	public BaseResponse<String> healthCheck() {
		return BaseResponse.OK("health check");
	}
}
