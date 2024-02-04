package com.chrisribble.jersey.ext.reactor.server;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.message.MessageBodyWorkers;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;

/**
 * Generic {@link MessageBodyWriter} that overrides writer to serialise incoming entity as type of generic class. <br>
 * This class will only redirect writing to another {@link MessageBodyWriter} <br>
 * Requires list of supported types <br>
 * <p>
 * Providers implementing {@link ReactorGenericBodyWriter} must be programmatically registered in {@link InjectionManager}
 */
public abstract class ReactorGenericBodyWriter implements MessageBodyWriter<Object> {

	private final List<Class<?>> allowedTypes;

	@Inject
	private Provider<MessageBodyWorkers> workers;

	/**
	 * @param allowedTypes
	 *            list of types to be processed by this writer
	 */
	protected ReactorGenericBodyWriter(final Class<?>... allowedTypes) {
		this.allowedTypes = Arrays.asList(allowedTypes);
	}

	/**
	 * @param type
	 *            type to process
	 * @return the raw type without generics
	 */
	private static Class<?> raw(final Type type) {
		if (type instanceof Class<?>) {
			return (Class<?>) type;
		}

		if (type instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) type).getRawType();
		}

		return null; // needs an assigning type to resolve TypeVariable or GenericArrayType
	}

	/**
	 * @param genericType
	 *            type to process
	 * @return first type from generic list
	 */
	private static Type actual(final Type genericType) {
		final ParameterizedType actualGenericType = (ParameterizedType) genericType;
		return actualGenericType.getActualTypeArguments()[0];
	}

	@Override
	public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
		if (genericType instanceof ParameterizedType) {
			Class<?> rawType = raw(genericType);

			final Type actualTypeArgument = actual(genericType);
			final MessageBodyWriter<?> messageBodyWriter = workers.get()
					.getMessageBodyWriter(raw(actualTypeArgument), actualTypeArgument, annotations, mediaType);

			return allowedTypes.contains(rawType) && messageBodyWriter != null;
		}
		return allowedTypes.contains(genericType);
	}

	@Override
	public long getSize(final Object o, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
		return 0; //skip
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void writeTo(final Object entity, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType,
			final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream)
			throws IOException, WebApplicationException {

		final Type actualTypeArgument = actual(genericType);
		final MessageBodyWriter writer = workers.get().getMessageBodyWriter(entity.getClass(), actualTypeArgument, annotations, mediaType);

		writer.writeTo(entity, entity.getClass(), actualTypeArgument, annotations, mediaType, httpHeaders, entityStream);
	}
}
