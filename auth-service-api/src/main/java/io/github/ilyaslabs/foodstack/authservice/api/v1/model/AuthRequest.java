package io.github.ilyaslabs.foodstack.authservice.api.v1.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
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
public class AuthRequest {

    @NotEmpty(message = "Username is required")
    private String username;

    @NotEmpty(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}
