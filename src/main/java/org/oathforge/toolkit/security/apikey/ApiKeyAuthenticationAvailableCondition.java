package org.oathforge.toolkit.security.apikey;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Matches when toolkit API key support is enabled and a concrete API key value
 * is configured.
 */
public class ApiKeyAuthenticationAvailableCondition implements Condition {

	private static final String API_KEY = "backend-toolkit.security.api-key.api-key";
	private static final String ENABLED = "backend-toolkit.security.api-key.enabled";
	private static final String LEGACY_ENABLED = "backend-toolkit.security.api-key.api-key-enabled";

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		String enabled = context.getEnvironment().getProperty(ENABLED);
		String legacyEnabled = context.getEnvironment().getProperty(LEGACY_ENABLED);
		boolean active = enabled != null ? Boolean.parseBoolean(enabled) : Boolean.parseBoolean(legacyEnabled);
		String apiKey = context.getEnvironment().getProperty(API_KEY);
		return active && apiKey != null && !apiKey.isBlank();
	}
}
