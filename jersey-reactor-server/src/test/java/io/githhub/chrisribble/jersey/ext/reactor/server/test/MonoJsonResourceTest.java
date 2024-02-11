package io.githhub.chrisribble.jersey.ext.reactor.server.test;

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
import reactor.core.publisher.Mono;

class MonoJsonResourceTest extends AbstractReactorJerseyTest {

	@Override
	protected Application configure() {
		return config().register(JsonResource.class);
	}

	@Test
	void shouldWriteJsonEntities() {
		Message message = target("json")
				.path("writeJson")
				.request(MediaType.APPLICATION_JSON)
				.get(Message.class);

		assertEquals("hello", message.message);
	}

	@Test
	void shouldWriteJsonEntitiesList() {
		List<Message> messages = target("json")
				.path("writeJsonList")
				.request(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<Message>>() {});

		assertNotNull(messages);
		assertEquals(1, messages.size());

		Message message = messages.get(0);
		assertEquals("hello", message.message);
	}

	@Test
	void shouldReadJsonEntities() {
		Message message = target("json")
				.path("readJson")
				.request(MediaType.APPLICATION_JSON)
				.post(Entity.json(new Message("hello")))
				.readEntity(Message.class);

		assertEquals("hello", message.message);
	}

	@Test
	void shouldValidateJsonEntities() {
		Response response = target("json")
				.path("validateJson")
				.request(MediaType.APPLICATION_JSON)
				.post(Entity.json(new Message(null)));

		assertEquals(400, response.getStatus());
	}

	@Path("/json")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public static class JsonResource {

		@GET
		@Path("writeJson")
		public Mono<Message> writeJson() {
			return Mono.just(new Message("hello"));
		}

		@GET
		@Path("writeJsonList")
		public Mono<List<Message>> writeJsonList() {
			return Mono.just(List.of(new Message("hello")));
		}

		@POST
		@Path("readJson")
		public Mono<Message> readJson(@NotNull final Message message) {
			return Mono.just(message);
		}

		@POST
		@Path("validateJson")
		public Mono<Message> validateJson(@Valid final Message message) {
			return Mono.just(message);
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
