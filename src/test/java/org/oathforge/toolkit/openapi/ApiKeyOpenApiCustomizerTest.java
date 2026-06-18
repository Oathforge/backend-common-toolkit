package org.oathforge.toolkit.openapi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.security.SecurityRequirement;

class ApiKeyOpenApiCustomizerTest {

	private final ApiKeyOpenApiCustomizer customizer = new ApiKeyOpenApiCustomizer();

	@Test
	void addsApiKeySchemeAndAugmentsBearerProtectedOperations() {
		Operation operation = new Operation().security(List.of(new SecurityRequirement().addList("bearerAuth")));
		OpenAPI openApi = new OpenAPI()
				.paths(new Paths().addPathItem("/users", new PathItem().get(operation)))
				.security(List.of(new SecurityRequirement().addList("bearerAuth")));

		customizer.customise(openApi);

		assertThat(openApi.getComponents().getSecuritySchemes()).containsKey(ApiKeyOpenApiCustomizer.API_KEY_SCHEME_NAME);
		assertThat(openApi.getSecurity()).allSatisfy(requirement -> assertThat(requirement)
				.containsKeys(ApiKeyOpenApiCustomizer.BEARER_SCHEME_NAME, ApiKeyOpenApiCustomizer.API_KEY_SCHEME_NAME));
		assertThat(operation.getSecurity()).allSatisfy(requirement -> assertThat(requirement)
				.containsKeys(ApiKeyOpenApiCustomizer.BEARER_SCHEME_NAME, ApiKeyOpenApiCustomizer.API_KEY_SCHEME_NAME));
	}
}
