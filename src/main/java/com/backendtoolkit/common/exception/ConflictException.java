package com.backendtoolkit.common.exception;

public class ConflictException extends CustomException {

	private static final long serialVersionUID = -9166963102306154142L;

	public ConflictException(String code, String message) {
		super(code, message);
	}
}
