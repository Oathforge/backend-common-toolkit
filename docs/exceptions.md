# Excepciones Disponibles y ProblemDetail

## Objetivo

La libreria centraliza el manejo de errores para que los microservicios puedan devolver respuestas consistentes ante fallos funcionales, tecnicos o de validacion.

El objetivo no es solo lanzar excepciones custom, sino tambien capturar excepciones habituales del stack Spring y transformarlas en respuestas HTTP uniformes.

## Componentes principales

- Jerarquia de excepciones custom en `exception`.
- Modelo `ProblemDetail` en `exception/model`.
- Handler centralizado en `exception/handler/ProblemDetailExceptionHandler`.
- Enumerado `ExceptionEnum` para codigos y mensajes genericos reutilizables.

## Beneficios

- Homogeneidad de respuestas entre servicios.
- Menor duplicacion de `@ControllerAdvice`.
- Mejor trazabilidad al trabajar con codigos de error consistentes.
- Separacion entre error tecnico interno y contrato HTTP expuesto por API.

## Excepciones custom incluidas

### `BadRequestException`
Se lanza cuando se recibe una solicitud invalida o un dato de entrada no cumple reglas esperadas.

```java
throw new BadRequestException("USR0003", "The request payload is invalid");
```

### `ConflictException`
Se lanza cuando hay un conflicto de estado o de regla funcional.

```java
throw new ConflictException("USR0002", "Email already exists");
```

### `ResourceNotFoundException`
Se lanza cuando no existe el recurso solicitado.

```java
throw new ResourceNotFoundException("USR0001", "User not found for id %s".formatted(id));
```

### `UnauthorizedException`
Se usa cuando la autenticacion no es valida o no esta presente.

```java
throw new UnauthorizedException();
```

### `AccessDeniedException`
Se lanza cuando el usuario esta autenticado pero no tiene permisos suficientes.

```java
throw new AccessDeniedException("SEC0001", "Access denied to this operation");
```

### `InternalServerErrorException`
Se lanza ante fallos internos no recuperables del backend.

```java
throw new InternalServerErrorException("INT0001", "Unexpected internal error");
```

### `ServiceUnavailableException`
Se usa cuando una dependencia externa o infraestructura no esta disponible.

```java
throw new ServiceUnavailableException("EXT0001", "Cloud storage is temporarily unavailable");
```

### `DataIntegrityViolationException`
Se usa para representar violaciones de integridad o restricciones persistentes.

```java
throw new DataIntegrityViolationException("DB0001", "Cannot delete role because it is still assigned");
```

### `IllegalArgumentException`
Se usa cuando se pasa un argumento invalido a una operacion interna.

```java
throw new IllegalArgumentException("ARG0001", "Encryption key is not initialized");
```

### `NotImplementedException`
Se usa cuando una funcionalidad esta definida pero todavia no implementada.

```java
throw new NotImplementedException("GEN0009", "This export format is not implemented yet");
```

### `CustomException`
Es la base comun del resto de excepciones custom con codigo y mensaje.

```java
throw new CustomException("CUS0001", "Custom application error");
```

## Excepciones framework que el handler tambien transforma

`ProblemDetailExceptionHandler` no solo captura excepciones propias de la libreria. Tambien convierte varias excepciones habituales del ecosistema Spring/Jakarta en respuestas uniformes.

### Validacion y binding

- `MethodArgumentNotValidException`
- `ConstraintViolationException`
- `MethodArgumentTypeMismatchException`
- `MissingServletRequestParameterException`
- `HttpMessageNotReadableException`

Estas suelen aparecer cuando un request body, un parametro o una validacion `@Valid` fallan.

### Seguridad

- `org.springframework.security.access.AccessDeniedException`

Se transforma a una respuesta `403 Forbidden` coherente.

### Concurrencia y persistencia

- `OptimisticLockingFailureException`

Se usa para reportar conflictos de concurrencia sobre entidades versionadas.

### Integraciones HTTP

- `WebClientRequestException`
- `WebClientResponseException`

Permiten convertir fallos de clientes HTTP reactivos en errores consistentes.

### Subidas de fichero

- `MaxUploadSizeExceededException`

Se transforma en respuesta controlada cuando el upload supera el tamano maximo permitido.

## Ejemplo de uso en un servicio

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

## Ejemplo de respuesta HTTP resultante

Cuando esa excepcion es interceptada por `ProblemDetailExceptionHandler`, el cliente recibira una respuesta uniforme similar a esta:

```json
{
  "code": "USR0001",
  "message": "User not found for id 123",
  "status": 404,
  "timestamp": "2026-05-05T12:30:00"
}
```

## Ejemplo de activacion en Spring

En la practica no necesitas escribir codigo adicional si el `ControllerAdvice` de la libreria esta en el classpath y forma parte del escaneo del proyecto.

```java
@SpringBootApplication
public class UserServiceApplication {
}
```

## Recomendacion de uso

La libreria aporta la base comun, pero cada microservicio deberia seguir definiendo sus propios codigos de error de dominio. La idea correcta es reutilizar la infraestructura de manejo de errores, no homogeneizar a la fuerza la semantica funcional de todos los servicios.
