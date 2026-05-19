package org.oathforge.toolkit.security.encryption;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "backend-toolkit.security.encryption")
@Data
public class EncryptionProperties {

	private String key = "TwonPtHs5&A4Xftq";
}
