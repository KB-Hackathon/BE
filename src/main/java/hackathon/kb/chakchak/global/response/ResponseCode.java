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
	S3_UPLOAD_FAIL("GEN-006", HttpStatus.INTERNAL_SERVER_ERROR, "S3 이미지 업로드 실패"),
	S3_NOT_FOUND("GEN-007", HttpStatus.INTERNAL_SERVER_ERROR, "이름 기반 S3 이미지 조회 실패"),

	;


	private final String code;
	private final HttpStatus status;
	private final String message;
}