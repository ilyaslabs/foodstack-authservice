package io.github.ilyaslabs.foodstack.authservice.api.v1.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@Data
@Accessors(chain = true)
public class AuthResponse {

    private String token;
    private String refreshToken;
    private List<String> scopes;
    private String userId;
    private String username;

    /**
     * expiration time in seconds
     */
    private Long expiresIn;

    /**
     * refresh token expiration time in seconds
     */
    private Long refreshTokenExpiresIn;
}
