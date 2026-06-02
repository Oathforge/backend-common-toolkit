# CORS Configuration

## Purpose

This block allows a shared CORS configuration to be activated directly from the library, avoiding the need for each microservice to reimplement its own `CorsFilter` and property binding.

The configuration is optional and is only activated when explicitly configured in `application.yml`.

## Components

- `CorsFilterConfiguration`
- `CorsProperties`

## How it is activated

CORS configuration is activated only when the `backend-toolkit.security.cors.enabled` property is set to `true`.

If this block is not configured, the library does not register any `CorsFilter`.

## Available properties

- `enabled`: enables or disables the shared CORS configuration.
- `allowed-origin-patterns`: allowed origin patterns.
- `allowed-methods`: allowed HTTP methods.
- `allowed-headers`: allowed HTTP headers.
- `allow-credentials`: whether credentials are allowed in CORS requests.
- `max-age`: maximum time in seconds to cache the preflight result.
- `paths`: routes where the configuration should be applied.

## Configuration example

```yml
backend-toolkit:
  security:
    cors:
      enabled: true
      allowed-origin-patterns: "https://example.com,https://*.example.com"
      allowed-methods: "GET,POST,PUT,DELETE"
      allowed-headers: "Content-Type,Authorization,X-Auth-Token"
      allow-credentials: true
      max-age: 3600
      paths: "/api/**,/public/**"
```

## Example usage in a microservice

You do not need to create any additional class if the library package is included in Spring scanning.

```java
@SpringBootApplication
public class ApiApplication {
}
```

With the configuration above, Spring will automatically register a shared `CorsFilter` for the specified paths.

## What it solves exactly

This block creates a `CorsFilter` instance based on external properties and registers it with a `UrlBasedCorsConfigurationSource`.

This allows you to:
- enable or disable CORS without touching code
- vary origins and methods per environment
- centralize behavior across services

## When it makes sense to use it

- when multiple microservices share similar CORS policies
- when you want to avoid repeated filter implementations
- when you need behavior to remain configuration-driven

## When not to use it

- if a service needs a completely exceptional and service-specific CORS policy
- if the project already has its own CORS configuration that you do not want to duplicate
