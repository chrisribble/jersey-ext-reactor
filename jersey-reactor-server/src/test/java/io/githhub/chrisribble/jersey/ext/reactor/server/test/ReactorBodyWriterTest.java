package io.githhub.chrisribble.jersey.ext.reactor.server.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.inject.hk2.Hk2InjectionManagerFactory;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.MapPropertiesDelegate;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.message.internal.MessageBodyFactory;
import org.glassfish.jersey.message.internal.MessageBodyFactory.MessageBodyWorkersConfigurator;
import org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException;
import org.glassfish.jersey.message.internal.MessagingBinders;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerBootstrapBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.chrisribble.jersey.ext.reactor.server.ReactorBodyWriter;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import reactor.core.publisher.Mono;

class ReactorBodyWriterTest {

	private InjectionManager injectionManager;

	@BeforeEach
	void setUp() {
		injectionManager = new Hk2InjectionManagerFactory().create();
		injectionManager.register(
				new MessagingBinders.MessageBodyProviders(null, null));
		injectionManager.register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(ReactorBodyWriter.class).to(MessageBodyWriter.class).in(Singleton.class);
			}
		});

		BootstrapBag bootstrapBag = new ServerBootstrapBag();
		bootstrapBag.setConfiguration(new ResourceConfig());

		MessageBodyWorkersConfigurator workerConfig = new MessageBodyWorkersConfigurator();
		workerConfig.init(injectionManager, bootstrapBag);
		injectionManager.completeRegistration();
		workerConfig.postInit(injectionManager, bootstrapBag);
	}

	@Test
	void shouldWriteMessageBody() throws NoSuchMethodException, IOException {
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		testWriteTo("text", MediaType.TEXT_PLAIN_TYPE, "test", outputStream);

		final String response = new String(outputStream.toByteArray());

		assertEquals("test", response);
	}

	@Test
	void shouldThrowMessageBodyProviderNotFoundException() {
		var message = new Message("test");
		var outputStream = OutputStream.nullOutputStream();

		assertThrows(MessageBodyProviderNotFoundException.class,
				() -> testWriteTo("xml", MediaType.APPLICATION_XML_TYPE, message, outputStream));
	}

	private OutputStream testWriteTo(final String methodName, final MediaType mediaType, final Object entity, final OutputStream outputStream)
			throws NoSuchMethodException, IOException {

		final MessageBodyFactory messageBodyFactory = injectionManager.getInstance(MessageBodyFactory.class);

		final Method textMethod = TestResource.class.getMethod(methodName);
		final Type genericReturnType = textMethod.getGenericReturnType();
		final Annotation[] annotations = textMethod.getDeclaredAnnotations();

		final MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>(
				Collections.singletonMap(HttpHeaders.CONTENT_TYPE, Collections.singletonList(mediaType)));

		return messageBodyFactory.writeTo(entity, entity.getClass(), genericReturnType, annotations, mediaType, headers,
				new MapPropertiesDelegate(), outputStream, Collections.emptyList());
	}

	@Path("/resource")
	public interface TestResource {

		@GET
		@Path("xml")
		@Produces(MediaType.APPLICATION_XML)
		Mono<Message> xml();

		@GET
		@Path("text")
		@Produces(MediaType.TEXT_PLAIN)
		Mono<String> text();

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