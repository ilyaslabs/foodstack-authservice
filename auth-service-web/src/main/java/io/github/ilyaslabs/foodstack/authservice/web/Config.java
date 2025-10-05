package io.github.ilyaslabs.foodstack.authservice.web;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)¬
 */
@Configuration
@EnableMongoRepositories(basePackages = "io.github.ilyaslabs.foodstack.authservice.web.repository")
@ConfigurationPropertiesScan(basePackages = "io.github.ilyaslabs.foodstack.authservice.web.config")
@ComponentScan(basePackages = "io.github.ilyaslabs.foodstack.authservice.web")
class Config {
}
