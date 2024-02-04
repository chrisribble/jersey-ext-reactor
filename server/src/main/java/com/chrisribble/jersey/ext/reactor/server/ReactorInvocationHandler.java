package com.chrisribble.jersey.ext.reactor.server;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.jersey.server.AsyncContext;
import org.glassfish.jersey.server.internal.LocalizationMessages;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Provides {@link InvocationHandler} for resources returning {@code reactor.core.publisher.*} instances
 * and converts them to {@link Mono}
 */
public abstract class ReactorInvocationHandler<T, R> implements InvocationHandler {
	private static final Supplier<Mono<Response>> NO_CONTENT_SUPPLIER = () -> Mono.just(Response.noContent().build());

	@Inject
	private Provider<AsyncContext> asyncContextProvider;

	@Inject
	private Provider<ContainerRequestContext> requestContextProvider;

	@Inject
	private IterableProvider<MonoRequestInterceptor> requestInterceptors;

	@Override
	@SuppressWarnings("unchecked")
	public Object invoke(final Object proxy, final Method method, final Object[] args) {
		AsyncContext asyncContext = suspend();
		ContainerRequestContext requestContext = requestContextProvider.get();

		Mono<Void> intercept = Flux.fromIterable(requestInterceptors)
				.flatMap(interceptor -> interceptor.intercept(requestContext))
				.then();

		Mono<R> invoke = Mono.defer(() -> {
			try {
				return convert((T) method.invoke(proxy, args));
			} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
				return Mono.error(e);
			}
		});

		Mono<Response> noContent = Mono.defer(NO_CONTENT_SUPPLIER);

		intercept.then(invoke)
				.map(this::getResponse)
				.switchIfEmpty(noContent)
				.subscribe(asyncContext::resume, asyncContext::resume);

		// async methods return null
		return null;
	}

	private Response getResponse(final Object result) {
		if (result instanceof Response) {
			return (Response) result;
		}
		return Response.ok(result).build();
	}

	/**
	 * Uses {@link AsyncContext} to suspend current request
	 *
	 * @return obtained {@link AsyncContext} or throws error
	 */
	protected AsyncContext suspend() {
		AsyncContext asyncContext = asyncContextProvider.get();
		if (!asyncContext.suspend()) {
			throw new ProcessingException(LocalizationMessages.ERROR_SUSPENDING_ASYNC_REQUEST());
		}

		return asyncContext;
	}

	/**
	 * Converts method result into Mono
	 *
	 * @param result
	 *            method call result
	 * @return converted value
	 */
	protected abstract Mono<R> convert(T result);
}
