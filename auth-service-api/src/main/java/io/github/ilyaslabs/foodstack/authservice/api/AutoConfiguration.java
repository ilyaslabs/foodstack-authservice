package io.github.ilyaslabs.foodstack.authservice.api;

import io.github.ilyaslabs.foodstack.authservice.api.v1.AuthApiV1;
import org.springframework.web.service.registry.ImportHttpServices;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@ImportHttpServices(group = "auth-service", types = {AuthApiV1.class})
public class AutoConfiguration {
}
