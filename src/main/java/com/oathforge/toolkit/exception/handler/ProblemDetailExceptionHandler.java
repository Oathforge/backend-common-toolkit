package com.oathforge.toolkit.exception.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.oathforge.toolkit.enums.ExceptionEnum;
import com.oathforge.toolkit.exception.AccessDeniedException;
import com.oathforge.toolkit.exception.BadRequestException;
import com.oathforge.toolkit.exception.ConflictException;
import com.oathforge.toolkit.exception.DataIntegrityViolationException;
import com.oathforge.toolkit.exception.InternalServerErrorException;
import com.oathforge.toolkit.exception.NotImplementedException;
import com.oathforge.toolkit.exception.ResourceNotFoundException;
import com.oathforge.toolkit.exception.ServiceUnavailableException;
import com.oathforge.toolkit.exception.UnauthorizedException;
import com.oathforge.toolkit.exception.model.ProblemDetail;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase ProblemDetailExceptionHandler.
 * 
 * Esta clase es un controlador de asesoramiento global que maneja las
 * excepciones específicas que se lanzan en la aplicación.
 * 
 * Las excepciones manejadas son las siguientes:
 * 
 * BadRequestException: Esta excepción se lanza cuando se recibe una solicitud
 * HTTP mal formada. Ejemplo de lanzamiento: throw new BadRequestException("La
 * solicitud es inválida");
 * 
 * NotFoundException: Esta excepción se lanza cuando no se encuentra un recurso
 * solicitado. Ejemplo de lanzamiento: throw new NotFoundException("Recurso no
 * encontrado");
 * 
 * UnauthorizedException: Esta excepción se lanza cuando un usuario intenta
 * acceder a un recurso sin la autorización adecuada. Ejemplo de lanzamiento:
 * throw new UnauthorizedException("No autorizado");
 * 
 * InternalServerErrorException: Esta excepción se lanza cuando ocurre un error
 * interno en el servidor. Ejemplo de lanzamiento: throw new
 * InternalServerErrorException("Error interno del servidor");
 * 
 * MethodArgumentNotValidException: Esta excepción se lanza cuando la validación
 * de argumentos de método anotados con @Valid falla. Ejemplo de lanzamiento:
 * throw new MethodArgumentNotValidException("Argumento de método no válido");
 * 
 * HttpMessageNotReadableException: Esta excepción se lanza cuando la solicitud
 * HTTP no se puede convertir a un objeto debido a errores de sintaxis. Ejemplo
 * de lanzamiento: throw new HttpMessageNotReadableException("Mensaje HTTP no
 * legible");
 * 
 * HttpMediaTypeNotSupportedException: Esta excepción se lanza cuando el cliente
 * envía una solicitud con un tipo de medio no soportado. Ejemplo de
 * lanzamiento: throw new HttpMediaTypeNotSupportedException("Tipo de medio HTTP
 * no soportado");
 * 
 * AccessDeniedException: Esta excepción se lanza cuando un usuario autenticado
 * intenta acceder a un recurso para el que no tiene permisos. Ejemplo de
 * lanzamiento: throw new AccessDeniedException("Acceso denegado");
 * 
 * NullPointerException: Esta excepción se lanza cuando se intenta utilizar un
 * objeto que no ha sido inicializado (es decir, su valor es null). Ejemplo de
 * lanzamiento: throw new NullPointerException("Referencia nula");
 * 
 * IllegalArgumentException: Esta excepción se lanza cuando se pasa un argumento
 * ilegal o inapropiado a un método. Ejemplo de lanzamiento: throw new
 * IllegalArgumentException("Argumento ilegal");
 * 
 * ResourceNotFoundException: Esta excepción personalizada se puede lanzar
 * cuando no se encuentra un recurso específico. Ejemplo de lanzamiento: throw
 * new ResourceNotFoundException("Recurso no encontrado");
 * 
 * DataIntegrityViolationException: Esta excepción se lanza cuando se viola una
 * restricción de integridad de la base de datos. Ejemplo de lanzamiento: throw
 * new DataIntegrityViolationException("Violación de integridad de datos");
 * 
 * CustomException: Esta excepción personalizada se puede lanzar cuando se
 * necesita una excepción específica de la aplicación que no está cubierta por
 * las excepciones estándar. Ejemplo de lanzamiento: throw new
 * CustomException("Mensaje de error personalizado",
 * HttpStatus.CUSTOM_HTTP_STATUS);
 * 
 * NotImplementedException: Esta excepción se lanza cuando queremos indicar que
 * cierta parte de la aplicación no funciona porque no está implementada
 * 
 * ConstraintViolationException: Esta excepción se lanza cuando se produce un
 * problema de validación con las anotaciones de validación de jakarta
 */
@Slf4j
@ControllerAdvice
public class ProblemDetailExceptionHandler {

	private static final String CODE_EXCEPTION = "ISEXXXX";

	private static final String CODE_METHOD_ARGUMENT_NOT_VALID_EXCEPTION = "GEN0001";

	private static final String CODE_METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION = "GEN0002";

	private static final String CODE_ILLEGAL_ARGUMENT_EXCEPTION = "GEN0003";

	private static final String CONSTRAINT_VIOLATION_EXCEPTION = "GEN0001";

	private static final String ACCESS_DENIED_EXCEPTION_CODE = "ADE0001";

	@Value("${spring.servlet.multipart.max-file-size:1MB}")
	private String maxFileSize;

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ProblemDetail> handleBadRequestException(BadRequestException ex, WebRequest request) {
		log.error("BadRequestException with code: {} and message: {}", ex.getCode(), ex.getMessage());
		return createProblemDetail(ex.getMessage(), ex.getCode(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<Void> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}

	@ExceptionHandler(InternalServerErrorException.class)
	public ResponseEntity<ProblemDetail> handleInternalServerErrorException(InternalServerErrorException ex,
			WebRequest request) {
		log.error("InternalServerErrorException with code: {} and message: {}", ex.getCode(), ex.getMessage());
		return createProblemDetail(ex.getMessage(), ex.getCode(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ProblemDetail> handleConflictException(ConflictException ex, WebRequest request) {
		log.error("ConflictException with code: {} and message: {}", ex.getCode(), ex.getMessage());
		return createProblemDetail(ex.getMessage(), ex.getCode(), HttpStatus.CONFLICT, request);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ProblemDetail> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
		log.error("AccessDeniedException with code: {} and message: {}", ex.getCode(), ex.getMessage());
		return createProblemDetail(ex.getMessage(), ex.getCode(), HttpStatus.FORBIDDEN, request);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ProblemDetail> handleResourceNotFoundException(ResourceNotFoundException ex,
			WebRequest request) {
		log.error("ResourceNotFoundException with code: {} and message: {}", ex.getCode(), ex.getMessage());
		return createProblemDetail(ex.getMessage(), ex.getCode(), HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(ServiceUnavailableException.class)
	public ResponseEntity<ProblemDetail> handleServiceUnavailableException(ServiceUnavailableException ex,
			WebRequest request) {
		log.error("ServiceUnavailableException with code: {} and message: {}", ex.getCode(), ex.getMessage());
		return createProblemDetail(ex.getMessage(), ex.getCode(), HttpStatus.SERVICE_UNAVAILABLE, request);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ProblemDetail> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
			WebRequest request) {
		log.error("DataIntegrityViolationException with code: {} and message: {}", ex.getCode(), ex.getMessage());
		return createProblemDetail(ex.getMessage(), ex.getCode(), HttpStatus.CONFLICT, request);
	}

	@ExceptionHandler(NotImplementedException.class)
	public ResponseEntity<ProblemDetail> handleNotImplementedException(NotImplementedException ex, WebRequest request) {
		log.error("NotImplementedException with code: {} and message: {}", ex.getCode(), ex.getMessage());
		return createProblemDetail(ex.getMessage(), ex.getCode(), HttpStatus.NOT_IMPLEMENTED, request);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex,
			WebRequest request) {

		String errors = ex.getConstraintViolations().stream()
				.map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
				.collect(Collectors.joining(", "));

		log.error("ConstraintViolationException with errors: {}", errors);

		return createProblemDetail(errors, CONSTRAINT_VIOLATION_EXCEPTION, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
			WebRequest request) {
		String errors = ex.getBindingResult().getAllErrors().stream().map(error -> {
			if (error instanceof FieldError fieldError) {
				return fieldError.getField() + ": " + fieldError.getDefaultMessage();
			} else {
				return error.getObjectName() + ": " + error.getDefaultMessage();
			}
		}).collect(Collectors.joining(", "));

		log.error("MethodArgumentNotValidException with errors: {}", errors);

		return createProblemDetail(errors, CODE_METHOD_ARGUMENT_NOT_VALID_EXCEPTION, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
			WebRequest request) {
		String error = String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName());

		log.error("MethodArgumentNotValidException with error: {}", error);
		return createProblemDetail(error, CODE_METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION, HttpStatus.BAD_REQUEST,
				request);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ProblemDetail> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex,
			WebRequest request) {
		log.error("HttpMessageNotReadableException with message: {}", ex.getMessage());
		return createProblemDetail(ex.getMessage(), CODE_METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION,
				HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException ex,
			WebRequest request) {
		log.error("IllegalArgumentException with message: {}", ex.getMessage());
		return createProblemDetail(ex.getMessage(), CODE_ILLEGAL_ARGUMENT_EXCEPTION, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
	public ResponseEntity<ProblemDetail> handleSpringAccessDeniedException(
			org.springframework.security.access.AccessDeniedException ex, WebRequest request) {
		log.error("Spring AccessDeniedException with message: {}", ex.getMessage());
		return createProblemDetail(ex.getMessage(), ACCESS_DENIED_EXCEPTION_CODE, HttpStatus.FORBIDDEN, request);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ProblemDetail> handleMissingServletRequestParameterException(
			MissingServletRequestParameterException ex, WebRequest request) {
		log.error("MissingServletRequestParameterException with message: {}", ex.getMessage());
		return createProblemDetail(ExceptionEnum.UTL0006.getValue().formatted(ex.getParameterName()),
				ExceptionEnum.UTL0006.name(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(OptimisticLockingFailureException.class)
	public ResponseEntity<ProblemDetail> handleObjectOptimisticLockingFailureException(
			OptimisticLockingFailureException ex, WebRequest request) {
		log.error("OptimisticLockingFailureException with message: {}", ex.getMessage());
		return createProblemDetail(ExceptionEnum.OLF0001.getValue(), ExceptionEnum.OLF0001.name(),
				HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	@ExceptionHandler(WebClientResponseException.class)
	public ResponseEntity<ProblemDetail> handleWebClientResponseException(WebClientResponseException ex,
			WebRequest request) {
		log.error("WebClientResponseException with message: {}", ex.getMessage());
		if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		ProblemDetail body = ex.getResponseBodyAs(ProblemDetail.class);

		body = body != null ? body
				: new ProblemDetail(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")),
						HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.name(),
						ExceptionEnum.SUN0002.getValue(), ExceptionEnum.SUN0002.name(),
						request.getDescription(false).replace("uri=", ""));

		return createProblemDetail(body.message(), body.code(), HttpStatus.valueOf(body.status()), request);
	}

	@ExceptionHandler(WebClientRequestException.class)
	public ResponseEntity<ProblemDetail> handleWebClientRequestException(WebClientRequestException ex,
			WebRequest request) {
		log.error("WebClientRequestException with message: {}", ex.getMessage());
		return createProblemDetail(ExceptionEnum.SUN0001.getValue(), ExceptionEnum.SUN0001.name(),
				HttpStatus.SERVICE_UNAVAILABLE, request);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ProblemDetail> handleMaxSizeException(MaxUploadSizeExceededException exc,
			WebRequest request) {
		log.error("MaxUploadSizeExceededException with message: {}", exc.getMessage());
		return createProblemDetail(ExceptionEnum.MFU0001.getValue().formatted(maxFileSize),
				ExceptionEnum.MFU0001.name(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ProblemDetail> handleException(Exception ex, WebRequest request) {
		log.error("Uncontrolled exception: ", ex);
		return createProblemDetail(ex.getMessage(), CODE_EXCEPTION, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	private ResponseEntity<ProblemDetail> createProblemDetail(String message, String code, HttpStatus status,
			WebRequest request) {
		String formattedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
		var problem = new ProblemDetail(formattedTimestamp, status.value(), status.name(), message, code,
				request.getDescription(false).replace("uri=", ""));

		return new ResponseEntity<>(problem, status);
	}

}