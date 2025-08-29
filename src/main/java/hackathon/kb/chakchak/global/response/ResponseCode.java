package hackathon.kb.chakchak.global.response;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

	// GENERAL
	SUCCESS("GEN-000", HttpStatus.OK, "Success"),
	BAD_REQUEST("GEN-001", HttpStatus.BAD_REQUEST, "Bad Request"),
	UNAUTHORIZED("GEN-002", HttpStatus.UNAUTHORIZED, "Unauthorized");

	private final String code;
	private final HttpStatus status;
	private final String message;
}