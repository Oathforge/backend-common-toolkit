package com.backendtoolkit.common.security.apikey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiKeyAuthenticationFactory {

	public UsernamePasswordAuthenticationToken createExpectedAuthentication(ApiKeySecurityProperties properties) {
		if (properties == null || !properties.isApiKeyEnabled() || properties.getApiKey() == null
				|| properties.getApiKey().isBlank()) {
			return null;
		}

		return new UsernamePasswordAuthenticationToken(properties.getApiKeyUsername(), properties.getApiKey(),
				AuthorityUtils.createAuthorityList(properties.getApiKeyAuthorities().toArray(String[]::new)));
	}
}
