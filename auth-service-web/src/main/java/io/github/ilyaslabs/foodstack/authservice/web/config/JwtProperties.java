package io.github.ilyaslabs.foodstack.authservice.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Configuration properties for JWT security settings.
 */
@ConfigurationProperties(prefix = "security.jwt")
@Data
public class JwtProperties {

    private ChronoUnit expiryUnit = ChronoUnit.MINUTES;
    private Long expiry = 60L; // Default to 60 minutes

    private ChronoUnit refreshExpiryUnit = ChronoUnit.DAYS;
    private Long refreshExpiry = 30L; // Default to 30 days

    /**
     * Calculates the expiration time in seconds based on the defined expiry value and unit.
     *
     * @return the expiration time in seconds as Long.
     */
    public Long getExpiryInSeconds() {
        return Duration.of(expiry, expiryUnit).getSeconds();
    }

    /**
     * Calculates the refresh token expiration time in seconds based on the defined
     * refresh expiry value and unit.
     *
     * @return the refresh token expiration time in seconds as Long.
     */
    public Long getRefreshExpiryInSeconds() {
        return Duration.of(refreshExpiry, refreshExpiryUnit).getSeconds();
    }
}
