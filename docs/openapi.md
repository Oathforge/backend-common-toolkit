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

## Usage example

```java
@SpringBootApplication
public class UserRolesApplication {
}
```

With the property enabled, the project gets a shared minimal OpenAPI configuration.

## What happens if the consumer does not use OpenAPI

Nothing.

If the project does not enable `backend-toolkit.openapi.enabled`, this block is not loaded.

## What happens if the consumer already uses its own configuration

The idea is for this block to serve as a simple base. If the consumer already has a richer configuration, it can:
- leave this feature disabled
- or replace it with its own definition

## Summary

This block exists to save repeated OpenAPI configuration work without imposing it on projects that do not need it.
