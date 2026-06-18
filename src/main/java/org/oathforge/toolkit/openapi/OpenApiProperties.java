package org.oathforge.toolkit.openapi;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "backend-toolkit.openapi")
@Data
public class OpenApiProperties {

	private boolean enabled = false;

	private String title = "Backend API";

	private String description = "OpenAPI configuration provided by backend-common-toolkit";

	private String version = "v1";
}
