package org.oathforge.toolkit.resilience;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.oathforge.toolkit.exception.ResilientOperationRetryExhaustedException;
import org.oathforge.toolkit.exception.ResilientOperationTimeoutException;

class ResilientOperationAspectTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withUserConfiguration(ResilienceConfiguration.class, TestConfiguration.class);

	@Test
	void shouldRetryAnnotatedOperationUntilItSucceeds() {
		contextRunner
				.withPropertyValues("backend-toolkit.resilience.enabled=true",
						"backend-toolkit.resilience.operations.retrying-client.retry.enabled=true",
						"backend-toolkit.resilience.operations.retrying-client.retry.max-attempts=3",
						"backend-toolkit.resilience.operations.retrying-client.retry.wait-duration=5ms")
				.run(context -> {
					RetryingClient retryingClient = context.getBean(RetryingClient.class);

					String result = retryingClient.call();

					assertThat(result).isEqualTo("ok");
					assertThat(retryingClient.attempts()).isEqualTo(3);
				});
	}

	@Test
	void shouldTimeoutAnnotatedOperation() {
		contextRunner
				.withPropertyValues("backend-toolkit.resilience.enabled=true",
						"backend-toolkit.resilience.operations.slow-client.timeout=25ms")
				.run(context -> {
					SlowClient slowClient = context.getBean(SlowClient.class);

					assertThatThrownBy(slowClient::call).isInstanceOf(ResilientOperationTimeoutException.class)
							.hasMessageContaining("slow-client");
				});
	}

	@Test
	void shouldRaiseSharedExceptionWhenRetriesAreExhausted() {
		contextRunner
				.withPropertyValues("backend-toolkit.resilience.enabled=true",
						"backend-toolkit.resilience.operations.unstable-client.retry.enabled=true",
						"backend-toolkit.resilience.operations.unstable-client.retry.max-attempts=2",
						"backend-toolkit.resilience.operations.unstable-client.retry.wait-duration=5ms")
				.run(context -> {
					UnstableClient unstableClient = context.getBean(UnstableClient.class);

					assertThatThrownBy(unstableClient::call)
							.isInstanceOf(ResilientOperationRetryExhaustedException.class)
							.hasMessageContaining("unstable-client")
							.cause()
							.isInstanceOf(IllegalStateException.class);
				});
	}

	@Test
	void shouldPreserveThreadBoundContextWhenTimeoutUsesWorkerThread() {
		contextRunner
				.withPropertyValues("backend-toolkit.resilience.enabled=true",
						"backend-toolkit.resilience.operations.context-aware-client.timeout=100ms")
				.run(context -> {
					SecurityContextHolder.getContext()
							.setAuthentication(new TestingAuthenticationToken("resilience-user", "n/a"));
					MDC.put("traceId", "trace-123");

					try {
						ContextAwareClient contextAwareClient = context.getBean(ContextAwareClient.class);

						assertThat(contextAwareClient.call())
								.isEqualTo("resilience-user|trace-123");
					} finally {
						MDC.clear();
						SecurityContextHolder.clearContext();
					}
				});
	}

	@Configuration
	static class TestConfiguration {

		@Bean
		RetryingClient retryingClient() {
			return new RetryingClient();
		}

		@Bean
		SlowClient slowClient() {
			return new SlowClient();
		}

		@Bean
		UnstableClient unstableClient() {
			return new UnstableClient();
		}

		@Bean
		ContextAwareClient contextAwareClient() {
			return new ContextAwareClient();
		}
	}

	static class RetryingClient {

		private final AtomicInteger attempts = new AtomicInteger();

		@ResilientOperation("retrying-client")
		String call() {
			if (attempts.incrementAndGet() < 3) {
				throw new IllegalStateException("transient error");
			}
			return "ok";
		}

		int attempts() {
			return attempts.get();
		}
	}

	@ResilientOperation("slow-client")
	static class SlowClient {

		String call() throws InterruptedException {
			Thread.sleep(Duration.ofMillis(150));
			return "late";
		}
	}

	static class UnstableClient {

		@ResilientOperation("unstable-client")
		String call() {
			throw new IllegalStateException("always failing");
		}
	}

	static class ContextAwareClient {

		@ResilientOperation("context-aware-client")
		String call() {
			return SecurityContextHolder.getContext().getAuthentication().getName() + "|" + MDC.get("traceId");
		}
	}
}
