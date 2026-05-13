package com.oathforge.toolkit.exception;

public class IllegalArgumentException extends CustomException {
	private static final long serialVersionUID = -6741343019827555825L;

	public IllegalArgumentException(String code, String message) {
		super(code, message);
	}

}
