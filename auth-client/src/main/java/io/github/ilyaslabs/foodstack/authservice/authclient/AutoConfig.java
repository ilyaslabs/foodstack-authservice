package io.github.ilyaslabs.foodstack.authservice.authclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestClient;

import java.time.Clock;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@Configuration
@ComponentScan("io.github.ilyaslabs.foodstack.authservice.authclient")
@ConfigurationPropertiesScan("io.github.ilyaslabs.foodstack.authservice.authclient")
@Slf4j
@EnableRetry
public class AutoConfig {

    /**
     * Provides a Clock bean set to the system's UTC time zone.
     * This bean is only created if no other Clock bean is present
     * in the application context.
     *
     * @return a Clock instance set to UTC
     */
    @Bean
    @ConditionalOnMissingBean(Clock.class)
    Clock clock() {
        log.info("Clock has been initialized");
        return Clock.systemUTC();
    }

    /**
     * Configures and provides a RestClient bean with a request interceptor
     * that automatically sets a Bearer token for authentication. The token
     * is retrieved from the provided TokenService.
     *
     * @param tokenService the service responsible for providing the authentication token
     * @return a configured RestClient instance
     */
    @Bean("authRestClient")
    @ConditionalOnMissingBean(name = "authRestClient")
    RestClient authRestClient(TokenService tokenService) {
        log.info("Auth RestClient has been initialized");
        return RestClient.builder()
                .requestInterceptor(((request, body, execution) -> {
                    request.getHeaders().setBearerAuth(tokenService.getToken());
                    return execution.execute(request, body);
                }))
                .build();
    }
}
