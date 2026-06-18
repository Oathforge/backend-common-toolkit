package org.oathforge.toolkit.openapi;

import java.util.List;

import org.springdoc.core.customizers.OpenApiCustomizer;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Adds toolkit API key documentation to OpenAPI and augments bearer-protected
 * operations so Swagger UI can authenticate them with either bearer or API key.
 */
public class ApiKeyOpenApiCustomizer implements OpenApiCustomizer {

	public static final String API_KEY_SCHEME_NAME = "apiKeyAuth";
	public static final String BEARER_SCHEME_NAME = "bearerAuth";
	public static final String API_KEY_HEADER = "API-Key";

	@Override
	public void customise(OpenAPI openApi) {
		Components components = openApi.getComponents();
		if (components == null) {
			components = new Components();
			openApi.setComponents(components);
		}

		if (components.getSecuritySchemes() == null || !components.getSecuritySchemes().containsKey(API_KEY_SCHEME_NAME)) {
			components.addSecuritySchemes(API_KEY_SCHEME_NAME,
					new SecurityScheme().type(SecurityScheme.Type.APIKEY)
							.in(SecurityScheme.In.HEADER)
							.name(API_KEY_HEADER)
							.description("Technical API key authentication for protected endpoints."));
		}

		augmentGlobalSecurity(openApi.getSecurity());
		augmentPaths(openApi.getPaths());
	}

	private void augmentGlobalSecurity(List<SecurityRequirement> securityRequirements) {
		if (securityRequirements == null) {
			return;
		}

		for (SecurityRequirement securityRequirement : securityRequirements) {
			if (securityRequirement.containsKey(BEARER_SCHEME_NAME)
					&& !securityRequirement.containsKey(API_KEY_SCHEME_NAME)) {
				securityRequirement.addList(API_KEY_SCHEME_NAME);
			}
		}
	}

	private void augmentPaths(Paths paths) {
		if (paths == null) {
			return;
		}

		for (PathItem pathItem : paths.values()) {
			for (Operation operation : pathItem.readOperations()) {
				augmentOperation(operation);
			}
		}
	}

	private void augmentOperation(Operation operation) {
		List<SecurityRequirement> securityRequirements = operation.getSecurity();
		if (securityRequirements == null) {
			return;
		}

		for (SecurityRequirement securityRequirement : securityRequirements) {
			if (securityRequirement.containsKey(BEARER_SCHEME_NAME)
					&& !securityRequirement.containsKey(API_KEY_SCHEME_NAME)) {
				securityRequirement.addList(API_KEY_SCHEME_NAME);
			}
		}
	}
}
