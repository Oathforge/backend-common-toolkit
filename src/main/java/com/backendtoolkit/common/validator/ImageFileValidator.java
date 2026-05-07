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
 * Utilidad para validar ficheros e URLs de imágenes.
 * <p>
 * Proporciona métodos para:
 * <ul>
 * <li>Verificar extensión y tipo MIME de imágenes (PNG/JPG).</li>
 * <li>Comprobar el tamaño máximo permitido de un fichero.</li>
 * <li>Validar la extensión de una URL de imagen.</li>
 * </ul>
 * <p>
 * Lanzará {@link BadRequestException} con códigos de error:
 * <ul>
 * <li>IMG0001: extensión inválida o ausencia de extensión.</li>
 * <li>IMG0002: tipo MIME detectado inválido o error de lectura.</li>
 * <li>IMG0003: tamaño de fichero superior al máximo permitido.</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class ImageFileValidator {

	private static final long BYTES_IN_MB = 1024 * 1024;

	private final ImageValidationProperties properties;

	/**
	 * Valida que el fichero upload sea una imagen con extensión y tipo MIME
	 * adecuados (PNG, JPEG o JPG).
	 *
	 * @param file el fichero a validar; si es null, no hace nada.
	 * @throws BadRequestException si la extensión o el tipo MIME no están en los
	 *                             valores permitidos.
	 */
	public void isValidImageExtension(MultipartFile file) {
		validateImage(file, properties.getAllowedExtensions(), properties.getAllowedMimeTypes(),
				ExceptionEnum.IMG0001);
	}

	/**
	 * Valida que el fichero upload sea una imagen con extensión y tipo MIME
	 * adecuados usando una politica de validacion explicita.
	 *
	 * @param file             el fichero a validar; si es null, no hace nada.
	 * @param allowedExts      conjunto de extensiones validas para esta operacion.
	 * @param allowedMimeTypes conjunto de tipos MIME validos para esta operacion.
	 * @throws BadRequestException si la extensión o el tipo MIME no están en los
	 *                             valores permitidos.
	 */
	public void validateImage(MultipartFile file, Set<String> allowedExts, Set<String> allowedMimeTypes) {
		validateImage(file, allowedExts, allowedMimeTypes, ExceptionEnum.IMG0001);
	}

	/**
	 * Método interno que centraliza la validación de extensión y tipo MIME en
	 * función de los conjuntos permitidos.
	 *
	 * @param file         el fichero a validar.
	 * @param allowedExts  conjunto de extensiones válidas.
	 * @param allowedMimes conjunto de tipos MIME válidos.
	 * @param extensionException error a devolver cuando la extension no es valida.
	 * @throws BadRequestException si no cumple con la extensión o el MIME.
	 */
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
	 * Valida que el tamaño del fichero no supere el límite indicado en MB.
	 *
	 * @param file          el fichero a comprobar.
	 * @param maxFileSizeMB límite máximo en megabytes.
	 * @throws BadRequestException si el tamaño supera maxFileSizeMB.
	 */
	public void validateFileSize(MultipartFile file) {
		validateFileSize(file, properties.getMaxFileSizeMb());
	}

	/**
	 * Valida que el tamaño del fichero no supere el límite indicado en MB.
	 *
	 * @param file          el fichero a comprobar.
	 * @param maxFileSizeMB límite máximo en megabytes.
	 * @throws BadRequestException si el tamaño supera maxFileSizeMB.
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
	 * Valida que la URL dada apunte a un fichero con extensión válida (PNG, JPEG o
	 * JPG).
	 *
	 * @param url la URL de la imagen.
	 * @throws BadRequestException si la extensión no está en VALID_EXTENSIONS.
	 */
	public void isValidImageUrl(String url) {
		String fileExtension = getFileExtensionFromUrl(url);

		if (!properties.getAllowedExtensions().contains(fileExtension)) {
			throw new BadRequestException(ExceptionEnum.IMG0001.name(), ExceptionEnum.IMG0001.getValue());
		}
	}

	/**
	 * Extrae la extensión de fichero de una URL o nombre.
	 *
	 * @param url la cadena que contiene el nombre o la URL.
	 * @return la extensión en minúsculas.
	 * @throws BadRequestException si no hay extensión o está malformada.
	 */
	private String getFileExtensionFromUrl(String url) {
		int lastDotIndex = url.lastIndexOf('.');
		if (lastDotIndex == -1 || lastDotIndex == url.length() - 1) {
			throw new BadRequestException(ExceptionEnum.IMG0001.name(), ExceptionEnum.IMG0001.getValue());
		}

		return url.substring(lastDotIndex + 1).toLowerCase();
	}

}
