package io.githhub.chrisribble.jersey.ext.reactor.server.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.junit.jupiter.api.Test;

import io.github.chrisribble.jersey.ext.reactor.server.MonoRequestInterceptor;
import jakarta.inject.Singleton;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import reactor.core.publisher.Mono;

class InterceptorsTest extends AbstractReactorJerseyTest {

	@Override
	protected Application configure() {
		return config()
				.register(EchoResource.class)
				.register(new Binder());
	}

	@Test
	void shouldInterceptRequests() {
		String message = target("interceptors")
				.path("echo")
				.request()
				.header("message", "hello")
				.get(String.class);

		assertEquals("intercepted", message);
	}

	@Test
	void shouldHandleInterceptorException() {
		Invocation.Builder builder = target("interceptors")
				.path("error")
				.request()
				.header("throw", true);
		assertThrows(NotAuthorizedException.class, () -> builder.get(String.class));
	}

	@Test
	void shouldHandleInterceptorError() {
		Invocation.Builder builder = target("interceptors")
				.path("error")
				.request()
				.header("error", true);

		assertThrows(BadRequestException.class, () -> builder.get(String.class));
	}

	@Path("/interceptors")
	public static class EchoResource {

		@GET
		@Path("echo")
		public Mono<String> echo(@Context final ContainerRequestContext request) {
			return Mono.just(request.getProperty("message").toString());
		}

		@GET
		@Path("error")
		public Mono<String> error() {
			return Mono.empty();
		}
	}

	public static class Interceptor implements MonoRequestInterceptor {

		@Context
		private SecurityContext securityContext;

		@Override
		public Mono<?> intercept(final ContainerRequestContext requestContext) {
			return Mono.defer(() -> {
				requestContext.setProperty("message", "intercepted");
				return Mono.just(requestContext);
			});
		}
	}

	public static class ThrowingInterceptor implements MonoRequestInterceptor {

		@Override
		public Mono<?> intercept(final ContainerRequestContext requestContext) {
			if (requestContext.getHeaders().containsKey("throw")) {
				throw new NotAuthorizedException("Surprise!");
			}
			return Mono.empty();
		}
	}

	public static class EmptyInterceptor implements MonoRequestInterceptor {

		@Override
		public Mono<?> intercept(final ContainerRequestContext requestContext) {
			return Mono.empty();
		}
	}

	public static class ErrorInterceptor implements MonoRequestInterceptor {

		@Override
		public Mono<?> intercept(final ContainerRequestContext requestContext) {
			if (requestContext.getHeaders().containsKey("error")) {
				return Mono.error(new BadRequestException("Surprise!"));
			}
			return Mono.empty();
		}
	}

	public static class Binder extends AbstractBinder {

		@Override
		protected void configure() {
			Stream.of(Interceptor.class, EmptyInterceptor.class, ThrowingInterceptor.class, ErrorInterceptor.class).forEach(interceptor -> {
				bind(interceptor)
						.to(MonoRequestInterceptor.class)
						.in(Singleton.class);
			});

		}
	}

}
