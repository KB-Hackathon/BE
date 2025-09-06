package hackathon.kb.chakchak.global.exception.exceptions;

import hackathon.kb.chakchak.global.kafka.dto.Common;
import hackathon.kb.chakchak.global.kafka.dto.LogLevel;
import hackathon.kb.chakchak.global.response.ResponseCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
	private ResponseCode responseCode;
	private LogLevel logLevel;
	private String txId;
	private Common common;

	public BusinessException(ResponseCode responseCode) {
		super(responseCode.getMessage());
		this.responseCode = responseCode;
		this.logLevel = null;
		this.txId = null;
		this.common = null;
	}

	public BusinessException(ResponseCode responseCode, LogLevel logLevel, String txId, Common common) {
		super(responseCode.getMessage());
		this.responseCode = responseCode;
		this.logLevel = logLevel;
		this.txId = txId;
		this.common = common;
	}
}
