package org.oathforge.toolkit.exception;

import org.oathforge.toolkit.enums.ExceptionEnum;

public class ResilientOperationTimeoutException extends ServiceUnavailableException {

	private static final long serialVersionUID = 7621792575052749454L;

	public ResilientOperationTimeoutException(String operationName, String timeout) {
		super(ExceptionEnum.RSL0001.name(), ExceptionEnum.RSL0001.getValue().formatted(operationName, timeout));
	}
}
