package org.oathforge.toolkit.openapi;

import org.oathforge.toolkit.security.apikey.ApiKeyEnabledCondition;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@AutoConfiguration
@ConditionalOnClass(OpenAPI.class)
@ConditionalOnProperty(prefix = "backend-toolkit.openapi", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(OpenApiProperties.class)
public class OpenApiConfiguration {

	@Bean
	@ConditionalOnMissingBean(OpenAPI.class)
	OpenAPI backendToolkitOpenApi(OpenApiProperties properties) {
		return new OpenAPI().info(new Info().title(properties.getTitle())
				.description(properties.getDescription())
				.version(properties.getVersion()));
	}

	@Bean
	@Conditional(ApiKeyEnabledCondition.class)
	@ConditionalOnMissingBean(name = "backendToolkitApiKeyOpenApiCustomizer")
	OpenApiCustomizer backendToolkitApiKeyOpenApiCustomizer() {
		return new ApiKeyOpenApiCustomizer();
	}
}
