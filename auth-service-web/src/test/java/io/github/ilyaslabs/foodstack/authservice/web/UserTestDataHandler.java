package io.github.ilyaslabs.foodstack.authservice.web;

import io.github.ilyaslabs.foodstack.authservice.web.db.document.User;
import io.github.ilyaslabs.foodstack.authservice.web.repository.UserTestRepository;
import jakarta.annotation.PostConstruct;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@Service
public class UserTestDataHandler {

    private final UserTestRepository userTestRepository;
    private final PasswordEncoder passwordEncoder;

    public static final ObjectId ADMIN_USER_ID = new ObjectId("5f7333333333333333333333");
    public static final ObjectId USER_ID = new ObjectId("5f7333333333333333333334");
    public static final String PASSWORD = "test123456";

    private String encodedPassword;

    public UserTestDataHandler(UserTestRepository userTestRepository, PasswordEncoder passwordEncoder) {
        this.userTestRepository = userTestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    private void init() {
        encodedPassword = passwordEncoder.encode(PASSWORD);
    }

    /**
     * Inserts test user data into the repository for administrative and regular user roles.
     * Two user entries are created:
     * 1. An administrator user with pre-defined credentials and scope ADMIN.
     * 2. A standard user with pre-defined credentials and scope USER.
     * Each user has their unique identifiers, username, email, and associated attributes.
     * <p>
     * This method is used to set up data for testing purposes by persisting the users
     * into the provided {@code userTestRepository}.
     */
    public void setupData() {
        User user = new User();
        user.setId(ADMIN_USER_ID);
        user.setUsername("admin");
        user.setPassword(encodedPassword);
        user.setEmail("admin@domain.com");
        user.setDeletedAt(null);
        user.setEnabled(true);
        user.setScopes(List.of(User.Scope.ADMIN));
        userTestRepository.save(user);

        user = new User();
        user.setId(USER_ID);
        user.setUsername("user");
        user.setPassword(encodedPassword);
        user.setEmail("user@domain.com");
        user.setDeletedAt(null);
        user.setEnabled(true);
        user.setScopes(List.of(User.Scope.USER));
        userTestRepository.save(user);
    }

    public void clearAllUsers() {
        userTestRepository.deleteAll();
    }
}
