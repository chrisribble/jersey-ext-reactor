package com.chrisribble.jersey.ext.reactor.example.config.spring;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.context.annotation.Configuration;

import com.chrisribble.jersey.ext.reactor.example.resource.ReactiveResourceExample;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;

import io.github.chrisribble.jersey.ext.reactor.server.ReactorServerFeature;

@Configuration
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		property(ServerProperties.WADL_FEATURE_DISABLE, true);

		// Response providers
		register(JacksonJsonProvider.class);
		register(ReactorServerFeature.class);

		// Resources
		register(ReactiveResourceExample.class);
	}
}
