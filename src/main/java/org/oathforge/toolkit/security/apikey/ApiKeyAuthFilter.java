package org.oathforge.toolkit.security.apikey;

import java.io.IOException;
import java.util.Objects;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Spring Security filter that authenticates requests carrying a valid API key
 * header.
 * <p>
 * The filter checks the standard toolkit API key headers and, when the value
 * matches the configured expected credentials, places a pre-authenticated
 * authentication object into the current security context.
 */
public class ApiKeyAuthFilter extends OncePerRequestFilter {

	private static final String API_KEY_HEADER = "API-Key";
	private static final String API_KEY_HEADER_ALT = "X-API-KEY";

	private final UsernamePasswordAuthenticationToken expectedAuth;

	/**
	 * Creates a filter bound to the expected API key authentication token.
	 *
	 * @param expectedAuth configured authentication token used to validate incoming
	 *                     API key requests
	 */
	public ApiKeyAuthFilter(UsernamePasswordAuthenticationToken expectedAuth) {
		this.expectedAuth = expectedAuth;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (expectedAuth == null) {
			filterChain.doFilter(request, response);
			return;
		}

		String headerValue = request.getHeader(API_KEY_HEADER);
		if (headerValue == null) {
			headerValue = request.getHeader(API_KEY_HEADER_ALT);
		}

		if (Objects.equals(expectedAuth.getCredentials(), headerValue)) {
			PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(
					expectedAuth.getPrincipal(), expectedAuth.getCredentials(), expectedAuth.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}
}
