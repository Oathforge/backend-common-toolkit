# backend-common-toolkit

`backend-common-toolkit` is a standalone Java library designed to centralize reusable technical building blocks for Spring Boot backend services.

The library groups together infrastructure components that are frequently repeated across services: consistent HTTP error handling, shared validation, `API-Key` support, encryption utilities, and public asset URL resolution.

## Purpose

Provide a shared technical foundation without introducing business logic or coupling consumers to a specific domain.

## What it currently includes

- Shared HTTP exception model.
- `ProblemDetail` and centralized error handling for REST APIs.
- Security helpers for `API-Key` authentication.
- Shared CORS configuration activated through properties.
- Optional OpenAPI support activated through properties.
- Optional geospatial utilities activated through properties.
- Shared validation for passwords, UUIDs, and images.
- Encryption utilities for persisted attributes.
- Shared public asset delivery layer and CDN abstraction.
- Simple reusable pagination DTOs.
- Logging utilities for `WebClient` clients.
- A separate JPA module for consumers that need relational support.

## Functional structure

- `exception`: exception hierarchy and standard HTTP response model.
- `security/apikey`: shared support for API key authentication.
- `security/cors`: reusable CORS configuration activated through properties.
- `openapi`: optional OpenAPI configuration.
- `geo`: optional geospatial utilities.
- `validator`: reusable input validation.
- `util`: encryption and general technical utilities.
- `assetdelivery`: public URL resolution and cache invalidation for assets.
- `payload`: pagination DTOs.
- `configuration`: reusable technical configuration helpers.

## Consumption approach

The library is intended to be consumed as a regular Maven dependency, without requiring a private parent POM or a project-specific BOM.

## Basic integration

### Maven dependency

```xml
<dependency>
  <groupId>org.oathforge</groupId>
  <artifactId>backend-common-toolkit</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Optional persistence modules

If the project uses JPA/Hibernate and needs base auditing, `TimeOrderedUuid`, or JPA converters, use the separate `backend-common-toolkit-jpa` module.

If the project uses Spring Data MongoDB and needs base auditing or automatic time-ordered ID generation, use `backend-common-toolkit-mongo`.

### Quick examples by area

#### HTTP errors

```java
throw new ResourceNotFoundException("USR0001", "User not found");
```

#### API key

```yml
backend-toolkit:
  security:
    api-key:
      api-key-enabled: true
      api-key: change-me
      api-key-username: internal-client
      api-key-authorities: "ROLE_READ,ROLE_ADMIN"
```

#### Shared CORS

```yml
backend-toolkit:
  security:
    cors:
      enabled: true
      allowed-origin-patterns: "https://example.com"
      allowed-methods: "GET,POST"
      allowed-headers: "Content-Type,Authorization"
      allowCredentials: true
      maxAge: 3600
      paths: "/api/**"
```

#### Optional OpenAPI

```yml
backend-toolkit:
  openapi:
    enabled: true
    title: "EcoRastro User Roles API"
    description: "API for user, role, and group management"
    version: "v1"
```

#### Optional geo utilities

```yml
backend-toolkit:
  geo:
    enabled: true
```

#### Password validation

```java
public record ChangePasswordRequest(@ValidPassword String newPassword) {
}
```

#### UUID validation

```java
public record FindUserRequest(@ValidUuid String userId) {
}
```

#### Configurable image validation

```yml
backend-toolkit:
  validation:
    image:
      allowed-extensions: "png,jpeg,jpg"
      allowed-mime-types: "image/png,image/jpeg,image/jpg"
      max-file-size-mb: 5
```

#### Direct encryption

```java
String encrypted = EncryptionUtil.encrypt("my-secret-token", "base-key");
String plain = EncryptionUtil.decrypt(encrypted, "base-key");
```

#### Public asset delivery

```java
AssetDelivery delivery = new DirectAssetDelivery(key -> "https://cdn.example.com/" + key);
String publicUrl = delivery.getFileUrlByKey("users/avatar.png");
```

## Detailed documentation

- [Available exceptions and ProblemDetail](docs/exceptions.md)
- [API key security](docs/security-apikey.md)
- [CORS configuration](docs/cors-configuration.md)
- [Optional OpenAPI](docs/openapi.md)
- [Optional geo utilities](docs/geo.md)
- [Shared validation](docs/validation.md)
- [Encryption and conversions](docs/encryption.md)
- [Public asset delivery](docs/asset-delivery.md)
- [Pagination and auxiliary utilities](docs/utilities.md)

## Typical use cases

- Standardize error responses across multiple microservices.
- Reuse filters and internal API key authentication configuration.
- Share password and image validation without duplicating code.
- Resolve public file URLs through different delivery providers.
- Reuse backend utilities without dragging in JPA when it is not needed.
