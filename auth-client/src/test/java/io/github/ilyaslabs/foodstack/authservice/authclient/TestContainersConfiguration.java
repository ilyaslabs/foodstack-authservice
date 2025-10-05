package io.github.ilyaslabs.foodstack.authservice.authclient;

import org.mockserver.client.MockServerClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@TestConfiguration(proxyBeanMethods = false)
class TestContainersConfiguration {

    @Bean
    MockServerContainer mockServerContainer() {
        return new MockServerContainer(DockerImageName
                .parse("mockserver/mockserver:5.15.0"));
    }

    @Bean
    MockServerClient mockServerClient(MockServerContainer mockServerContainer) {
        return new MockServerClient(mockServerContainer.getHost(), mockServerContainer.getServerPort());
    }
}
