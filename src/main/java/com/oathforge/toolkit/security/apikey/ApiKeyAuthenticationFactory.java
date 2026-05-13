package com.oathforge.toolkit.security.apikey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiKeyAuthenticationFactory {

	/**
	 * Creates the expected API key authentication token from the provided
	 * configuration.
	 * <p>
	 * The returned token is intended to represent the technical identity that
	 * should be granted when an incoming request provides the configured API key.
	 *
	 * @param properties API key security properties
	 * @return expected authentication token, or {@code null} when API key support
	 *         is disabled or incomplete
	 */
	public UsernamePasswordAuthenticationToken createExpectedAuthentication(ApiKeySecurityProperties properties) {
		if (properties == null || !properties.isApiKeyEnabled() || properties.getApiKey() == null
				|| properties.getApiKey().isBlank()) {
			return null;
		}

		return new UsernamePasswordAuthenticationToken(properties.getApiKeyUsername(), properties.getApiKey(),
				AuthorityUtils.createAuthorityList(properties.getApiKeyAuthorities().toArray(String[]::new)));
	}
}
