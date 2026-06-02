package org.oathforge.toolkit.exception;

public class CustomException extends RuntimeException {
	private static final long serialVersionUID = 2509149271511102225L;
	private final String code;

	public CustomException(String code, String message) {
		super(message);
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}