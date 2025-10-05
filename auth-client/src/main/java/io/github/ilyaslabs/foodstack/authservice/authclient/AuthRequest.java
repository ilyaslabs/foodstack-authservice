package io.github.ilyaslabs.foodstack.authservice.authclient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Accessors(chain = true)
class AuthRequest {

    private String username;
    private String password;
}
