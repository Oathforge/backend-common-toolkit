# API Key Security

## Purpose

This block allows you to secure technical or internal endpoints with an `API-Key` without having to reimplement filters and authentication construction in every service.

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
      api-key-enabled: true
      api-key: very-secret-key
      api-key-username: internal-client
      api-key-authorities: "ROLE_READ,ROLE_ADMIN"
```

## Example wiring in SecurityConfig

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(ApiKeySecurityProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {

  private final ApiKeySecurityProperties apiKeySecurityProperties;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    var expectedAuth = ApiKeyAuthenticationFactory.createExpectedAuthentication(apiKeySecurityProperties);
    var apiKeyFilter = new ApiKeyAuthFilter(expectedAuth);

    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.GET, "/internal/**").hasAnyRole("READ", "ADMIN")
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
```

## Example HTTP call

```bash
curl -H "API-Key: very-secret-key" http://localhost:8080/internal/health-sync
```

## Considerations

- An `API-Key` does not replace a global security strategy on its own.
- It should be used for controlled integrations, not as a universal mechanism.
- The associated authorities should be defined precisely to avoid over-permissioning.
