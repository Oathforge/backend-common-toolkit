package com.oathforge.toolkit.exception;

public class ServiceUnavailableException extends CustomException {

	private static final long serialVersionUID = 177182254945803048L;

	public ServiceUnavailableException(String code, String message) {
		super(code, message);
	}

}
