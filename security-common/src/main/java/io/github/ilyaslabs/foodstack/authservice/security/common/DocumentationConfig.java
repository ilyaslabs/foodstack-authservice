package io.github.ilyaslabs.foodstack.authservice.security.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@ConfigurationProperties("auth.doc")
@Data
class DocumentationConfig {
    private boolean enabled = false;
}
