package io.github.ilyaslabs.foodstack.authservice.authclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;

/**
 * Service to manage authentication tokens.
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@Service
@RequiredArgsConstructor
@Slf4j
class TokenService {

    private final AuthClientConfig authClientConfig;
    private final Clock clock;

    private final RestClient restClient = RestClient.create();

    private String cachedToken;
    private String cachedRefreshToken;
    private Instant tokenExpiry;
    private Instant refreshTokenExpiry;

    /**
     * Fetches an authentication token from the authentication service.
     * The method retries the request in case of exceptions, with a configurable backoff delay.
     * Caches the token and its expiry time to reduce redundant requests.
     *
     * @return A valid authentication token as a String.
     */
    @Retryable(
            retryFor = {UnauthorizedRetryException.class, RestClientException.class, IOException.class},
            noRetryFor = {HttpClientErrorException.class, HttpServerErrorException.class},
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    synchronized String getToken() {
        log.info("Fetching token from auth service...");
        if (isTokenValid()) {
            return cachedToken;
        }

        // if the refresh token is valid, use it to get a new token
        if (isRefreshTokenValid()) {
            return fetchTokenUsingRefreshToken();
        }

        return fetchToken();
    }

    /**
     * Fetch token using username and password
     *
     * @return token as String
     */
    private String fetchToken() {
        // Clear token and let retry happen
        cachedToken = null;
        tokenExpiry = null;

        AuthRequest request = new AuthRequest(
                authClientConfig.getUsername(),
                authClientConfig.getPassword()
        );

        AuthResponse response = restClient.post()
                .uri(authClientConfig.getTokenUrl())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.value() == 401,
                        (_, _) -> {
                            log.error("Invalid credentials provided for authentication");
                            throw new UnauthorizedRetryException("Invalid credentials");
                        }
                )
                .body(AuthResponse.class);

        cachedToken = response.getToken();
        cachedRefreshToken = response.getRefreshToken();
        tokenExpiry = Instant.now(clock).plusSeconds(response.getExpiresIn());
        refreshTokenExpiry = Instant.now(clock).plusSeconds(response.getRefreshTokenExpiresIn());

        log.info("Token fetched successfully with expiry of {} seconds", response.getExpiresIn());

        return cachedToken;
    }

    /**
     * Fetch token using refresh token
     *
     * @return token as String
     */
    private String fetchTokenUsingRefreshToken() {
        // Clear token and let retry happen
        cachedToken = null;
        tokenExpiry = null;

        RefreshTokenRequest request = new RefreshTokenRequest(
                cachedRefreshToken
        );

        AuthResponse response = restClient.post()
                .uri(authClientConfig.getRefreshTokenUrl())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .retrieve()
                .body(AuthResponse.class);

        cachedToken = response.getToken();
        cachedRefreshToken = response.getRefreshToken();
        tokenExpiry = Instant.now(clock).plusSeconds(response.getExpiresIn());
        refreshTokenExpiry = Instant.now(clock).plusSeconds(response.getRefreshTokenExpiresIn());

        log.info("Token fetched successfully using refresh token with expiry of {} seconds", response.getExpiresIn());

        return cachedToken;
    }

    /**
     * Checks if the current token is valid based on its expiry time.
     *
     * @return true if the token is valid, false otherwise
     */
    private boolean isTokenValid() {
        return tokenExpiry != null && Instant.now(clock).isBefore(tokenExpiry);
    }

    /**
     * Checks if the current refresh token is valid based on its expiry time.
     *
     * @return true if the refresh token is valid, false otherwise
     */
    private boolean isRefreshTokenValid() {
        return refreshTokenExpiry != null && Instant.now(clock).isBefore(refreshTokenExpiry);
    }

    private static class UnauthorizedRetryException extends RuntimeException {
        public UnauthorizedRetryException(String message) {
            super(message);
        }
    }
}

