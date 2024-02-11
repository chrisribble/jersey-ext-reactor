package io.githhub.chrisribble.jersey.ext.reactor.server.test;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import io.github.chrisribble.jersey.ext.reactor.server.ReactorServerFeature;

public abstract class AbstractReactorJerseyTest extends JerseyTest {

	protected ResourceConfig config() {
		return new ResourceConfig()
				.register(JacksonFeature.class)
				.register(ReactorServerFeature.class);
	}

	@Override
	protected void configureClient(final ClientConfig config) {
		config.register(JacksonFeature.class);
	}
}
