package com.chrisribble.jersey.ext.reactor.server.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import reactor.core.publisher.Flux;

class FluxResourceTest extends AbstractReactorJerseyTest {
	private static final GenericType<List<String>> STRING_LIST_TYPE = new GenericType<>() {};

	@Override
	protected Application configure() {
		return config().register(FlowableResource.class);
	}

	@Test
	void shouldReturnContent() {
		List<String> multiple = target("flux").path("echo")
				.queryParam("message", "hello")
				.request(MediaType.APPLICATION_JSON)
				.get(STRING_LIST_TYPE);

		assertNotNull(multiple);
		assertEquals(1, multiple.size());
	}

	@Test
	void shouldReturnMultiple() {
		List<String> multiple = target("flux").path("multiple")
				.request(MediaType.APPLICATION_JSON)
				.get(STRING_LIST_TYPE);
		assertNotNull(multiple);
		assertEquals(2, multiple.size());
	}

	@Test
	void shouldReturnOkOnEmptyFlux() {
		try (Response response = target("flux").path("empty")
				.request(MediaType.APPLICATION_JSON)
				.get()) {
			List<String> multiple = response.readEntity(STRING_LIST_TYPE);
			assertNotNull(multiple);
			assertTrue(multiple.isEmpty());
			assertEquals(Status.OK.getStatusCode(), response.getStatus());
		}
	}

	@Test
	void shouldThrowOnNullFlux() {
		Invocation.Builder builder = target("flux").path("npe")
				.request(MediaType.APPLICATION_JSON);
		assertThrows(InternalServerErrorException.class, () -> builder.get(String.class));
	}

	@Path("/flux")
	public static class FlowableResource {

		@GET
		@Path("echo")
		public Flux<String> echo(@QueryParam("message") final String message) {
			return Flux.just(message);
		}

		@GET
		@Path("empty")
		public Flux<String> empty() {
			return Flux.empty();
		}

		@GET
		@Path("npe")
		public Flux<String> npe() {
			return null;
		}

		@GET
		@Path("multiple")
		public Flux<String> multiple() {
			return Flux.just("hello", "reactor");
		}

	}
}
