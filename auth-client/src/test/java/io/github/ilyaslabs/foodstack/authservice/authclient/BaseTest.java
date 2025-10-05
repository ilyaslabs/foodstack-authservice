package io.github.ilyaslabs.foodstack.authservice.authclient;

import io.github.ilyaslabs.foodstack.authservice.test.FixedClockConfiguration;
import io.github.ilyaslabs.foodstack.authservice.test.MutableClock;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@SpringBootTest(classes = AutoConfig.class)
@ImportTestcontainers(TestContainersConfiguration.class)
@Import(FixedClockConfiguration.class)
abstract class BaseTest {

    @Autowired
    protected AuthClientConfig authClientConfig;

    @Autowired
    protected MockServerClient mockServerClient;

    @Autowired
    protected MockServerContainer mockServerContainer;

    @Autowired
    MutableClock clock;

    protected ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        authClientConfig.setTokenUrl(getAuthTokenUrl());
        authClientConfig.setRefreshTokenUrl(getRefreshTokenUrl());
    }

    protected String getAuthTokenUrl() {
        return mockServerContainer.getEndpoint() + "/auth/token";
    }

    protected String getRefreshTokenUrl() {
        return mockServerContainer.getEndpoint() + "/auth/refresh-token";
    }

    /**
     * Convert object to json string
     *
     * @param obj object
     * @return json string
     */
    protected String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
