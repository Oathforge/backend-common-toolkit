# Shared Validation

## Purpose

The library includes reusable validations to avoid rewriting common input rules in every microservice.

## Passwords

The `ValidPassword` block and its associated validator allow you to apply a shared password policy.

It currently includes rules such as:
- minimum and maximum length
- at least one uppercase character
- at least one digit
- at least one special character
- exclusion of spaces or accents

## Example usage with DTO

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

The library includes `@ValidUuid` to validate UUID identifiers in string format.

It does not only check that the value has 36 characters. It also validates the canonical format:
- `8-4-4-4-12`
- only valid hexadecimal characters

## Example usage with DTO

```java
public record FindUserRequest(
    @ValidUuid String userId
) {
}
```

## Example usage in a path variable

```java
@GetMapping("/{userId}")
public UserResponse getUser(@PathVariable @ValidUuid String userId) {
  return service.getUser(userId);
}
```

## Images

`ImageFileValidator` provides validation for:
- allowed file extension
- detected MIME type
- maximum file size
- basic image URL validation

## Property-based configuration

The image validation policy is not hardcoded inside the library. It is configured through external properties:

```yml
backend-toolkit:
  validation:
    image:
      allowed-extensions: "png,jpeg,jpg"
      allowed-mime-types: "image/png,image/jpeg,image/jpg"
      max-file-size-mb: 5
```

Available properties:
- `allowed-extensions`
- `allowed-mime-types`
- `max-file-size-mb`

## Example usage for uploads

```java
@Service
@RequiredArgsConstructor
public class AvatarService {

  private final ImageFileValidator imageFileValidator;

  public void uploadAvatar(MultipartFile file) {
    imageFileValidator.isValidImageExtension(file);
    imageFileValidator.validateFileSize(file);

    // continue with storage
  }
}
```

## Example public URL validation

```java
public void validateCurrentImage(String imageUrl) {
  imageFileValidator.isValidImageUrl(imageUrl);
}
```

## Example explicit policy for another use case

If a service needs a policy different from the configured default, it can pass it explicitly without depending on a method with a specific name.

```java
public void uploadAnimatedImage(MultipartFile file) {
  imageFileValidator.validateImage(
      file,
      Set.of("png", "jpeg", "jpg", "gif"),
      Set.of("image/png", "image/jpeg", "image/jpg", "image/gif"));

  imageFileValidator.validateFileSize(file);
}
```

This approach keeps the library generic and avoids names coupled to a specific format.

## Benefits

- Consistency across services.
- Less rule duplication.
- Controlled and unified error messages.

## Recommendation

Any validator added here should meet two conditions:
- it should be technically generic
- it should have real reuse potential across multiple services
