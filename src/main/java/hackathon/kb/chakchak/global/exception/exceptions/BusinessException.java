package hackathon.kb.chakchak.global.exception.exceptions;

import org.springframework.http.HttpStatus;

import hackathon.kb.chakchak.global.response.ResponseCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
	private ResponseCode responseCode;

	public BusinessException(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}
}
