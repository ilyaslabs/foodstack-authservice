package io.github.ilyaslabs.foodstack.authservice.authserviceclient;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@ConfigurationProperties("auth")
@Data
class AuthConfig {

    private String baseUrl;
}
