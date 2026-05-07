package com.backendtoolkit.common.exception;

public class BadRequestException extends CustomException {
    private static final long serialVersionUID = 7932234140178286638L;

	public BadRequestException(String code, String message) {
        super(code, message);
    }
}