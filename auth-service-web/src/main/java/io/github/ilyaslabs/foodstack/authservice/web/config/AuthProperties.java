package io.github.ilyaslabs.foodstack.authservice.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@Data
@ConfigurationProperties("auth")
public class AuthProperties {

    private String issuer;
}
