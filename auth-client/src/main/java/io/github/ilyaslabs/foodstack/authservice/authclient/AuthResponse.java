package io.github.ilyaslabs.foodstack.authservice.authclient;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@Data
@Accessors(chain = true)
class AuthResponse {

    private String token;
    private String refreshToken;
    private List<String> scopes;
    private String userId;
    private String username;

    /**
     * expiration time in seconds
     */
    private Long expiresIn;

    private Long refreshTokenExpiresIn;
}
