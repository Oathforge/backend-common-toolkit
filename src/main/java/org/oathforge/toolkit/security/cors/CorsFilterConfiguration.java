package org.oathforge.toolkit.security.cors;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class CorsFilterConfiguration {

	@Bean
	@ConditionalOnProperty(value = "backend-toolkit.security.cors.enabled", havingValue = "true", matchIfMissing = false)
	CorsFilter corsFilter(CorsProperties properties) {
		log.warn("Using backend-common-toolkit custom CORS configuration");

		org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
		configuration.setAllowedOriginPatterns(List.of(properties.getAllowedOriginPatterns()));
		configuration.setAllowedMethods(List.of(properties.getAllowedMethods()));
		configuration.setAllowedHeaders(List.of(properties.getAllowedHeaders()));
		configuration.setAllowCredentials(properties.isAllowCredentials());
		configuration.setMaxAge(properties.getMaxAge());

		log.warn("Cors - Allowed origin patterns: {}", configuration.getAllowedOriginPatterns());
		log.warn("Cors - Allowed methods: {}", configuration.getAllowedMethods());
		log.warn("Cors - Allowed headers: {}", configuration.getAllowedHeaders());
		log.warn("Cors - Allow credentials: {}", properties.isAllowCredentials());
		log.warn("Cors - Max age: {}", properties.getMaxAge());

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		for (String path : properties.getPaths()) {
			log.warn("Cors - Adding path: {}", path);
			source.registerCorsConfiguration(path, configuration);
		}

		return new CorsFilter(source);
	}
}
