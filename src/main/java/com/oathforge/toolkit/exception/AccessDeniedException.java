package com.oathforge.toolkit.exception;

public class AccessDeniedException extends CustomException {

	private static final long serialVersionUID = -5580636728554107735L;

	public AccessDeniedException(String code, String message) {
		super(code, message);
	}
}
