package com.example.fakeshopapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FakeshopapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FakeshopapiApplication.class, args);
	}

}
