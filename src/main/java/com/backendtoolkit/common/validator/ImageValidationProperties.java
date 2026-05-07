package com.backendtoolkit.common.validator;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "backend-toolkit.validation.image")
@Data
public class ImageValidationProperties {

	private Set<String> allowedExtensions = Set.of("png", "jpeg", "jpg");

	private Set<String> allowedMimeTypes = Set.of("image/png", "image/jpeg", "image/jpg");

	private long maxFileSizeMb = 5;
}
