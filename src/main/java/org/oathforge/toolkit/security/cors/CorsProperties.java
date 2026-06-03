package org.oathforge.toolkit.security.cors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configuration properties for the shared CORS filter.
 * <p>
 * These values control whether the toolkit CORS filter is enabled and which
 * origins, methods, headers, paths, and cache settings are applied.
 */
@Configuration
@ConfigurationProperties(prefix = "backend-toolkit.security.cors")
@Data
public class CorsProperties {

	private boolean enabled = false;

	private String[] allowedOriginPatterns = {};

	private String[] allowedMethods = {};

	private String[] allowedHeaders = {};

	private String[] paths = {};

	private boolean allowCredentials = false;

	private long maxAge = 1800L;
}
