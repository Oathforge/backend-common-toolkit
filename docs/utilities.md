# Pagination and Auxiliary Utilities

## Pagination

The library includes simple DTOs and a utility class to standardize paginated responses in REST APIs:

- `PaginatedDTO<T>`
- `PaginationDTO`
- `PaginationUtils`

`PaginatedDTO<T>` and `PaginationDTO` define the response shape.

There are two valid usage patterns depending on where pagination happens:
- if pagination happens in the database through Spring Data `Page<T>`, build the response with `PaginatedDTO.build(...)`
- if pagination happens in memory from a full `List<T>`, build the response with `PaginationUtils.createPaginatedDto(...)`

### `PaginatedDTO<T>`

#### Description

`PaginatedDTO<T>` is a generic class designed to encapsulate a list of elements of type `T` together with the associated pagination information.

It is useful when an endpoint needs to return:
- the elements of the current page
- the requested page number
- the applied page size
- the total number of available elements

#### Attributes

- `elements`: list of elements of type `T`.
- `pagination`: `PaginationDTO` instance with the pagination details.

### `PaginationDTO`

#### Description

`PaginationDTO` is the object that contains the pagination metadata of a response.

#### Attributes

- `pageNumber`: current page number.
- `pageSize`: applied page size.
- `totalElements`: total number of available records.

## When To Use Each Approach

Use `PaginatedDTO.build(...)` when:
- the repository already returns a paginated `Page<T>`
- the total number of elements comes from the database query
- the service only maps the page content before returning the response

Use `PaginationUtils.createPaginatedDto(...)` when:
- the service works with a full `List<T>` in memory
- pagination must happen after mapping, filtering, combining, or transforming data
- you want the toolkit to slice the list and build the response metadata for you

## Database Pagination With `Page<T>`

This is the typical case when the repository already performs pagination.

### Example repository

```java
public interface UserRepository extends JpaRepository<User, UUID> {

  Page<User> findAllByActiveTrue(Pageable pageable);
}
```

### Example service

```java
public PaginatedDTO<UserResponse> getUsers(int page, int size) {
  Page<User> userPage = userRepository.findAllByActiveTrue(PageRequest.of(page, size));

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

### Example controller

```java
@GetMapping
public PaginatedDTO<UserResponse> getAllUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size) {
  return userService.getUsers(page, size);
}
```

## In-Memory Pagination With `PaginationUtils`

Use `PaginationUtils` when the service already has the full list in memory and needs to paginate it before returning the response.

This keeps pagination creation consistent and avoids repeating the manual slicing logic in every service.

### Example service

```java
public PaginatedDTO<UserResponse> getUsers(int page, int size) {
  List<UserResponse> allUsers = userRepository.findAllByActiveTrue().stream()
      .map(user -> new UserResponse(user.getId(), user.getEmail()))
      .toList();

  return PaginationUtils.createPaginatedDto(allUsers, page, size);
}
```

### Example repository

```java
public interface UserRepository extends JpaRepository<User, UUID> {

  List<User> findAllByActiveTrue();
}
```

### Example controller

```java
@GetMapping
public PaginatedDTO<UserResponse> getAllUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size) {
  return userService.getUsers(page, size);
}
```

This is useful when:
- the source is no longer a Spring Data `Page<T>`
- the service needs to paginate a list in memory before returning it
- you built the list after combining multiple sources
- you want to keep the same paginated response format

## Example JSON response

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

## WebClient logging

`WebClientLoggingFilter` provides reusable filters to trace requests and responses when working with `WebClient`.

This is useful for:
- debugging HTTP clients
- inspecting headers and response status
- technical support during integrations

## Example usage with WebClient

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

## Inclusion criteria

Utilities in this block only make sense here if:
- they are technical rather than domain-specific
- they expose a stable API
- they are reusable across multiple services
