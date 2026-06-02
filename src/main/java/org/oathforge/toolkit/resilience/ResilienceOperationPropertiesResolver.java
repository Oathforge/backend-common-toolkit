package org.oathforge.toolkit.resilience;

import java.util.Optional;

final class ResilienceOperationPropertiesResolver {

	private final ResilienceProperties properties;

	ResilienceOperationPropertiesResolver(ResilienceProperties properties) {
		this.properties = properties;
	}

	ResolvedResilienceOperation resolve(String operationName) {
		ResilienceProperties.OperationProperties defaults = Optional.ofNullable(properties.getDefaultOperation())
				.orElseGet(ResilienceProperties.OperationProperties::new);
		ResilienceProperties.OperationProperties configured = properties.getOperations().get(operationName);

		ResolvedResilienceOperation.RetrySettings retrySettings = new ResolvedResilienceOperation.RetrySettings(
				retryEnabled(defaults, configured), maxAttempts(defaults, configured), waitDuration(defaults, configured));

		return new ResolvedResilienceOperation(timeout(defaults, configured), retrySettings);
	}

	private java.time.Duration timeout(ResilienceProperties.OperationProperties defaults,
			ResilienceProperties.OperationProperties configured) {
		if (configured != null && configured.getTimeout() != null) {
			return configured.getTimeout();
		}
		return defaults.getTimeout();
	}

	private boolean retryEnabled(ResilienceProperties.OperationProperties defaults,
			ResilienceProperties.OperationProperties configured) {
		if (configured != null && configured.getRetry() != null) {
			return configured.getRetry().isEnabled();
		}
		return defaults.getRetry().isEnabled();
	}

	private int maxAttempts(ResilienceProperties.OperationProperties defaults,
			ResilienceProperties.OperationProperties configured) {
		if (configured != null && configured.getRetry() != null) {
			return configured.getRetry().getMaxAttempts();
		}
		return defaults.getRetry().getMaxAttempts();
	}

	private java.time.Duration waitDuration(ResilienceProperties.OperationProperties defaults,
			ResilienceProperties.OperationProperties configured) {
		if (configured != null && configured.getRetry() != null && configured.getRetry().getWaitDuration() != null) {
			return configured.getRetry().getWaitDuration();
		}
		return defaults.getRetry().getWaitDuration();
	}
}
