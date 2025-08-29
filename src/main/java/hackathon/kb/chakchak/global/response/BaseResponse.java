package hackathon.kb.chakchak.global.response;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;

import lombok.Builder;

@Builder
public class BaseResponse<T> {
	private final String code;
	private final HttpStatus status;
	private final String message;
	private final T data;

	public static <T> BaseResponse<T> OK( T data) {
		ResponseCode code = ResponseCode.SUCCESS;

		return BaseResponse
			.<T>builder()
			.code(code.getCode())
			.message(code.getMessage())
			.data(data)
			.build();
	}

	public static <T> BaseResponse<T> OK() {
		ResponseCode code = ResponseCode.SUCCESS;

		return BaseResponse
			.<T>builder()
			.code(code.getCode())
			.message(code.getMessage())
			.build();
	}

	public static <T> BaseResponse<T> buildResponse(ResponseCode code,T data) {

		return BaseResponse
			.<T>builder()
			.code(code.getCode())
			.message(code.getMessage())
			.data(data)
			.build();
	}

}
