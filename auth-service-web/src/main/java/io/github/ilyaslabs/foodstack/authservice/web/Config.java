package io.github.ilyaslabs.foodstack.authservice.web;

import io.github.ilyaslabs.microservice.security.guard.annotation.EnableMicroserviceSecurity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.time.Clock;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@Configuration
@EnableMongoRepositories(basePackages = "io.github.ilyaslabs.foodstack.authservice.web.repository")
@ConfigurationPropertiesScan(basePackages = "io.github.ilyaslabs.foodstack.authservice.web.config")
@ComponentScan(basePackages = "io.github.ilyaslabs.foodstack.authservice.web")
@EnableMicroserviceSecurity
class Config {

    @Bean
    @ConditionalOnMissingBean
    Clock clock() {
        return Clock.systemUTC();
    }
}
