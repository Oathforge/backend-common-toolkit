package org.oathforge.toolkit.security.encryption;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configuration properties for shared encryption support.
 * <p>
 * The configured key acts as the base secret used by toolkit encryption helpers
 * and should normally come from secure external configuration.
 */
@Configuration
@ConfigurationProperties(prefix = "backend-toolkit.security.encryption")
@Data
public class EncryptionProperties {

	private String key = "TwonPtHs5&A4Xftq";
}
