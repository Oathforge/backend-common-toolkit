package com.oathforge.toolkit.security.apikey;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "backend-toolkit.security.api-key")
public class ApiKeySecurityProperties {

	private boolean apiKeyEnabled;

	private String apiKey;

	private String apiKeyUsername = "api-key-user";

	private List<String> apiKeyAuthorities = List.of("ROLE_API", "ROLE_ADMIN");
}
