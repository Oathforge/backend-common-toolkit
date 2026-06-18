package org.oathforge.toolkit.security.apikey;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * Auto-configures toolkit API key support for consumer applications.
 */
@AutoConfiguration
@ConditionalOnClass(UsernamePasswordAuthenticationToken.class)
@EnableConfigurationProperties(ApiKeySecurityProperties.class)
public class ApiKeySecurityAutoConfiguration {

	@Bean
	@Conditional(ApiKeyAuthenticationAvailableCondition.class)
	@ConditionalOnMissingBean(name = "backendToolkitApiKeyExpectedAuthentication")
	UsernamePasswordAuthenticationToken backendToolkitApiKeyExpectedAuthentication(ApiKeySecurityProperties properties) {
		return ApiKeyAuthenticationFactory.createExpectedAuthentication(properties);
	}

	@Bean
	@Conditional(ApiKeyAuthenticationAvailableCondition.class)
	@ConditionalOnMissingBean
	ApiKeyAuthFilter apiKeyAuthFilter(UsernamePasswordAuthenticationToken expectedAuthentication) {
		return new ApiKeyAuthFilter(expectedAuthentication);
	}
}
