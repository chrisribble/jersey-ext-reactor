package io.github.chrisribble.jersey.ext.reactor.server;

import java.lang.reflect.InvocationHandler;
import java.util.List;
import java.util.Map;

import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.model.Invocable;
import org.glassfish.jersey.server.spi.internal.ResourceMethodInvocationHandlerProvider;

import jakarta.inject.Inject;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Provides {@link InvocationHandler} for resources returning {@code reactor.core.publisher.*} instances
 * and converts them to {@link Mono}
 */
public class ReactorInvocationHandlerProvider implements ResourceMethodInvocationHandlerProvider {

	private final Map<Class<?>, Class<? extends InvocationHandler>> handlers;

	@Inject
	private InjectionManager injectionManager;

	/**
	 * Creates a new handler provider with default reactor type to handler mappings
	 */
	public ReactorInvocationHandlerProvider() {
		handlers = Map.of(
				Flux.class, FluxHandler.class,
				Mono.class, MonoHandler.class);
	}

	@Override
	public InvocationHandler create(final Invocable invocable) {
		Class<?> returnType = invocable.getRawResponseType();
		if (handlers.containsKey(returnType)) {
			return injectionManager.createAndInitialize(handlers.get(returnType));
		}
		return null;
	}

	private static class FluxHandler<T> extends ReactorInvocationHandler<Flux<T>, List<T>> {
		@Override
		public Mono<List<T>> convert(final Flux<T> result) {
			return result.collectList();
		}
	}

	private static class MonoHandler<T> extends ReactorInvocationHandler<Mono<T>, T> {
		@Override
		public Mono<T> convert(final Mono<T> result) {
			return result;
		}
	}
}
