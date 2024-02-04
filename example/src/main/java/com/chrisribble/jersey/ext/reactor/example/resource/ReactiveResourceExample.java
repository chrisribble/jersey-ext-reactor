package com.chrisribble.jersey.ext.reactor.example.resource;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chrisribble.jersey.ext.reactor.example.binding.Message;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Path("/example")
@Produces(MediaType.APPLICATION_JSON)
@Named
public class ReactiveResourceExample {
	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final Scheduler SCHEDULER = Schedulers.parallel();

	@Inject
	public ReactiveResourceExample() {
		// empty for now
	}

	@GET
	@Path("mono/{message}")
	public Mono<Message> pathParamEcho(@PathParam("message") final String message) {
		logThread();
		return Mono.just(new Message(message))
				.doOnNext(m -> logThread())
				.subscribeOn(SCHEDULER);
	}

	@POST
	@Path("mono")
	@Consumes(MediaType.APPLICATION_JSON)
	public Mono<Message> entityEcho(@Valid final Message message) {
		logThread();
		return Mono.just(message)
				.doOnNext(m -> logThread())
				.subscribeOn(SCHEDULER);
	}

	@GET
	@Path("flux")
	public Flux<Message> flux() {
		logThread();
		return Flux.just(new Message("mesage1"), new Message("message2"))
				.doOnNext(m -> logThread())
				.subscribeOn(SCHEDULER);
	}

	@POST
	@Path("flux")
	@Consumes(MediaType.APPLICATION_JSON)
	public Flux<Message> flux(final List<Message> messages) {
		logThread();
		return Flux.fromIterable(messages)
				.doOnNext(m -> logThread())
				.subscribeOn(SCHEDULER);
	}

	private void logThread() {
		LOG.info("{}", Thread.currentThread().getName());
	}
}
