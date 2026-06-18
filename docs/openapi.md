# Optional OpenAPI

## Purpose

This block allows the library to provide a base OpenAPI configuration when the consuming project wants to use it.

It does not try to impose Swagger or OpenAPI on every project. That is why:
- it is disabled by default
- the dependency is declared as `provided`
- it is only activated through properties

## Activation

```yml
backend-toolkit:
  openapi:
    enabled: true
    title: "EcoRastro User Roles API"
    description: "API for user, role, and group management"
    version: "v1"
```

## What it creates

When active, it registers a base `OpenAPI` bean with:
- `title`
- `description`
- `version`

The `OpenAPI` bean is only created if the consumer does not already provide one.

If toolkit API key support is enabled too, it also registers an `OpenApiCustomizer`
that:
- adds the `apiKeyAuth` security scheme
- keeps compatibility with existing `bearerAuth` documentation
- augments bearer-protected operations so Swagger UI can use bearer or API key

## Usage example

```java
@SpringBootApplication
public class UserRolesApplication {
}
```

With the property enabled, the project gets a shared minimal OpenAPI configuration.

If the consumer already defines its own `OpenAPI` bean, the toolkit does not
replace it. In that case, the toolkit can still contribute the API key
customizer when API key support is enabled.

Typical combined setup:

```yml
backend-toolkit:
  openapi:
    enabled: true
    title: "EcoRastro User Roles API"
    description: "API for user, role, and group management"
    version: "v1"
  security:
    api-key:
      enabled: true
      api-key: very-secret-key
      api-key-username: internal-client
      api-key-authorities:
        - ROLE_READ
        - ROLE_ADMIN
```

## What happens if the consumer does not use OpenAPI

Nothing.

If the project does not enable `backend-toolkit.openapi.enabled`, this block is not loaded.

## What happens if the consumer already uses its own configuration

The idea is for this block to serve as a simple base. If the consumer already has a richer configuration, it can:
- leave this feature disabled
- or replace it with its own definition

## Migration notes

If a consumer previously created toolkit-style OpenAPI beans manually, the
preferred setup now is:

- keep `backend-toolkit.openapi.enabled=true`
- let the toolkit provide the base OpenAPI configuration
- let the toolkit add `apiKeyAuth` automatically when API key support is also enabled
- remove duplicate manual configuration unless the service truly needs a richer custom definition

## Troubleshooting

If shared OpenAPI configuration does not appear, verify:

- `backend-toolkit.openapi.enabled=true`
- springdoc/OpenAPI is on the classpath
- the consumer did not disable the feature intentionally

If `apiKeyAuth` does not appear in Swagger UI, verify:

- `backend-toolkit.security.api-key.enabled=true`
- `backend-toolkit.security.api-key.api-key` is configured
- the documented operations already expose `bearerAuth` or global security requirements for augmentation

## Summary

This block exists to save repeated OpenAPI configuration work without imposing it on projects that do not need it.
