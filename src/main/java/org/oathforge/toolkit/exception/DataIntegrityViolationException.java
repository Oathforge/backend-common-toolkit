package org.oathforge.toolkit.exception;

public class DataIntegrityViolationException extends CustomException {
	private static final long serialVersionUID = -5816403635505944645L;

	public DataIntegrityViolationException(String code, String message) {
		super(code, message);
	}

}
