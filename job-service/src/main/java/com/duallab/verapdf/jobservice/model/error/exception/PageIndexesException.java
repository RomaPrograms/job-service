package com.duallab.verapdf.jobservice.model.error.exception;

/**
 * @author Maksim Bezrukov
 */
public class PageIndexesException extends BadRequestException {

	public PageIndexesException() {
	}

	public PageIndexesException(String message) {
		super(message);
	}

	public PageIndexesException(String message, Throwable cause) {
		super(message, cause);
	}

	public PageIndexesException(Throwable cause) {
		super(cause);
	}

	public PageIndexesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
