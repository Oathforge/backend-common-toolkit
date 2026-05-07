# Validaciones Compartidas

## Objetivo

La libreria incluye validaciones reutilizables para evitar reescribir reglas comunes de entrada en cada microservicio.

## Passwords

El bloque `ValidPassword` y su validador asociado permiten aplicar una politica comun de contraseñas.

Actualmente se contemplan reglas como:
- longitud minima y maxima
- al menos una mayuscula
- al menos un digito
- al menos un caracter especial
- exclusion de espacios o acentos

## Ejemplo de uso con DTO

```java
public record ChangePasswordRequest(
    @ValidPassword String newPassword
) {
}
```

```java
@PostMapping("/password")
public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
  service.changePassword(request.newPassword());
  return ResponseEntity.noContent().build();
}
```

## UUID

La libreria incluye `@ValidUuid` para validar identificadores UUID en formato string.

No se limita a comprobar que el valor tenga 36 caracteres. Valida tambien el formato canonico:
- `8-4-4-4-12`
- solo caracteres hexadecimales validos

## Ejemplo de uso con DTO

```java
public record FindUserRequest(
    @ValidUuid String userId
) {
}
```

## Ejemplo de uso en path variable

```java
@GetMapping(\"/{userId}\")
public UserResponse getUser(@PathVariable @ValidUuid String userId) {
  return service.getUser(userId);
}
```

## Imagenes

`ImageFileValidator` aporta validaciones sobre:
- extension permitida
- tipo MIME detectado
- tamano maximo de fichero
- validacion basica de URLs de imagen

## Configuracion por propiedades

La politica de validacion de imagen ya no queda fijada a fuego dentro de la libreria. Se configura mediante propiedades externas:

```yml
backend-toolkit:
  validation:
    image:
      allowed-extensions: "png,jpeg,jpg"
      allowed-mime-types: "image/png,image/jpeg,image/jpg"
      max-file-size-mb: 5
```

Las propiedades disponibles son:
- `allowed-extensions`
- `allowed-mime-types`
- `max-file-size-mb`

## Ejemplo de uso en upload

```java
@Service
@RequiredArgsConstructor
public class AvatarService {

  private final ImageFileValidator imageFileValidator;

  public void uploadAvatar(MultipartFile file) {
    imageFileValidator.isValidImageExtension(file);
    imageFileValidator.validateFileSize(file);

    // continuar con almacenamiento
  }
}
```

## Ejemplo de validacion de URL publica

```java
public void validateCurrentImage(String imageUrl) {
  imageFileValidator.isValidImageUrl(imageUrl);
}
```

## Ejemplo de politica explicita para otro caso de uso

Si un servicio necesita una politica diferente a la configurada por defecto, puede pasarla de forma explicita sin depender de un metodo con nombre especifico.

```java
public void uploadAnimatedImage(MultipartFile file) {
  imageFileValidator.validateImage(
      file,
      Set.of("png", "jpeg", "jpg", "gif"),
      Set.of("image/png", "image/jpeg", "image/jpg", "image/gif"));

  imageFileValidator.validateFileSize(file);
}
```

Ese enfoque mantiene la libreria generica y evita nombres acoplados a un formato concreto.

## Beneficios

- Consistencia entre servicios.
- Menor duplicacion de reglas.
- Mensajes de error controlados y unificados.

## Recomendacion

Todo validador que entre aqui deberia cumplir dos condiciones:
- ser tecnicamente generico
- tener potencial real de reutilizacion entre varios servicios
