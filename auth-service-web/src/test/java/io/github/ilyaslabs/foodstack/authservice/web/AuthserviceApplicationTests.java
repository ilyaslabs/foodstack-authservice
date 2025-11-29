package io.github.ilyaslabs.foodstack.authservice.web;

import io.github.ilyaslabs.microservice.test.common.FixedClockConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import({TestContainersConfiguration.class})
@SpringBootTest(classes = FixedClockConfiguration.class)
class AuthserviceApplicationTests {

	@Test
	void contextLoads() {
	}

}
