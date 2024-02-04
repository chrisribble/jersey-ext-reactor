package com.chrisribble.jersey.ext.reactor.server.test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import reactor.core.publisher.Mono;

class MissingBodyWriterTest extends AbstractReactorJerseyTest {

	@Override
	protected Application configure() {
		return config().register(XmlResource.class);
	}

	@Test
	void shouldReturnInternalServerError() {
		Invocation.Builder builder = target("xml")
				.path("writeXml")
				.request();
		assertThrows(InternalServerErrorException.class, () -> builder.get(Message.class));
	}

	@Path("/xml")
	@Produces(MediaType.APPLICATION_XML)
	public static class XmlResource {

		@GET
		@Path("writeXml")
		public Mono<Message> writeXml() {
			return Mono.just(new Message("hello"));
		}

	}

	public static class Message {

		@NotNull
		public String message;

		@JsonCreator
		public Message(@JsonProperty("message") final String message) {
			this.message = message;
		}
	}
}
