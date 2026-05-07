# Seguridad API Key

## Objetivo

Este bloque permite securizar endpoints tecnicos o internos mediante una `API-Key`, sin tener que reimplementar filtros y construccion de autenticacion en cada servicio.

## Componentes

- `ApiKeyAuthFilter`
- `ApiKeyAuthenticationFactory`
- `ApiKeySecurityProperties`

## Responsabilidad

- Leer la API key desde cabeceras HTTP.
- Compararla con el valor configurado.
- Construir un `Authentication` valido para el contexto de Spring Security.
- Permitir asignar authorities configurables a esa identidad tecnica.

## Casos de uso

- Llamadas internas entre microservicios.
- Endpoints de sincronizacion o automatizacion.
- Integraciones tecnicas donde no compensa usar JWT completo.

## Ejemplo de configuracion YAML

```yml
backend-toolkit:
  security:
    api-key:
      api-key-enabled: true
      api-key: very-secret-key
      api-key-username: internal-client
      api-key-authorities: "ROLE_READ,ROLE_ADMIN"
```

## Ejemplo de wiring en SecurityConfig

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

## Ejemplo de llamada HTTP

```bash
curl -H "API-Key: very-secret-key" http://localhost:8080/internal/health-sync
```

## Consideraciones

- La `API-Key` no sustituye por si sola una estrategia global de seguridad.
- Conviene usarla para integraciones controladas y no como mecanismo universal.
- Las authorities asociadas deben definirse con precision para evitar sobrepermisos.
