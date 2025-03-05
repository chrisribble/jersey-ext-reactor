package io.github.chrisribble.jersey.ext.reactor.server;

import jakarta.annotation.Priority;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
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
@Produces({ MediaType.APPLICATION_JSON, "text/json", MediaType.WILDCARD })
public class ReactorBodyWriter extends ReactorGenericBodyWriter {
	/**
	 * Automatically instantiated by Jersey; do not manually invoke
	 */
	public ReactorBodyWriter() {
		super(Flux.class, Mono.class);
	}
}
