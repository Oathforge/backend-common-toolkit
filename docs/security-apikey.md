# API Key Security

## Purpose

This block allows you to secure technical or internal endpoints with an `API-Key` without having to reimplement filters and authentication construction in every service.

When the toolkit is consumed as a Spring Boot dependency, API key support is
auto-configured from properties alone.

## Components

- `ApiKeyAuthFilter`
- `ApiKeyAuthenticationFactory`
- `ApiKeySecurityProperties`

## Responsibility

- Read the API key from HTTP headers.
- Compare it with the configured value.
- Build a valid `Authentication` for the Spring Security context.
- Allow configurable authorities to be assigned to that technical identity.

## Use cases

- Internal calls between microservices.
- Synchronization or automation endpoints.
- Technical integrations where using a full JWT would be unnecessary.

## YAML configuration example

```yml
backend-toolkit:
  security:
    api-key:
      enabled: true
      api-key: very-secret-key
      api-key-username: internal-client
      api-key-authorities:
        - ROLE_READ
        - ROLE_ADMIN
```

Legacy compatibility is also kept for `api-key-enabled`, but `enabled` is the
recommended property name.

## Runtime behavior

- The toolkit auto-registers `ApiKeySecurityProperties`.
- When enabled and a concrete key is configured, it auto-creates
  `ApiKeyAuthFilter`.
- The filter is inserted automatically into Spring Security chains through a
  toolkit `AbstractHttpConfigurer` published through `META-INF/spring.factories`.
- Consumer services keep control of their authorization rules and endpoint
  matchers.

## SecurityConfig expectation

The consumer service still defines which endpoints are protected and which
authorities are required, but it does not need to wire the toolkit filter
manually anymore.

## Minimal consumer `SecurityFilterChain`

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.GET, "/internal/**").hasAnyRole("READ", "ADMIN")
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated());

    return http.build();
  }
}
```

The toolkit contributes `ApiKeySecurityProperties`, the expected
`Authentication`, and `ApiKeyAuthFilter` automatically when activation
conditions are met.

## Activation requirements

Toolkit API key support activates when:

- Spring Security is on the classpath
- `backend-toolkit.security.api-key.enabled=true`
- `backend-toolkit.security.api-key.api-key` has a configured value

If any of those conditions is missing, the toolkit does not create the API key
authentication beans.

## Migration from older consumer wiring

If a consumer service previously wired toolkit API key support manually:

- keep the service `SecurityFilterChain`
- remove manual `ApiKeyAuthFilter` bean creation
- remove manual `addFilterBefore(...)` usage for the toolkit filter
- move to `backend-toolkit.security.api-key.enabled`
- keep `api-key-enabled` only as a temporary legacy alias if needed

## Example HTTP call

```bash
curl -H "API-Key: very-secret-key" http://localhost:8080/internal/health-sync
```

## Considerations

- An `API-Key` does not replace a global security strategy on its own.
- It should be used for controlled integrations, not as a universal mechanism.
- The associated authorities should be defined precisely to avoid over-permissioning.
- For Swagger/OpenAPI integration, enable `backend-toolkit.openapi.enabled=true`.

## Troubleshooting

If requests authenticated with `API-Key` are still rejected, check:

- whether the target endpoint is actually protected by your `SecurityFilterChain`
- whether your authorization rules expect roles/authorities that match
  `api-key-authorities`
- whether another custom filter or bean override in the consumer is interfering
  with the toolkit configuration
