package com.chrisribble.jersey.ext.reactor.example.filter;

import java.lang.invoke.MethodHandles;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ClientRequest;

import reactor.core.publisher.Mono;

public class WebClientRequestLogFilter implements Function<ClientRequest, Mono<ClientRequest>> {
	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public Mono<ClientRequest> apply(final ClientRequest request) {
		LOG.info("{} {}", request.method(), request.url());
		request.headers().forEach((name, values) -> values.forEach(value -> LOG.info("{}={}", name, value)));
		return Mono.just(request);
	}
}
