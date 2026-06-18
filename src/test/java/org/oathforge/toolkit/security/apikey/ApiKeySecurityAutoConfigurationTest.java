package org.oathforge.toolkit.security.apikey;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.oathforge.toolkit.openapi.OpenApiConfiguration;
import org.oathforge.toolkit.openapi.OpenApiProperties;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

class ApiKeySecurityAutoConfigurationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(ApiKeySecurityAutoConfiguration.class, OpenApiConfiguration.class));

	@Test
	void createsApiKeyBeansWhenEnabledPropertyIsUsed() {
		contextRunner
				.withPropertyValues(
						"backend-toolkit.security.api-key.enabled=true",
						"backend-toolkit.security.api-key.api-key=test-key",
						"backend-toolkit.openapi.enabled=true")
				.run(context -> {
					assertThat(context).hasSingleBean(ApiKeySecurityProperties.class);
					assertThat(context).hasSingleBean(UsernamePasswordAuthenticationToken.class);
					assertThat(context).hasSingleBean(ApiKeyAuthFilter.class);
					assertThat(context).hasSingleBean(OpenApiCustomizer.class);
				});
	}

	@Test
	void createsApiKeyBeansWhenLegacyEnabledPropertyIsUsed() {
		contextRunner
				.withPropertyValues(
						"backend-toolkit.security.api-key.api-key-enabled=true",
						"backend-toolkit.security.api-key.api-key=test-key")
				.run(context -> {
					assertThat(context).hasSingleBean(ApiKeySecurityProperties.class);
					assertThat(context).hasSingleBean(UsernamePasswordAuthenticationToken.class);
					assertThat(context).hasSingleBean(ApiKeyAuthFilter.class);
					assertThat(context.getBean(ApiKeySecurityProperties.class).isEnabled()).isTrue();
				});
	}

	@Test
	void doesNotCreateAuthenticationBeansWhenApiKeyValueIsMissing() {
		contextRunner
				.withPropertyValues("backend-toolkit.security.api-key.enabled=true")
				.run(context -> {
					assertThat(context).hasSingleBean(ApiKeySecurityProperties.class);
					assertThat(context).doesNotHaveBean(UsernamePasswordAuthenticationToken.class);
					assertThat(context).doesNotHaveBean(ApiKeyAuthFilter.class);
				});
	}

	@Test
	void createsOpenApiPropertiesBeanWhenOpenApiIsEnabled() {
		contextRunner
				.withPropertyValues("backend-toolkit.openapi.enabled=true")
				.run(context -> assertThat(context).hasSingleBean(OpenApiProperties.class));
	}
}
