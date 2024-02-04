package com.chrisribble.jersey.ext.reactor.example.config.spring;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.chrisribble.jersey.ext.reactor.example.filter.WebClientRequestLogFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.blackbird.BlackbirdModule;

import reactor.netty.http.client.HttpClient;

@Configuration
@ImportAutoConfiguration(value = {
		ServletWebServerFactoryAutoConfiguration.class,
		JerseyAutoConfiguration.class,
		ValidationAutoConfiguration.class,
})
@ComponentScan("com.chrisribble.jersey.ext.reactor.example")
public class ExampleSpringConfig {
	@Bean
	public ObjectMapper getObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new BlackbirdModule());
		mapper.registerModule(new JavaTimeModule());

		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);

		return mapper;
	}

	@Bean
	public WebClient getWebClient() {
		HttpClient httpClient = HttpClient.create()
				.wiretap(true);
		return WebClient.builder()
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.filter(ExchangeFilterFunction.ofRequestProcessor(new WebClientRequestLogFilter()))
				.build();
	}
}
