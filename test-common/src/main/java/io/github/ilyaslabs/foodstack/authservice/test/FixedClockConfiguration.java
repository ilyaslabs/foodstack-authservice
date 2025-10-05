package io.github.ilyaslabs.foodstack.authservice.test;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.backoff.Sleeper;

import java.time.Instant;
import java.time.ZoneId;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@TestConfiguration
public class FixedClockConfiguration {

    @Bean
    MutableClock clock() {
        return new MutableClock(Instant.parse("2020-01-01T00:00:00.00Z"), ZoneId.of("UTC"));
    }

    @Bean
    public Sleeper sleeper() {
        // No-op sleeper (skips Thread.sleep)
        return millis -> {};
    }
}
