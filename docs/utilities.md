# Pagination and Auxiliary Utilities

## Pagination

The library includes simple DTOs to standardize paginated responses in REST APIs:

- `PaginatedDTO<T>`
- `PaginationDTO`
- `PaginationUtils`

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

#### Main method

##### `build(List<T> elements, int page, int size, Long totalElements)`

Static method that simplifies the construction of the full DTO from a list and its metadata.

**Parameters**:
- `elements`: elements of the current page.
- `page`: current page number.
- `size`: page size.
- `totalElements`: total number of available records.

**Returns**:
- a new `PaginatedDTO<T>` instance ready to be returned by the API.

### `PaginationDTO`

#### Description

`PaginationDTO` is the object that contains the pagination metadata of a response.

#### Attributes

- `pageNumber`: current page number.
- `pageSize`: applied page size.
- `totalElements`: total number of available records.

## Example usage in a service

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

## Example with `PaginationUtils`

When you already have a full list in memory and want to paginate it simply without repeating the manual slicing block, you can use `PaginationUtils`.

```java
List<UserResponse> allUsers = userRepository.findAll().stream()
    .map(user -> new UserResponse(user.getId(), user.getEmail()))
    .toList();

PaginatedDTO<UserResponse> response = PaginationUtils.createPaginatedDto(allUsers, page, size);
```

This is useful when:
- the source is no longer a Spring Data `Page<T>`
- you built the list after combining multiple sources
- you want to keep the same paginated response format

## Example usage in a controller

This example shows the usage pattern in an endpoint. Data access logic should live in the service, not in the controller.

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
