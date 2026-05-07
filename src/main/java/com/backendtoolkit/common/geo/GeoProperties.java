package com.backendtoolkit.common.geo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "backend-toolkit.geo")
@Data
public class GeoProperties {

	private boolean enabled = false;
}
