package io.github.ilyaslabs.foodstack.authservice.api.v1;

import io.github.ilyaslabs.foodstack.authservice.api.Constants;
import io.github.ilyaslabs.foodstack.authservice.api.v1.dto.AuthRequest;
import io.github.ilyaslabs.foodstack.authservice.api.v1.dto.AuthResponse;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * API for authentication service
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@HttpExchange(Constants.API_PREFIX_V1)
public interface AuthApiV1 {

    /**
     * Authenticates a user based on the provided authentication request.
     *
     * @param request an {@code AuthRequest} object containing the username and password of the user
     * @return an {@code AuthResponse} object containing the authentication token, refresh token, scopes,
     *         user ID, username, token expiration time, and refresh token expiration time
     */
    @PostExchange("/authenticate")
    AuthResponse authenticate(AuthRequest request);

    /**
     * Refreshes the authentication token using a valid refresh token.
     *
     * @return an {@code AuthResponse} object containing a new authentication token, a new refresh token,
     *         updated token expiration time, and refresh token expiration time
     */
    @PostExchange( "/refresh")
    AuthResponse refreshToken();
}
