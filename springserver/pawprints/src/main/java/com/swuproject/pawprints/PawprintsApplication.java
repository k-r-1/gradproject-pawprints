package com.swuproject.pawprints;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.swuproject.pawprints.repository")
public class PawprintsApplication {
	// test
	public static void main(String[] args) {
		SpringApplication.run(PawprintsApplication.class, args);
	}

}