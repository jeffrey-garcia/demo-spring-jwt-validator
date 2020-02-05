package com.jeffrey.example.demospringjwtvalidator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DemoSpringJwtValidatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoSpringJwtValidatorApplication.class, args);
	}

	@Bean("restTemplate")
	public RestTemplate rest() {
		return new RestTemplateBuilder().build();
	}

}
