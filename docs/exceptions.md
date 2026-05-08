# Available Exceptions and ProblemDetail

## Purpose

The library centralizes error handling so that microservices can return consistent responses for functional, technical, or validation failures.

The goal is not only to throw custom exceptions, but also to capture common exceptions from the Spring stack and transform them into uniform HTTP responses.

## Main components

- Custom exception hierarchy in `exception`.
- `ProblemDetail` model in `exception/model`.
- Centralized handler in `exception/handler/ProblemDetailExceptionHandler`.
- `ExceptionEnum` for reusable generic codes and messages.

## Benefits

- Consistent responses across services.
- Less duplication of `@ControllerAdvice`.
- Better traceability through consistent error codes.
- Separation between internal technical errors and the HTTP contract exposed through the API.

## Included custom exceptions

### `BadRequestException`
Thrown when an invalid request is received or an input value does not meet expected rules.

```java
throw new BadRequestException("USR0003", "The request payload is invalid");
```

### `ConflictException`
Thrown when there is a state conflict or a business rule conflict.

```java
throw new ConflictException("USR0002", "Email already exists");
```

### `ResourceNotFoundException`
Thrown when the requested resource does not exist.

```java
throw new ResourceNotFoundException("USR0001", "User not found for id %s".formatted(id));
```

### `UnauthorizedException`
Used when authentication is invalid or missing.

```java
throw new UnauthorizedException();
```

### `AccessDeniedException`
Thrown when the user is authenticated but does not have sufficient permissions.

```java
throw new AccessDeniedException("SEC0001", "Access denied to this operation");
```

### `InternalServerErrorException`
Thrown for non-recoverable internal backend failures.

```java
throw new InternalServerErrorException("INT0001", "Unexpected internal error");
```

### `ServiceUnavailableException`
Used when an external dependency or infrastructure is unavailable.

```java
throw new ServiceUnavailableException("EXT0001", "Cloud storage is temporarily unavailable");
```

### `DataIntegrityViolationException`
Used to represent integrity violations or persistent constraints.

```java
throw new DataIntegrityViolationException("DB0001", "Cannot delete role because it is still assigned");
```

### `IllegalArgumentException`
Used when an invalid argument is passed to an internal operation.

```java
throw new IllegalArgumentException("ARG0001", "Encryption key is not initialized");
```

### `NotImplementedException`
Used when a feature is defined but not yet implemented.

```java
throw new NotImplementedException("GEN0009", "This export format is not implemented yet");
```

### `CustomException`
This is the common base class for the other custom exceptions with code and message.

```java
throw new CustomException("CUS0001", "Custom application error");
```

## Framework exceptions also transformed by the handler

`ProblemDetailExceptionHandler` not only captures exceptions from the library itself. It also converts several common exceptions from the Spring/Jakarta ecosystem into uniform responses.

### Validation and binding

- `MethodArgumentNotValidException`
- `ConstraintViolationException`
- `MethodArgumentTypeMismatchException`
- `MissingServletRequestParameterException`
- `HttpMessageNotReadableException`

These usually appear when a request body, parameter, or `@Valid` validation fails.

### Security

- `org.springframework.security.access.AccessDeniedException`

It is transformed into a consistent `403 Forbidden` response.

### Concurrency and persistence

- `OptimisticLockingFailureException`

Used to report concurrency conflicts on versioned entities.

### HTTP integrations

- `WebClientRequestException`
- `WebClientResponseException`

These allow reactive HTTP client failures to be converted into consistent errors.

### File uploads

- `MaxUploadSizeExceededException`

It is transformed into a controlled response when an upload exceeds the maximum allowed size.

## Example usage in a service

```java
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public User getById(String id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            "USR0001",
            "User not found for id %s".formatted(id)));
  }
}
```

## Example resulting HTTP response

When that exception is intercepted by `ProblemDetailExceptionHandler`, the client receives a uniform response similar to this:

```json
{
  "code": "USR0001",
  "message": "User not found for id 123",
  "status": 404,
  "timestamp": "2026-05-05T12:30:00"
}
```

## Example Spring activation

In practice, you do not need to write any extra code if the library `ControllerAdvice` is on the classpath and part of the project scan.

```java
@SpringBootApplication
public class UserServiceApplication {
}
```

## Usage recommendation

The library provides the shared infrastructure, but each microservice should still define its own domain-specific error codes. The right idea is to reuse the error-handling infrastructure, not to force every service into the same business semantics.
