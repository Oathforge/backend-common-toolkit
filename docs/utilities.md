# Paginacion y Utilidades Auxiliares

## Paginacion

La libreria incluye dos DTOs simples para estandarizar respuestas paginadas en APIs REST:

- `PaginatedDTO<T>`
- `PaginationDTO`
- `PaginationUtils`

### `PaginatedDTO<T>`

#### Descripcion

`PaginatedDTO<T>` es una clase generica diseñada para encapsular una lista de elementos de tipo `T` junto con la informacion de paginacion asociada.

Es util cuando un endpoint necesita devolver:
- los elementos de la pagina actual
- el numero de pagina solicitado
- el tamano de pagina aplicado
- el total de elementos disponibles

#### Atributos

- `elements`: lista de elementos del tipo `T`.
- `pagination`: instancia de `PaginationDTO` con el detalle de paginacion.

#### Metodo principal

##### `build(List<T> elements, int page, int size, Long totalElements)`

Metodo estatico que facilita la construccion del DTO completo a partir de una lista y sus metadatos.

**Parametros**:
- `elements`: elementos de la pagina actual.
- `page`: numero de pagina actual.
- `size`: tamano de pagina.
- `totalElements`: numero total de registros disponibles.

**Retorno**:
- una nueva instancia de `PaginatedDTO<T>` lista para devolverse por API.

### `PaginationDTO`

#### Descripcion

`PaginationDTO` es el objeto que concentra los metadatos de paginacion de una respuesta.

#### Atributos

- `pageNumber`: numero de pagina actual.
- `pageSize`: tamano de pagina aplicado.
- `totalElements`: total de registros disponibles.

## Ejemplo de uso en un servicio

```java
public PaginatedDTO<UserResponse> getUsers(Page<User> page) {
  List<UserResponse> elements = page.getContent().stream()
      .map(user -> new UserResponse(user.getId(), user.getEmail()))
      .toList();

  return PaginatedDTO.build(
      elements,
      page.getNumber(),
      page.getSize(),
      page.getTotalElements());
}
```

## Ejemplo con `PaginationUtils`

Cuando ya tienes una lista completa en memoria y quieres paginarla de forma simple sin repetir el bloque de slicing manual, puedes usar `PaginationUtils`.

```java
List<UserResponse> allUsers = userRepository.findAll().stream()
    .map(user -> new UserResponse(user.getId(), user.getEmail()))
    .toList();

PaginatedDTO<UserResponse> response = PaginationUtils.createPaginatedDto(allUsers, page, size);
```

Esto es util cuando:
- el origen ya no es un `Page<T>` de Spring Data
- has compuesto la lista tras combinar varias fuentes
- quieres mantener el mismo formato de respuesta paginada

## Ejemplo de uso en un controlador

Este ejemplo muestra el formato de uso en un endpoint. La logica de acceso a datos deberia vivir en el servicio, no en el controlador.

```java
@GetMapping
public PaginatedDTO<UserResponse> getAllUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size) {

  Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));

  List<UserResponse> elements = userPage.getContent().stream()
      .map(user -> new UserResponse(user.getId(), user.getEmail()))
      .toList();

  return PaginatedDTO.build(
      elements,
      userPage.getNumber(),
      userPage.getSize(),
      userPage.getTotalElements());
}
```

## Ejemplo de respuesta JSON

```json
{
  "elements": [
    {
      "id": "0196a5c0-4d78-7c2b-8c6e-a7c24f787b12",
      "email": "user1@example.com"
    },
    {
      "id": "0196a5c1-16c3-7fd8-baa1-2a87f16f6a18",
      "email": "user2@example.com"
    }
  ],
  "pagination": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 146
  }
}
```

## Logging de WebClient

`WebClientLoggingFilter` aporta filtros reutilizables para trazar peticiones y respuestas cuando se trabaja con `WebClient`.

Esto es util para:
- depuracion de clientes HTTP
- inspeccion de cabeceras y estado de respuesta
- soporte tecnico durante integraciones

## Ejemplo de uso con WebClient

```java
@Bean
WebClient partnerClient(WebClient.Builder builder) {
  return builder
      .baseUrl("https://partner.example.com")
      .filter(WebClientLoggingFilter.logRequest())
      .filter(WebClientLoggingFilter.logResponse())
      .build();
}
```

## Criterio de inclusion

Las utilidades de este bloque solo tienen sentido aqui si:
- son tecnicas y no de dominio
- tienen una API estable
- son reutilizables entre varios servicios
