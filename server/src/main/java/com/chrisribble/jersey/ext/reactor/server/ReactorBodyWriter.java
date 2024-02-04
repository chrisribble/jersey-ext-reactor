package com.chrisribble.jersey.ext.reactor.server;

import jakarta.annotation.Priority;
import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * MessageBodyWriter accepting {@code reactor.core.publisher.Flux} and {@code reactor.core.publisher.Mono} types
 *
 * @see Flux
 * @see Mono
 */
@Singleton
@Priority(1) //Priority should be higher than JSON providers
public class ReactorBodyWriter extends ReactorGenericBodyWriter {
	public ReactorBodyWriter() {
		super(Flux.class, Mono.class);
	}
}
