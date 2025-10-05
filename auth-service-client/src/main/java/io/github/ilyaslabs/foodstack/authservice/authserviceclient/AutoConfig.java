package io.github.ilyaslabs.foodstack.authservice.authserviceclient;

import io.github.ilyaslabs.foodstack.authservice.api.v1.AuthApiV1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@Configuration
@ComponentScan("io.github.ilyaslabs.foodstack.authservice.authserviceclient")
@ConfigurationPropertiesScan("io.github.ilyaslabs.foodstack.authservice.authserviceclient")
@Slf4j
public class AutoConfig {

    /**
     * RestClient bean for {@link AuthApiV1} making http calls
     */
    @Bean
    AuthApiV1 authApiV1Client(AuthConfig authConfig) {
        log.info("Initializing AuthApiV1 RestClient with baseUrl: {}", authConfig.getBaseUrl());

        RestClient restClient = RestClient.builder()
                .baseUrl(authConfig.getBaseUrl())
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();

        log.info("AuthApiV1 RestClient has been initialized");
        return factory.createClient(AuthApiV1.class);
    }

}
