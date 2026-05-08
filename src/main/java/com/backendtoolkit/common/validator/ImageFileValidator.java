package com.backendtoolkit.common.validator;

import java.io.IOException;
import java.util.Set;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.backendtoolkit.common.enums.ExceptionEnum;
import com.backendtoolkit.common.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

/**
 * Validates uploaded image files and public image URLs using configurable
 * validation rules.
 * <p>
 * This component is intended for services that want to centralize extension,
 * MIME type, and file size checks without hardcoding those rules locally.
 */
@Component
@RequiredArgsConstructor
public class ImageFileValidator {

	private static final long BYTES_IN_MB = 1024 * 1024;

	private final ImageValidationProperties properties;

	/**
	 * Validates an uploaded image using the default policy configured through
	 * {@link ImageValidationProperties}.
	 * <p>
	 * The validation checks both the file extension and the detected MIME type.
	 *
	 * @param file file to validate
	 */
	public void isValidImageExtension(MultipartFile file) {
		validateImage(file, properties.getAllowedExtensions(), properties.getAllowedMimeTypes(),
				ExceptionEnum.IMG0001);
	}

	/**
	 * Validates an uploaded image using an explicit validation policy provided by
	 * the caller.
	 * <p>
	 * This is useful when a service needs a case-specific rule set that differs
	 * from the default configuration.
	 *
	 * @param file file to validate
	 * @param allowedExts allowed extensions for the operation
	 * @param allowedMimeTypes allowed MIME types for the operation
	 */
	public void validateImage(MultipartFile file, Set<String> allowedExts, Set<String> allowedMimeTypes) {
		validateImage(file, allowedExts, allowedMimeTypes, ExceptionEnum.IMG0001);
	}

	private void validateImage(MultipartFile file, Set<String> allowedExts, Set<String> allowedMimes,
			ExceptionEnum extensionException) {
		if (file != null) {
			String fileName = file.getOriginalFilename();

			if (fileName == null || !fileName.contains(".")) {
				throw new BadRequestException(extensionException.name(), extensionException.getValue());
			}

			String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

			if (!allowedExts.contains(extension)) {
				throw new BadRequestException(extensionException.name(), extensionException.getValue());
			}

			Tika tika = new Tika();
			String mimeType;
			try {
				mimeType = tika.detect(file.getInputStream());
			} catch (IOException e) {
				throw new BadRequestException(ExceptionEnum.IMG0002.name(), ExceptionEnum.IMG0002.getValue());
			}

			if (!allowedMimes.contains(mimeType)) {
				throw new BadRequestException(ExceptionEnum.IMG0002.name(), ExceptionEnum.IMG0002.getValue());
			}
		}
	}

	/**
	 * Validates file size using the configured maximum size defined in
	 * {@link ImageValidationProperties}.
	 *
	 * @param file file to validate
	 */
	public void validateFileSize(MultipartFile file) {
		validateFileSize(file, properties.getMaxFileSizeMb());
	}

	/**
	 * Validates file size against the provided maximum size in megabytes.
	 * <p>
	 * This overload is useful when a service needs a one-off size limit that
	 * should not replace the shared default configuration.
	 *
	 * @param file file to validate
	 * @param maxFileSizeMB maximum allowed size in megabytes
	 */
	public void validateFileSize(MultipartFile file, long maxFileSizeMB) {
		long fileSizeInBytes = file.getSize();
		long maxFileSizeInBytes = maxFileSizeMB * BYTES_IN_MB;

		if (fileSizeInBytes > maxFileSizeInBytes) {
			throw new BadRequestException(ExceptionEnum.IMG0003.name(),
					ExceptionEnum.IMG0003.getValue().formatted(maxFileSizeMB));
		}
	}

	/**
	 * Validates that the provided image URL ends with one of the configured image
	 * extensions.
	 * <p>
	 * This method is useful when a service stores or receives public image URLs
	 * and still wants to enforce the same extension policy used for uploads.
	 *
	 * @param url image URL to validate
	 */
	public void isValidImageUrl(String url) {
		String fileExtension = getFileExtensionFromUrl(url);

		if (!properties.getAllowedExtensions().contains(fileExtension)) {
			throw new BadRequestException(ExceptionEnum.IMG0001.name(), ExceptionEnum.IMG0001.getValue());
		}
	}

	private String getFileExtensionFromUrl(String url) {
		int lastDotIndex = url.lastIndexOf('.');
		if (lastDotIndex == -1 || lastDotIndex == url.length() - 1) {
			throw new BadRequestException(ExceptionEnum.IMG0001.name(), ExceptionEnum.IMG0001.getValue());
		}

		return url.substring(lastDotIndex + 1).toLowerCase();
	}

}
