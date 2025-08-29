package com.bajaj.fintechchallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class FintechChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FintechChallengeApplication.class, args);
	}

    /**
     * Creates a RestTemplate bean to be used for making HTTP requests.
     * This bean is managed by the Spring container and can be injected elsewhere.
     * @return A new instance of RestTemplate.
     */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}