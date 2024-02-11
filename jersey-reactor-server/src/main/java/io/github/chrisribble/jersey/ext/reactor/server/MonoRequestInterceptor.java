package io.github.chrisribble.jersey.ext.reactor.server;

import reactor.core.publisher.Mono;

/**
 * {@link ReactorRequestInterceptor} returning {@link reactor.core.publisher.Mono}
 */
public interface MonoRequestInterceptor extends ReactorRequestInterceptor<Mono<?>> {

}
