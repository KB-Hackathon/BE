package hackathon.kb.chakchak.global.exception.handler;

import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.kafka.dto.Common;
import hackathon.kb.chakchak.global.kafka.dto.LogLevel;
import hackathon.kb.chakchak.global.kafka.producer.KafkaProducer;
import hackathon.kb.chakchak.global.kafka.util.LogMessageMapper;
import hackathon.kb.chakchak.global.response.BaseResponse;
import hackathon.kb.chakchak.global.response.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private final KafkaProducer kafkaProducer;

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<BaseResponse<Void>> handleBusinessException(BusinessException e, HttpServletRequest request) {
		// ---- null 방어 및 기본값 ----
		final ResponseCode rc = (e.getResponseCode() != null) ? e.getResponseCode() : ResponseCode.INTERNAL_SERVER_ERROR;
		final LogLevel level = (e.getLogLevel() != null) ? e.getLogLevel() : LogLevel.ERROR;

		final Common common = (e.getCommon() != null) ? e.getCommon() :
				Common.builder()
						.srcIp(request.getRemoteAddr())
						.apiMethod(request.getMethod())
						.callApiPath(request.getRequestURI())
						.deviceInfo(request.getHeader("user-agent"))
						.retryCount(0)
						.build();

		final String message = (e.getMessage() != null && !e.getMessage().isBlank())
				? e.getMessage() : rc.getMessage();

		// ---- 로깅 (Kafka) ----
		try {
			kafkaProducer.sendToLogTopic(
					LogMessageMapper.buildLogMessage(
							level,
							e.getTxId(),
							rc.getCode(),
							common,
							message
					)
			);
		} catch (Exception ignore) { /* 로깅 실패 무시 */ }

		return ResponseEntity
				.status(rc.getStatus())
				.body(BaseResponse.ERROR(rc, message));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleOtherErrorException(Exception e, HttpServletRequest request) {
		kafkaProducer.sendToLogTopic(LogMessageMapper.buildLogMessage(
			LogLevel.ERROR,
			null,
			"예측하지 못한 에러",
			Common.builder().srcIp(request.getRemoteAddr()).apiMethod(request.getMethod())
				.callApiPath(request.getRequestURI()).deviceInfo(request.getHeader("user-agent"))
				.retryCount(0).build(),
			e.getMessage()
		));
		return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
	}
}
