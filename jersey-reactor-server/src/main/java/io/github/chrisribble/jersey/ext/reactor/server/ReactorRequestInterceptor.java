package io.github.chrisribble.jersey.ext.reactor.server;

import jakarta.ws.rs.container.ContainerRequestContext;

/**
 * Contract for dispatch request interceptor <br>
 * Interceptors implementing {@link ReactorRequestInterceptor} should be programmatically registered in {@link org.glassfish.jersey.internal.inject.InjectionManager}
 *
 * @param <T> return type of interceptor (Supposed to be Observable or Future)
 */
public interface ReactorRequestInterceptor<T> {

    /**
     * This method will be called for each request and should be implemented as non-blocking
     *
     * @param requestContext request context
     * @return Future or Observable
     */
    T intercept(ContainerRequestContext requestContext);

}
