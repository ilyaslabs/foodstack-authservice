package io.github.ilyaslabs.foodstack.authservice.authclient;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configurations for auth client
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@ConfigurationProperties("auth.client")
@Data
class AuthClientConfig {
    String tokenUrl;
    String refreshTokenUrl;
    String username;
    String password;
}