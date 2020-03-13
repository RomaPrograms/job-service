package com.duallab.verapdf.jobservice.model.error.exception;

/**
 * @author Maksim Bezrukov
 */
public class VeraPDFBackEndException extends Exception {

	public VeraPDFBackEndException() {
	}

	public VeraPDFBackEndException(String message) {
		super(message);
	}

	public VeraPDFBackEndException(String message, Throwable cause) {
		super(message, cause);
	}

	public VeraPDFBackEndException(Throwable cause) {
		super(cause);
	}

	public VeraPDFBackEndException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
