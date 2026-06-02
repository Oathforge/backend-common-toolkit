package org.oathforge.toolkit.exception;

import org.oathforge.toolkit.enums.ExceptionEnum;

public class ResilientOperationRetryExhaustedException extends ServiceUnavailableException {

	private static final long serialVersionUID = -2391667813063607279L;

	public ResilientOperationRetryExhaustedException(String operationName, int maxAttempts, Throwable cause) {
		super(ExceptionEnum.RSL0002.name(),
				ExceptionEnum.RSL0002.getValue().formatted(operationName, maxAttempts));
		initCause(cause);
	}
}
