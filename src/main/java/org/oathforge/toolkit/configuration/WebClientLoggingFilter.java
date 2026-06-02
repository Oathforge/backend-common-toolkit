package org.oathforge.toolkit.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import reactor.core.publisher.Mono;

/**
 * Factory for reusable request and response logging filters for
 * {@code WebClient}.
 * <p>
 * These filters are intended for troubleshooting and integration support, and
 * only produce output when trace logging is enabled.
 */
public class WebClientLoggingFilter {

	private static final Logger logger = LoggerFactory.getLogger(WebClientLoggingFilter.class);

	private WebClientLoggingFilter() {

	}

	/**
	 * Creates a filter that logs outgoing requests at trace level, including
	 * method, target URL, and headers.
	 *
	 * @return request logging filter
	 */
	public static ExchangeFilterFunction logRequest() {
		return ExchangeFilterFunction.ofRequestProcessor(request -> {
			if (logger.isTraceEnabled()) {
				logger.trace("Request: {} {}", request.method(), request.url());
				request.headers()
						.forEach((name, values) -> values.forEach(value -> logger.trace("{}: {}", name, value)));
			}
			return Mono.just(request);
		});
	}

	/**
	 * Creates a filter that logs incoming responses at trace level, including
	 * status code and response headers.
	 *
	 * @return response logging filter
	 */
	public static ExchangeFilterFunction logResponse() {
		return ExchangeFilterFunction.ofResponseProcessor(response -> {
			if (logger.isTraceEnabled()) {
				logger.trace("Response Status: {}", response.statusCode());
				response.headers().asHttpHeaders().forEach((key, value) -> logger.trace("{}: {}", key, value));
			}
			return Mono.just(response);
		});
	}
}
