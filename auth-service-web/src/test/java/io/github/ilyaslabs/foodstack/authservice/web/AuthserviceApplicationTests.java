package io.github.ilyaslabs.foodstack.authservice.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestContainersConfiguration.class)
@SpringBootTest
class AuthserviceApplicationTests {

	@Test
	void contextLoads() {
	}

}
