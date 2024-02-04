package com.chrisribble.jersey.ext.reactor.server.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Verify that reactive handlers do not interfere with non-reactive handlers
 */
class NonReactiveJsonResourceTest extends AbstractReactorJerseyTest {

	@Override
	protected Application configure() {
		return config().register(NonReactiveJsonResource.class);
	}

	@Test
	void shouldWriteJsonEntities() {
		Message message = target("json")
				.path("writeJson")
				.request(MediaType.APPLICATION_JSON)
				.get(Message.class);

		assertNotNull(message);
		assertEquals("hello", message.message);
	}

	@Test
	void shouldWriteJsonEntitiesList() {
		List<Message> messages = target("json")
				.path("writeListJson")
				.request(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<Message>>() {});

		assertNotNull(messages);
		assertEquals(2, messages.size());

		for (Message message : messages) {
			assertEquals("hello", message.message);
		}
	}

	@Test
	void shouldReadJsonEntities() {
		Message message = target("json")
				.path("readJson")
				.request(MediaType.APPLICATION_JSON)
				.post(Entity.json(new Message("hello")), Message.class);

		assertNotNull(message);
		assertEquals("hello", message.message);
	}

	@Test
	void shouldValidateJsonEntities() {
		try (Response response = target("json")
				.path("validateJson")
				.request(MediaType.APPLICATION_JSON)
				.post(Entity.json(new Message(null)))) {

			assertEquals(400, response.getStatus());
		}
	}

	@Path("/json")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public static class NonReactiveJsonResource {

		@GET
		@Path("writeJson")
		public Message writeJson() {
			return new Message("hello");
		}

		@GET
		@Path("writeListJson")
		public List<Message> writeListJson() {
			return List.of(new Message("hello"), new Message("hello"));
		}

		@POST
		@Path("readJson")
		public Message readJson(@NotNull final Message message) {
			return message;
		}

		@POST
		@Path("validateJson")
		public Message validateJson(@Valid final Message message) {
			return message;
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
