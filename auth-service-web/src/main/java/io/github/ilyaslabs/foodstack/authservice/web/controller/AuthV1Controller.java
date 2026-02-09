package io.github.ilyaslabs.foodstack.authservice.web.controller;

import io.github.ilyaslabs.foodstack.authservice.api.v1.AuthApiV1;
import io.github.ilyaslabs.foodstack.authservice.api.v1.dto.AuthRequest;
import io.github.ilyaslabs.foodstack.authservice.api.v1.dto.AuthResponse;
import io.github.ilyaslabs.foodstack.authservice.web.config.AuthProperties;
import io.github.ilyaslabs.foodstack.authservice.web.config.JwtProperties;
import io.github.ilyaslabs.foodstack.authservice.web.db.document.User;
import io.github.ilyaslabs.foodstack.authservice.web.repository.UserRepository;
import io.github.ilyaslabs.foodstack.authservice.web.service.AuthService;
import io.github.ilyaslabs.microservice.exception.HttpResponseException;
import io.github.ilyaslabs.microservice.security.guard.AuthenticationContextProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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
    private final AuthProperties authProperties;
    private final JwtProperties jwtProperties;

    private final AuthenticationContextProvider authenticationContextProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthResponse authenticate(@RequestBody @Validated AuthRequest request) {

        log.info("Authenticating user {}", request.getUsername());

        var user = userRepository.findByEmail(request.getUsername())
                .filter(u -> Boolean.TRUE.equals(u.getEnabled()) && u.getDeletedAt() == null)
                .orElseThrow(() -> HttpResponseException.of(HttpStatus.NOT_FOUND, "Not found", Map.of("username", "User not found")));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw HttpResponseException.ofUnauthorized("Unauthorized", Map.of("password", "Invalid credentials"));

        return buildAuthResponse(user);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasAuthority('" + AuthService.SCOPE_REFRESH_TOKEN + "')")
    public AuthResponse refreshToken() {

        ObjectId userId = authenticationContextProvider.current().userId();
        User user = userRepository.findById(userId)
                .filter(u -> Boolean.TRUE.equals(u.getEnabled()) && u.getDeletedAt() == null)
                .orElseThrow(() -> HttpResponseException.of(HttpStatus.NOT_FOUND, "Not found", Map.of("username", "User not found")));

        // build scopes list
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        // build scopes list
        List<String> scopes = user.getScopes().stream().map(Enum::name).toList();

        // generate token
        String token = authService.generateToken(
                user.getId().toHexString(),
                authProperties.getIssuer(),
                null,
                scopes
        );

        //generate refresh token
        String refreshToken = authService.generateRefreshToken(
                user.getId().toHexString(),
                authProperties.getIssuer(),
                null,
                null
        );

        return new AuthResponse()
                .setToken(token)
                .setRefreshToken(refreshToken)
                .setScopes(scopes)
                .setUserId(user.getId().toHexString())
                .setUsername(user.getUsername())
                .setExpiresIn(jwtProperties.getExpiryInSeconds())
                .setRefreshTokenExpiresIn(jwtProperties.getRefreshExpiryInSeconds());
    }
}
