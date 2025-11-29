package io.github.ilyaslabs.foodstack.authservice.web.controller;

import io.github.ilyaslabs.foodstack.authservice.api.v1.AuthApiV1;
import io.github.ilyaslabs.foodstack.authservice.api.v1.dto.AuthRequest;
import io.github.ilyaslabs.foodstack.authservice.api.v1.dto.AuthResponse;
import io.github.ilyaslabs.foodstack.authservice.security.common.CommonSecurityConfig;
import io.github.ilyaslabs.foodstack.authservice.web.config.AuthConfig;
import io.github.ilyaslabs.foodstack.authservice.web.db.document.User;
import io.github.ilyaslabs.foodstack.authservice.web.repository.UserRepository;
import io.github.ilyaslabs.microservice.exception.HttpResponseException;
import io.github.ilyaslabs.microservice.security.jwt.AuthService;
import io.github.ilyaslabs.microservice.security.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@RestController
@RequiredArgsConstructor
@Slf4j
class AuthV1Controller implements AuthApiV1 {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthConfig authConfig;
    private final JwtProperties jwtProperties;

    private final Clock clock;

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthResponse authenticate(@Validated @RequestBody AuthRequest request) {

        log.info("Authenticating user {}", request.getUsername());

        User user = userRepository.findByUsernameOrEmail(request.getUsername(), request.getUsername())
                .orElseThrow(() -> HttpResponseException.of(
                        HttpStatus.NOT_FOUND,
                        "Not found",
                        Map.of("username", "Invalid username or email")
                ));

        // match password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw HttpResponseException.of(
                    HttpStatus.BAD_REQUEST,
                    "Validation failed",
                    Map.of("password", "Invalid password")
            );
        }

        return buildAuthResponse(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthResponse refreshToken() {
        // get user id from jwt claim
        String userId = authService.getClaim(CommonSecurityConfig.JWT_CLAIM_USER_ID, String.class).orElseThrow(() -> HttpResponseException.ofForbidden("Invalid user id"));

        // get user from the database
        User user = userRepository.findById(new ObjectId(userId))
                .orElseThrow(() -> HttpResponseException.ofNotFound("User not found"));

        return buildAuthResponse(user);

    }

    /**
     * Constructs an {@link AuthResponse} for the given user by generating authentication
     * and refresh tokens, along with associated claims and scopes.
     *
     * @param user the user for whom the authentication response is to be built; must be enabled
     *             and not deleted
     * @return an {@link AuthResponse} containing the user's ID, username, authentication token,
     * refresh token, scopes, token expiration time, and refresh token expiration time
     * @throws HttpResponseException if the user is disabled or deleted
     */
    private AuthResponse buildAuthResponse(User user) {
        // user should be enabled and not deleted
        if (!Boolean.TRUE.equals(user.getEnabled()) || user.getDeletedAt() != null) {
            throw HttpResponseException.ofForbidden("User is disabled");
        }

        List<String> scopes = Optional.ofNullable(user.getScopes()).orElse(Collections.emptyList())
                .stream().map(Enum::name).toList();

        Map<String, String> claims = Map.of(
                CommonSecurityConfig.JWT_CLAIM_USER_ID, user.getId().toHexString(),
                CommonSecurityConfig.JWT_CLAIM_USERNAME, user.getUsername()
        );

        String token = authService.generateToken(
                user.getId().toHexString(),
                authConfig.getIssuer(),
                claims,
                scopes
        );

        String refreshToken = authService.generateRefreshToken(
                user.getId().toHexString(),
                authConfig.getIssuer(),
                null,
                null
        );

        return new AuthResponse()
                .setUserId(user.getId().toHexString())
                .setUsername(user.getUsername())
                .setToken(token)
                .setRefreshToken(refreshToken)
                .setScopes(scopes)
                .setExpiresIn(Instant.now(clock).plus(jwtProperties.getExpiry(), jwtProperties.getExpiryUnit()).getEpochSecond())
                .setRefreshTokenExpiresIn(Instant.now(clock).plus(jwtProperties.getRefreshExpiry(), jwtProperties.getRefreshExpiryUnit()).getEpochSecond());
    }
}
