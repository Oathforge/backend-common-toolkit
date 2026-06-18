package org.oathforge.toolkit.security.apikey;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Matches when toolkit API key support is enabled through either the current
 * `enabled` property or the legacy `api-key-enabled` alias.
 */
public class ApiKeyEnabledCondition implements Condition {

	private static final String ENABLED = "backend-toolkit.security.api-key.enabled";
	private static final String LEGACY_ENABLED = "backend-toolkit.security.api-key.api-key-enabled";

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		String enabled = context.getEnvironment().getProperty(ENABLED);
		if (enabled != null) {
			return Boolean.parseBoolean(enabled);
		}

		String legacyEnabled = context.getEnvironment().getProperty(LEGACY_ENABLED);
		return Boolean.parseBoolean(legacyEnabled);
	}
}
