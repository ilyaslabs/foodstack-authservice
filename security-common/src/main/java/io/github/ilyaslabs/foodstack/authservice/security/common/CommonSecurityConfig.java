package io.github.ilyaslabs.foodstack.authservice.security.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@Configuration
public class CommonSecurityConfig {

    public static final String SCOPE_PREFIX = "SCOPE_";
    public static final String SCOPE_REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String SCOPE_USER = "USER";
    public static final String SCOPE_SYSTEM = "SYSTEM";

    public static final String JWT_CLAIM_USER_ID = "userId";
    public static final String JWT_CLAIM_USERNAME = "username";

    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**"
    };

    @Bean
    public SecurityFilterChain SecurityFilterChain(HttpSecurity http,
                                                   DocumentationConfig docConfig,
                                                   ActuatorConfig actuatorConfig) throws Exception {
        http
                .authorizeHttpRequests(customizer ->
                        customizer
                                .requestMatchers("/api/v*/authenticate").permitAll()
                                .requestMatchers("/api/v*/refresh").hasAuthority(SCOPE_PREFIX + SCOPE_REFRESH_TOKEN)
                                .requestMatchers("/api/v*/internal/**").hasAuthority(SCOPE_PREFIX + SCOPE_SYSTEM)
                );

        // allow public access to swagger
        if (docConfig.isEnabled()) {
            http.authorizeHttpRequests(customizer -> customizer.requestMatchers(SWAGGER_WHITELIST).permitAll());
        }

        // allow public access to actuator
        if (actuatorConfig.isEnabled()) {
            http.authorizeHttpRequests(customizer -> customizer.requestMatchers("/actuator/**").permitAll());
        }

        http
                .authorizeHttpRequests(customizer -> customizer.anyRequest().authenticated());

        return http.build();
    }
}
