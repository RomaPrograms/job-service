package com.duallab.verapdf.jobservice.model.error.exception;

public class LowDiskSpaceException extends BadRequestException {

	public LowDiskSpaceException() {
	}

	public LowDiskSpaceException(String message) {
		super(message);
	}

	public LowDiskSpaceException(String message, Throwable cause) {
		super(message, cause);
	}

	public LowDiskSpaceException(Throwable cause) {
		super(cause);
	}

	public LowDiskSpaceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
