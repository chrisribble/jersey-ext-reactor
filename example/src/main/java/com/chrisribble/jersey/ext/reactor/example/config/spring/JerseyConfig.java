package com.chrisribble.jersey.ext.reactor.example.config.spring;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.context.annotation.Configuration;

import com.chrisribble.jersey.ext.reactor.example.resource.ReactiveResourceExample;
import com.chrisribble.jersey.ext.reactor.server.ReactorJerseyServerFeature;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;

@Configuration
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		property(ServerProperties.WADL_FEATURE_DISABLE, true);

		// Response providers
		register(JacksonJsonProvider.class);
		register(ReactorJerseyServerFeature.class);

		// Resources
		register(ReactiveResourceExample.class);
	}
}
