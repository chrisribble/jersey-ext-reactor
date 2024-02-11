package io.github.chrisribble.jersey.ext.reactor.server;

import java.util.LinkedList;
import java.util.List;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.spi.internal.ResourceMethodInvocationHandlerProvider;

import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

/**
 * See <a href="https://github.com/chrisribble/jersey-ext-reactor">Github Repository</a>
 */
public final class ReactorServerFeature implements Feature {

	private final List<Class<? extends MonoRequestInterceptor>> interceptors = new LinkedList<>();

	/**
	 * Create an unconfigured instance
	 */
	public ReactorServerFeature() {
		// not configured yet
	}

	/**
	 * Registers an interceptor
	 *
	 * @param interceptor
	 *            to register
	 * @return configured Feature
	 */
	public ReactorServerFeature register(final Class<? extends MonoRequestInterceptor> interceptor) {
		interceptors.add(interceptor);
		return this;
	}

	@Override
	public boolean configure(final FeatureContext context) {
		context.register(ReactorBodyWriter.class);
		context.register(new Binder());
		return true;
	}

	private class Binder extends AbstractBinder {

		@Override
		protected void configure() {
			bind(ReactorInvocationHandlerProvider.class)
					.to(ResourceMethodInvocationHandlerProvider.class)
					.in(Singleton.class);

			interceptors.forEach(interceptor -> bind(interceptor)
					.to(MonoRequestInterceptor.class)
					.in(Singleton.class));
		}
	}
}
