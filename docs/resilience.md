# Resilience for Outbound Operations

The library includes an opt-in resilience layer for outbound calls executed from Spring beans.

The feature is centered on `@ResilientOperation("<operation-name>")` and currently provides:

- operation-level timeout
- configurable retry policy
- shared technical logging during retries and timeout failures

## Activation

The feature is disabled by default.

Enable it in the consumer service:

```yml
backend-toolkit:
  resilience:
    enabled: true
```

## Configuration model

You can define defaults for every resilient operation and override them per operation key:

```yml
backend-toolkit:
  resilience:
    enabled: true
    default-operation:
      timeout: 3s
      retry:
        enabled: false
        max-attempts: 3
        wait-duration: 200ms
    operations:
      chain-gateway:
        timeout: 2s
        retry:
          enabled: true
          max-attempts: 3
          wait-duration: 200ms
```

### Available properties

- `backend-toolkit.resilience.enabled`
- `backend-toolkit.resilience.default-operation.timeout`
- `backend-toolkit.resilience.default-operation.retry.enabled`
- `backend-toolkit.resilience.default-operation.retry.max-attempts`
- `backend-toolkit.resilience.default-operation.retry.wait-duration`
- `backend-toolkit.resilience.operations.<name>.timeout`
- `backend-toolkit.resilience.operations.<name>.retry.enabled`
- `backend-toolkit.resilience.operations.<name>.retry.max-attempts`
- `backend-toolkit.resilience.operations.<name>.retry.wait-duration`

## Usage

Annotate the Spring bean method that performs the outbound call:

```java
@Component
@RequiredArgsConstructor
public class ChainGatewayClient {

  private final ChainGatewayHttpClient httpClient;

  @ResilientOperation("chain-gateway")
  public ChainTokenMintBatchResponseDTO mintBatch(ChainTokenMintBatchRequestDTO request) {
    return httpClient.mintBatch(request);
  }
}
```

You can also place the annotation at class level if every method in the bean belongs to the same external integration.

## Recommended placement

For the most predictable behavior, place the annotation on:

- adapter or proxy beans that encapsulate outbound HTTP calls
- service beans dedicated to an external integration

Avoid putting the annotation on:

- private methods
- self-invoked methods inside the same bean
- raw framework client classes that are not managed as interceptable Spring beans

## Timeout behavior

Timeout is enforced at the annotated operation level. When the timeout is reached, the operation fails with `org.oathforge.toolkit.exception.ResilientOperationTimeoutException`.

Thread-bound execution context such as Spring Security context, request attributes, locale context, and MDC is preserved when the timeout guard executes the operation on a worker thread.

Timeout enforcement is not supported inside an active Spring transaction. In that case the library fails fast and the annotation should be moved to a non-transactional outbound adapter.

Consumer services should still configure HTTP client connection and read timeouts when the underlying client supports them.

## Retry behavior

When retry is enabled, the operation is retried up to `max-attempts`, waiting `wait-duration` between attempts.

If all attempts fail, the operation fails with `org.oathforge.toolkit.exception.ResilientOperationRetryExhaustedException`, preserving the original cause.
