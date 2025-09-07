package hackathon.kb.chakchak.global.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
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
				.status(code.getStatus())
			.message(code.getMessage())
			.data(data)
			.build();
	}

	public static <T> BaseResponse<T> OK() {
		ResponseCode code = ResponseCode.SUCCESS;

		return BaseResponse
			.<T>builder()
			.code(code.getCode())
				.status(code.getStatus())
			.message(code.getMessage())
			.build();
	}

	public static <T> BaseResponse<T> ERROR(ResponseCode rc) {
		return BaseResponse
				.<T>builder()
				.code(rc.getCode())
				.status(rc.getStatus())
				.message(rc.getMessage())
				.data(null)
				.build();
	}

	public static <T> BaseResponse<T> ERROR(ResponseCode rc, String customMessage) {
		return BaseResponse
				.<T>builder()
				.code(rc.getCode())
				.status(rc.getStatus())
				.message(customMessage)
				.data(null)
				.build();
	}

	public static <T> BaseResponse<T> buildResponse(ResponseCode code,T data) {

		return BaseResponse
			.<T>builder()
			.code(code.getCode())
				.status(code.getStatus())
			.message(code.getMessage())
			.data(data)
			.build();
	}

}
