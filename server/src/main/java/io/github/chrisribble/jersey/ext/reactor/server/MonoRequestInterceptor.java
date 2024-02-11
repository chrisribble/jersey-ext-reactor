package io.github.chrisribble.jersey.ext.reactor.server;

import reactor.core.publisher.Mono;

/**
 * {@link ReactorRequestInterceptor} returning {@link io.reactivex.Completable}
 */
public interface MonoRequestInterceptor extends ReactorRequestInterceptor<Mono<?>> {

}
