package br.com.evandrorenan.web3270.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionWeb3270 extends Exception {
	private static final long serialVersionUID = 6227395943875919783L;
	private final Date timestamp = Calendar.getInstance().getTime();
	private final String message;
	private final String details;

	private static final Logger logger = LoggerFactory.getLogger(ExceptionWeb3270.class);

	public ExceptionWeb3270(String message, String details, Exception e) {
		this.message = message;
		this.details = details;

		if (e == null ) {
			return;
		}
		
		logger.error(e.getMessage());
		logger.error(e.getLocalizedMessage());
		Writer buffer = new StringWriter();
		PrintWriter pw = new PrintWriter(buffer);
		e.printStackTrace(pw);
		String strBuffer = buffer.toString();
		logger.error("Stack trace: %s", strBuffer);
	}

	@Override
	public String toString() {
		String exception = "Message: " +  this.message + ", ";
		exception += "details: " + this.details + ", ";
		exception += "timestamp: " + this.timestamp.toString() + ", ";
		return exception;
	}
}