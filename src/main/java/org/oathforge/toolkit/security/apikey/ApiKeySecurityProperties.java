package org.oathforge.toolkit.security.apikey;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties for shared API key authentication support.
 * <p>
 * These properties define whether the feature is enabled, the expected API key
 * value, the technical username associated with that key, and the authorities
 * granted to authenticated requests.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "backend-toolkit.security.api-key")
public class ApiKeySecurityProperties {

	private boolean apiKeyEnabled;

	private String apiKey;

	private String apiKeyUsername = "api-key-user";

	private List<String> apiKeyAuthorities = List.of("ROLE_API", "ROLE_ADMIN");
}
