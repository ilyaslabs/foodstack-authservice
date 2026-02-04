package io.github.ilyaslabs.foodstack.authservice.web;

import io.github.ilyaslabs.foodstack.authservice.web.db.document.User;
import io.github.ilyaslabs.foodstack.authservice.web.repository.UserRepository;
import io.github.ilyaslabs.foodstack.authservice.web.service.AuthService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.bson.types.ObjectId;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@SpringBootTest(classes = Config.class)
@AutoConfigureMockMvc
public class BaseContractsTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockitoBean
    UserRepository userRepository;

    @MockitoBean
    AuthService authService;

    private final String ADMIN_USER_ID = "697e60723f73509d5143f6ef";

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        stubUserRepository();
        stubAuthService();
    }

    private void stubAuthService() {
        doReturn("jwt token").when(authService).generateToken(eq(ADMIN_USER_ID), anyString(), any(), any());
        doReturn("jwt refresh token").when(authService).generateRefreshToken(eq(ADMIN_USER_ID), anyString(), any(), any());
    }

    private void stubUserRepository() {
        User adminUser = getAdminUser();
        doReturn(Optional.of(adminUser)).when(userRepository).findByEmail(eq(adminUser.getEmail()));
        doReturn(Optional.of(adminUser)).when(userRepository).findById(eq(adminUser.getId()));
    }

    private @NonNull User getAdminUser() {
        User user = new User();
        user.setId(new ObjectId(ADMIN_USER_ID));
        user.setUsername("Admin");
        user.setPassword(passwordEncoder.encode("123456789"));
        user.setEmail("admin@domain.com");
        user.setEnabled(true);
        user.setScopes(List.of(User.Scope.ADMIN));
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return user;
    }
}
