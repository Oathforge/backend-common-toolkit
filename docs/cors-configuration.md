# Configuracion CORS

## Objetivo

Este bloque permite activar una configuracion CORS compartida desde la propia libreria, evitando que cada microservicio tenga que reimplementar el `CorsFilter` y el binding de propiedades.

La configuracion es opcional y solo se activa si se informa explicitamente en `application.yml`.

## Componentes

- `CorsFilterConfiguration`
- `CorsProperties`

## Como se activa

La configuracion CORS se activa unicamente cuando la propiedad `backend-toolkit.security.cors.enabled` vale `true`.

Si no se informa este bloque, la libreria no registra ningun `CorsFilter`.

## Propiedades disponibles

- `enabled`: activa o desactiva la configuracion CORS compartida.
- `allowed-origin-patterns`: patrones de origen permitidos.
- `allowed-methods`: metodos HTTP permitidos.
- `allowed-headers`: cabeceras HTTP permitidas.
- `allow-credentials`: permite o no credenciales en las solicitudes CORS.
- `max-age`: tiempo maximo en segundos para cachear el resultado del preflight.
- `paths`: rutas sobre las que aplicar la configuracion.

## Ejemplo de configuracion

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

## Ejemplo de uso en un microservicio

No hace falta crear ninguna clase adicional si el paquete de la libreria entra en el escaneo de Spring.

```java
@SpringBootApplication
public class ApiApplication {
}
```

Con la configuracion anterior, Spring registrara automaticamente un `CorsFilter` comun para los paths indicados.

## Que resuelve exactamente

Este bloque crea una instancia de `CorsFilter` basada en propiedades externas y la registra con un `UrlBasedCorsConfigurationSource`.

Eso permite:
- activar o desactivar CORS sin tocar codigo
- variar origenes y metodos por entorno
- centralizar el comportamiento entre servicios

## Cuando tiene sentido usarlo

- cuando varios microservicios comparten politicas CORS similares
- cuando quieres evitar una implementacion repetida del filtro
- cuando necesitas mantener el comportamiento controlado por configuracion

## Cuando no usarlo

- si un servicio necesita una politica CORS totalmente excepcional y especifica
- si ya existe una configuracion CORS propia del proyecto que no quieres duplicar
