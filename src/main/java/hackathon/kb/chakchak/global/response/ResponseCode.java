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
	UNAUTHORIZED("GEN-002", HttpStatus.UNAUTHORIZED, "Unauthorized"),
	NOT_FOUND("GEN-003", HttpStatus.NOT_FOUND, "Not Found"),
	CONFLICT("GEN-004", HttpStatus.CONFLICT, "Conflict"),
	INTERNAL_SERVER_ERROR("GEN-005", HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),

	SIGNUP_TOKEN_INVALID("OAUTH-001", HttpStatus.BAD_REQUEST, "Invalid signup token"),
	SIGNUP_TOKEN_EXPIRED("OAUTH-002", HttpStatus.UNAUTHORIZED, "Signup token expired"),
	SIGNUP_NOT_REQUIRED("OAUTH-003", HttpStatus.CONFLICT, "Signup not required");

	private final String code;
	private final HttpStatus status;
	private final String message;
}