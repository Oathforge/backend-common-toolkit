# backend-common-toolkit

`backend-common-toolkit` es una libreria Java standalone orientada a centralizar bloques tecnicos reutilizables en microservicios backend Spring Boot.

La libreria agrupa piezas de infraestructura que se repiten con frecuencia entre servicios: manejo homogéneo de errores HTTP, validaciones compartidas, soporte de `API-Key`, utilidades de cifrado y resolucion de URLs publicas de assets.

## Objetivo

Aportar una base tecnica comun sin introducir logica de negocio ni acoplar el consumidor a un dominio concreto.

## Que incluye actualmente

- Modelo comun de excepciones HTTP.
- `ProblemDetail` y manejo centralizado de errores para APIs REST.
- Helpers de seguridad para autenticacion por `API-Key`.
- Configuracion CORS compartida y activable por propiedades.
- Soporte OpenAPI opcional y activable por propiedades.
- Utilidades geoespaciales opcionales activables por propiedades.
- Validaciones compartidas para contraseñas, UUIDs e imagenes.
- Utilidades de cifrado para atributos persistidos.
- Capa comun de entrega publica de assets y abstraccion de CDN.
- DTOs simples de paginacion reutilizable.
- Utilidades de logging para clientes `WebClient`.
- Módulo JPA separado para quien necesite soporte relacional.

## Estructura funcional

- `exception`: jerarquia de excepciones y respuesta HTTP estandar.
- `security/apikey`: soporte compartido para autenticacion por API key.
- `security/cors`: configuracion CORS reutilizable y activable por propiedades.
- `openapi`: configuracion OpenAPI opcional.
- `geo`: utilidades geoespaciales opcionales.
- `validator`: validaciones de entrada reutilizables.
- `util`: cifrado y utilidades tecnicas generales.
- `assetdelivery`: resolucion de URLs publicas e invalidacion de cache para assets.
- `payload`: DTOs de paginacion.
- `configuration`: helpers tecnicos de configuracion reutilizables.

## Enfoque de consumo

La libreria esta planteada para ser consumida como dependencia Maven normal, sin exigir un parent privado ni un BOM propietario del proyecto consumidor.

## Integracion basica

### Dependencia Maven

```xml
<dependency>
  <groupId>io.backendtoolkit</groupId>
  <artifactId>backend-common-toolkit</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Módulos de persistencia opcionales

Si el proyecto usa JPA/Hibernate y necesita auditoría base, `TimeOrderedUuid` o converters JPA, utiliza el módulo separado `backend-common-toolkit-jpa`.

Si el proyecto usa Spring Data MongoDB y necesita auditoría base o generación automática de IDs temporales, utiliza `backend-common-toolkit-mongo`.

### Ejemplos rapidos por bloque

#### Errores HTTP

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

#### CORS compartido

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

#### OpenAPI opcional

```yml
backend-toolkit:
  openapi:
    enabled: true
    title: "EcoRastro User Roles API"
    description: "API de gestion de usuarios, roles y grupos"
    version: "v1"
```

#### Geo opcional

```yml
backend-toolkit:
  geo:
    enabled: true
```

#### Validacion de password

```java
public record ChangePasswordRequest(@ValidPassword String newPassword) {
}
```

#### Validacion de UUID

```java
public record FindUserRequest(@ValidUuid String userId) {
}
```

#### Validacion de imagen configurable

```yml
backend-toolkit:
  validation:
    image:
      allowed-extensions: "png,jpeg,jpg"
      allowed-mime-types: "image/png,image/jpeg,image/jpg"
      max-file-size-mb: 5
```

#### Cifrado directo

```java
String encrypted = EncryptionUtil.encrypt("my-secret-token", "base-key");
String plain = EncryptionUtil.decrypt(encrypted, "base-key");
```

#### Entrega publica de assets

```java
AssetDelivery delivery = new DirectAssetDelivery(key -> "https://cdn.example.com/" + key);
String publicUrl = delivery.getFileUrlByKey("users/avatar.png");
```

## Documentacion detallada

- [Excepciones disponibles y ProblemDetail](docs/exceptions.md)
- [Seguridad API key](docs/security-apikey.md)
- [Configuracion CORS](docs/cors-configuration.md)
- [OpenAPI opcional](docs/openapi.md)
- [Geo util opcional](docs/geo.md)
- [Validaciones compartidas](docs/validation.md)
- [Cifrado y conversiones](docs/encryption.md)
- [Entrega publica de assets](docs/asset-delivery.md)
- [Paginacion y utilidades auxiliares](docs/utilities.md)

## Casos de uso tipicos

- Estandarizar respuestas de error entre varios microservicios.
- Reutilizar filtros y configuracion de autenticacion interna por API key.
- Compartir validaciones de password e imagen sin duplicar codigo.
- Resolver URLs publicas de ficheros desde distintos proveedores de entrega.
- Reutilizar utilidades backend sin arrastrar JPA cuando no hace falta.
