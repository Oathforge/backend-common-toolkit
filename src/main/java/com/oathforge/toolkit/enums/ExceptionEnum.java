package com.oathforge.toolkit.enums;

public enum ExceptionEnum {
	UTL0001("Encryption key is not initialized."), UTL0002("Error deriving encryption key"),
	UTL0006("Required parameter %s is missing"), UTL0007("SHA-256 algorithm not available"),
	UTL0008("The seed must to be a 8 digits number"),

	OLF0001("Maximum attempts made, try again later"),

	SUN0001("Cannot connect with internal service"), SUN0002("Error retrieving service error info"),

	GEO0001("Invalid Geolocation WKT format: %s"),

	MFU0001("The file exceeds the maximum allowed size of %s"),

	S3E0001("The service is temporarily unavailable. Please try again later"),

	IMG0001("File extension must be: .png, .jpeg or .jpg"), IMG0002("File is not a valid image file"),
	IMG0003("File image exceeds max size: %s MiB"), IMG0004("File extension must be: .png, .jpeg, .jpg or .gif"),

	ENC0001("Error encrypting attribute"), ENC0002("Error decrypting attribute"),

	AWS0001("One or more keys cant be invalidated");

	private final String value;

	ExceptionEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
