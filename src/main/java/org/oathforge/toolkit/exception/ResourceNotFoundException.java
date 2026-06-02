package org.oathforge.toolkit.exception;

public class ResourceNotFoundException extends CustomException {
	private static final long serialVersionUID = -7947246883491325687L;

	public ResourceNotFoundException(String code, String message) {
		super(code, message);
	}

}
