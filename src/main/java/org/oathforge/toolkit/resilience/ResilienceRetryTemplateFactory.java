package org.oathforge.toolkit.resilience;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

final class ResilienceRetryTemplateFactory {

	private static final Logger log = LoggerFactory.getLogger(ResilienceRetryTemplateFactory.class);

	RetryTemplate create(String operationName, ResolvedResilienceOperation.RetrySettings retrySettings) {
		RetryTemplate retryTemplate = new RetryTemplate();

		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(Math.max(1, retrySettings.maxAttempts()));
		retryTemplate.setRetryPolicy(retryPolicy);

		FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
		backOffPolicy.setBackOffPeriod(Math.max(0L, retrySettings.waitDuration().toMillis()));
		retryTemplate.setBackOffPolicy(backOffPolicy);

		retryTemplate.registerListener(new RetryListener() {

			@Override
			public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
				return true;
			}

			@Override
			public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
					Throwable throwable) {
				int nextAttempt = context.getRetryCount() + 1;
				if (nextAttempt < retrySettings.maxAttempts()) {
					log.warn("Resilient operation '{}' failed on attempt {}. Retrying.", operationName, nextAttempt,
							throwable);
				}
			}

			@Override
			public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
					Throwable throwable) {
				if (throwable != null) {
					log.warn("Resilient operation '{}' exhausted {} attempt(s).", operationName,
							retrySettings.maxAttempts(), throwable);
				}
			}
		});

		return retryTemplate;
	}
}
