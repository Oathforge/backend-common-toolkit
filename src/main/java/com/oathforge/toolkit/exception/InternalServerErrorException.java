package com.oathforge.toolkit.exception;

public class InternalServerErrorException extends CustomException {
    private static final long serialVersionUID = 6230224331680180374L;

	public InternalServerErrorException(String code, String message) {
        super(code, message);
    }
}