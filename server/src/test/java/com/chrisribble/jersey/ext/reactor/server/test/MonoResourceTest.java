package com.chrisribble.jersey.ext.reactor.server.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Application;
import reactor.core.publisher.Mono;

class MonoResourceTest extends AbstractReactorJerseyTest {

	@Override
	protected Application configure() {
		return config().register(MaybeResource.class);
	}

	@Test
	void shouldReturnContent() {
		final String message = target("mono")
				.path("echo")
				.queryParam("message", "hello")
				.request()
				.get(String.class);

		assertEquals("hello", message);
	}

	@Test
	void shouldThrowOnErrorMono() {
		Invocation.Builder builder = target("mono")
				.path("error")
				.request();
		assertThrows(BadRequestException.class, () -> builder.get(String.class));
	}

	@Test
	void shouldReturnNoContentOnEmptyMono() {
		final int status = target("mono")
				.path("empty")
				.request()
				.get()
				.getStatus();

		assertEquals(204, status);
	}

	@Test
	void shouldThrowOnNullMono() {
		Invocation.Builder builder = target("mono")
				.path("npe")
				.request();
		assertThrows(InternalServerErrorException.class, () -> builder.get(String.class));
	}

	@Path("/mono")
	public static class MaybeResource {
		@GET
		@Path("echo")
		public Mono<String> echo(@QueryParam("message") final String message) {
			return Mono.just(message);
		}

		@GET
		@Path("echoList")
		public Mono<List<String>> echoList(@QueryParam("message") final String message) {
			return Mono.just(List.of(message));
		}

		@GET
		@Path("error")
		public Mono<String> error() {
			return Mono.error(new BadRequestException());
		}

		@GET
		@Path("npe")
		public Mono<String> npe() {
			return null;
		}

		@GET
		@Path("empty")
		public Mono<String> empty() {
			return Mono.empty();
		}
	}
}
