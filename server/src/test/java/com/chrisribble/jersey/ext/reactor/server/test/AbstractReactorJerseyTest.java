package com.chrisribble.jersey.ext.reactor.server.test;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import com.chrisribble.jersey.ext.reactor.server.ReactorJerseyServerFeature;

public abstract class AbstractReactorJerseyTest extends JerseyTest {

	protected ResourceConfig config() {
		return new ResourceConfig()
				.register(JacksonFeature.class)
				.register(ReactorJerseyServerFeature.class);
	}

	@Override
	protected void configureClient(final ClientConfig config) {
		config.register(JacksonFeature.class);
	}
}
