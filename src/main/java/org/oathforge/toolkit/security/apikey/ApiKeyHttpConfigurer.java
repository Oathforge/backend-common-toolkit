package org.oathforge.toolkit.security.apikey;

import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security DSL that inserts the toolkit API key filter into application
 * security chains without taking over authorization rules.
 */
public class ApiKeyHttpConfigurer extends AbstractHttpConfigurer<ApiKeyHttpConfigurer, HttpSecurity> {

	@Override
	public void configure(HttpSecurity http) {
		ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
		if (applicationContext == null) {
			return;
		}

		ApiKeyAuthFilter apiKeyAuthFilter = applicationContext.getBeanProvider(ApiKeyAuthFilter.class).getIfAvailable();
		if (apiKeyAuthFilter != null) {
			http.addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class);
		}
	}
}
