package org.oathforge.toolkit.resilience;

import java.time.Duration;

record ResolvedResilienceOperation(Duration timeout, RetrySettings retry) {

	record RetrySettings(boolean enabled, int maxAttempts, Duration waitDuration) {
	}
}
