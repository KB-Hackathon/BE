package hackathon.kb.chakchak.global.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import hackathon.kb.chakchak.global.kafka.dto.LogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

	@Value("${spring.kafka.log-topic}")
	private String logTopic;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final KafkaTemplate<String, String> kafkaTemplate;

	public void sendToLogTopic(LogMessage logMessage) {
		try {
			String message = objectMapper.writeValueAsString(logMessage);
			kafkaTemplate.send(logTopic, message);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
