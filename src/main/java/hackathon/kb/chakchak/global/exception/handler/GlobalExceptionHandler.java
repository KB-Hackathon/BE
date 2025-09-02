package hackathon.kb.chakchak.global.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import hackathon.kb.chakchak.global.exception.exceptions.BusinessException;
import hackathon.kb.chakchak.global.kafka.dto.Common;
import hackathon.kb.chakchak.global.kafka.producer.KafkaProducer;
import hackathon.kb.chakchak.global.kafka.util.LogMessageMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private final KafkaProducer kafkaProducer;

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<String> handleBusinessException(BusinessException e, HttpServletRequest request) {
		kafkaProducer.sendToLogTopic(LogMessageMapper.buildLogMessage(
			e.getLogLevel(),
			e.getTxId(),
			e.getResponseCode().getMessage(),
			Common.builder().srcIp(request.getRemoteAddr()).apiMethod(request.getMethod())
				.callApiPath(request.getRequestURI()).deviceInfo(request.getHeader("user-agent"))
				.retryCount(0).build(),
			e.getMessage()
		));
		return ResponseEntity.status(e.getResponseCode().getStatus()).body(e.getMessage());
	}
}
