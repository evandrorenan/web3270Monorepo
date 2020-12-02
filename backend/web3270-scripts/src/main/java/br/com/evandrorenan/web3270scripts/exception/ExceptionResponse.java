package br.com.evandrorenan.web3270scripts.exception;

import java.util.Calendar;
import java.util.Date;

public class ExceptionResponse extends RuntimeException {
	private static final long serialVersionUID = 6227395943875919783L;
	private final Date timestamp = Calendar.getInstance().getTime();
	private final String message;
	private final String details;

	public ExceptionResponse(String message, String details) {
		this.message = message;
		this.details = details;
	}

	@Override
	public String toString() {
		return "ExceptionResponse(timestamp=" + this.timestamp + ", message=" + this.message + ", details="
				+ this.details + ")";
	}
}