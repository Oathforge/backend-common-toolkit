package org.oathforge.toolkit.resilience;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "backend-toolkit.resilience")
public class ResilienceProperties {

	private boolean enabled = false;

	private OperationProperties defaultOperation = new OperationProperties();

	private Map<String, OperationProperties> operations = new LinkedHashMap<>();

	public static class OperationProperties {

		private Duration timeout;

		private RetryProperties retry = new RetryProperties();

		public Duration getTimeout() {
			return timeout;
		}

		public void setTimeout(Duration timeout) {
			this.timeout = timeout;
		}

		public RetryProperties getRetry() {
			return retry;
		}

		public void setRetry(RetryProperties retry) {
			this.retry = retry;
		}
	}

	public static class RetryProperties {

		private boolean enabled = false;

		private int maxAttempts = 3;

		private Duration waitDuration = Duration.ofMillis(200);

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public int getMaxAttempts() {
			return maxAttempts;
		}

		public void setMaxAttempts(int maxAttempts) {
			this.maxAttempts = maxAttempts;
		}

		public Duration getWaitDuration() {
			return waitDuration;
		}

		public void setWaitDuration(Duration waitDuration) {
			this.waitDuration = waitDuration;
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public OperationProperties getDefaultOperation() {
		return defaultOperation;
	}

	public void setDefaultOperation(OperationProperties defaultOperation) {
		this.defaultOperation = defaultOperation;
	}

	public Map<String, OperationProperties> getOperations() {
		return operations;
	}

	public void setOperations(Map<String, OperationProperties> operations) {
		this.operations = operations;
	}
}
