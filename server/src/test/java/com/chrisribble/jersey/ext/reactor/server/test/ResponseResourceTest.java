package com.chrisribble.jersey.ext.reactor.server.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import reactor.core.publisher.Mono;

class ResponseResourceTest extends AbstractReactorJerseyTest {

	@Override
	protected Application configure() {
		return config().register(ResponseResource.class);
	}

	@Test
	void shouldHandleResponseReturnType() {
		final String message = target("response")
				.path("hello")
				.request()
				.get(String.class);

		assertEquals("hello", message);
	}

	@Path("/response")
	public static class ResponseResource {

		@GET
		@Path("hello")
		public Mono<Response> get() {
			return Mono.just(Response.ok("hello").build());
		}
	}
}
