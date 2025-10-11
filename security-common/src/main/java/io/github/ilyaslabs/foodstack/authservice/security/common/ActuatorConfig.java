package io.github.ilyaslabs.foodstack.authservice.security.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@ConfigurationProperties("auth.actuator")
@Data
class ActuatorConfig {
    private boolean enabled = false;

}
