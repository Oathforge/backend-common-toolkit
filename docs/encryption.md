# Encryption and Conversions

## Purpose

This block provides centralized support for encrypting and decrypting values, especially in scenarios where certain attributes need to be stored protected in a database.

## Components

- `EncryptionUtil`

## Responsibility

- Derive encryption keys.
- Encrypt and decrypt strings.

## Typical use case

Persist sensitive fields such as secrets, tokens, or data that should not be stored in plain text.

## Direct utility example

```java
String encrypted = EncryptionUtil.encrypt("my-secret-token", "base-key");
String plain = EncryptionUtil.decrypt(encrypted, "base-key");
```

## Configuration example

```yml
backend-toolkit:
  security:
    encryption:
      key: ${APP_ENCRYPTION_KEY}
```

## Considerations

- The encryption key should be injected from secure configuration.
- The library provides the mechanism, but it does not replace a secrets management policy.
- If you use JPA and want transparent encryption through an `AttributeConverter`, use the `backend-common-toolkit-jpa` module.
