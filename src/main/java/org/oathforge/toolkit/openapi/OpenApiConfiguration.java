package org.oathforge.toolkit.openapi;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@ConditionalOnClass(OpenAPI.class)
@ConditionalOnProperty(prefix = "backend-toolkit.openapi", name = "enabled", havingValue = "true", matchIfMissing = false)
public class OpenApiConfiguration {

	@Bean
	OpenAPI backendToolkitOpenApi(OpenApiProperties properties) {
		return new OpenAPI().info(new Info().title(properties.getTitle())
				.description(properties.getDescription())
				.version(properties.getVersion()));
	}
}
