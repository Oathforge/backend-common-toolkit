package com.backendtoolkit.common.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import reactor.core.publisher.Mono;

public class WebClientLoggingFilter {

	private static final Logger logger = LoggerFactory.getLogger(WebClientLoggingFilter.class);

	private WebClientLoggingFilter() {

	}

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