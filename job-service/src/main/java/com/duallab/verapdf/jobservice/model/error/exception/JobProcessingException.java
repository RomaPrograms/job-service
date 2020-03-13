package com.duallab.verapdf.jobservice.model.error.exception;

import com.duallab.verapdf.jobservice.model.entity.enums.JobError;

public class JobProcessingException extends VeraPDFBackEndException {

	private JobError errorType;

	public JobProcessingException(JobError errorType) {
		this.errorType = errorType;
	}

	public JobProcessingException(JobError errorType, String message) {
		super(message);
		this.errorType = errorType;
	}

	public JobProcessingException(JobError errorType, String message, Throwable cause) {
		super(message, cause);
		this.errorType = errorType;
	}

	public JobProcessingException(JobError errorType, Throwable cause) {
		super(cause);
		this.errorType = errorType;
	}

	public JobProcessingException(JobError errorType, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.errorType = errorType;
	}

	public static JobProcessingException handle(Throwable e) {
		return e instanceof JobProcessingException
		       ? (JobProcessingException) e
		       : new JobProcessingException(JobError.INTERNAL_ERROR, e.getMessage());
	}

	public JobError getErrorType() {
		return errorType;
	}
}
