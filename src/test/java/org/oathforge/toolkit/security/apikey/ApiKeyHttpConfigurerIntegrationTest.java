package org.oathforge.toolkit.security.apikey;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(
		classes = ApiKeyHttpConfigurerIntegrationTest.TestApplication.class,
		properties = {
				"backend-toolkit.security.api-key.enabled=true",
				"backend-toolkit.security.api-key.api-key=test-key",
				"backend-toolkit.security.api-key.api-key-username=technical-client",
				"backend-toolkit.security.api-key.api-key-authorities[0]=ROLE_ADMIN"
		})
@AutoConfigureMockMvc
class ApiKeyHttpConfigurerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void authenticatesProtectedEndpointWithApiKeyWithoutManualFilterWiring() throws Exception {
		mockMvc.perform(get("/secured").header("API-Key", "test-key"))
				.andExpect(status().isOk());
	}

	@Test
	void rejectsProtectedEndpointWithoutApiKey() throws Exception {
		mockMvc.perform(get("/secured"))
				.andExpect(status().isUnauthorized());
	}

	@SpringBootApplication
	@RestController
	static class TestApplication {

		@GetMapping("/secured")
		String secured() {
			return "ok";
		}

		@Bean
		SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
			http.csrf(csrf -> csrf.disable())
					.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
					.exceptionHandling(exceptions -> exceptions
							.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
			return http.build();
		}
	}
}
