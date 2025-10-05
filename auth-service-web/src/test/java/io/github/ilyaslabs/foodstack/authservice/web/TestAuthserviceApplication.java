package io.github.ilyaslabs.foodstack.authservice.web;

import org.springframework.boot.SpringApplication;

public class TestAuthserviceApplication {

	static void main(String[] args) {
		SpringApplication.from(AuthserviceApplication::main).with(TestContainersConfiguration.class).run(args);
	}

}
