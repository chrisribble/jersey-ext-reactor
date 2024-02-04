package com.chrisribble.jersey.ext.reactor.example.boot;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.chrisribble.jersey.ext.reactor.example.config.spring.ExampleSpringConfig;

public class ExampleApplication {
	public static void main(final String[] argv) {
		new SpringApplicationBuilder()
				.web(WebApplicationType.SERVLET)
				.registerShutdownHook(false)
				.bannerMode(Mode.OFF)
				.sources(ExampleSpringConfig.class)
				.run(argv);
	}
}
