package io.github.ilyaslabs.foodstack.authservice.web.controller;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.github.ilyaslabs.foodstack.authservice.api.v1.model.AuthRequest;
import io.github.ilyaslabs.foodstack.authservice.api.v1.model.AuthResponse;
import io.github.ilyaslabs.foodstack.authservice.security.common.CommonSecurityConfig;
import io.github.ilyaslabs.foodstack.authservice.test.FixedClockConfiguration;
import io.github.ilyaslabs.foodstack.authservice.test.MutableClock;
import io.github.ilyaslabs.foodstack.authservice.web.BaseTest;
import io.github.ilyaslabs.foodstack.authservice.web.UserTestDataHandler;
import io.github.ilyaslabs.foodstack.authservice.web.config.AuthConfig;
import io.github.ilyaslabs.foodstack.authservice.web.db.document.User;
import io.github.ilyaslabs.foodstack.authservice.web.repository.UserRepository;
import io.github.ilyaslabs.microservice.exception.HttpResponseException;
import io.github.ilyaslabs.microservice.security.jwt.AuthService;
import io.github.ilyaslabs.microservice.security.jwt.JwtProperties;
import org.assertj.core.data.MapEntry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(FixedClockConfiguration.class)
class AuthV1ControllerTest extends BaseTest {

    @Autowired
    private UserTestDataHandler userTestDataHandler;

    @Autowired
    private MutableClock clock;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private AuthConfig authConfig;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeAll
    void setUp() {

        userTestDataHandler.clearAllUsers();
        userTestDataHandler.setupData();
    }

    /**
     * Test of login method, of class AuthV1Controller.
     *
     * @throws Exception if any.
     */
    @Test
    void testLoginSuccessfully() throws Exception {

        var request = post("/api/v1/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(new AuthRequest().setUsername("admin@domain.com").setPassword("test123456")));

        String responseString = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthResponse response = objectMapper.readValue(responseString, AuthResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();
        assertThat(response.getExpiresIn()).isEqualTo(Instant.now(clock).plus(jwtProperties.getExpiry(), jwtProperties.getExpiryUnit()).getEpochSecond());
        assertThat(response.getRefreshTokenExpiresIn()).isEqualTo(Instant.now(clock).plus(jwtProperties.getRefreshExpiry(), jwtProperties.getRefreshExpiryUnit()).getEpochSecond());
        assertThat(response.getUserId()).isEqualTo(UserTestDataHandler.ADMIN_USER_ID.toHexString());
        assertThat(response.getUsername()).isEqualTo("admin");
        assertThat(response.getScopes()).containsExactly("ADMIN");

        // decrypt token without checking signature
        SignedJWT jwt = SignedJWT.parse(response.getToken());

        JWTClaimsSet jwtClaimsSet = jwt.getJWTClaimsSet();

        assertThat(jwtClaimsSet.getSubject()).isEqualTo("admin");
        assertThat(jwtClaimsSet.getIssuer()).isEqualTo(authConfig.getIssuer());

        assertThat(jwtClaimsSet.getClaim(CommonSecurityConfig.JWT_CLAIM_USER_ID)).isEqualTo(UserTestDataHandler.ADMIN_USER_ID.toHexString());
        assertThat(jwtClaimsSet.getClaim(CommonSecurityConfig.JWT_CLAIM_USERNAME)).isEqualTo("admin");
        assertThat(jwtClaimsSet.getClaim(AuthService.KEY_SCOPE_CLAIM)).isEqualTo("ADMIN");

    }

    @Test
    void testLoginFailedWhenNoUserNameIsProvided() throws Exception {
        var request = post("/api/v1/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(new AuthRequest().setUsername(null).setPassword("")));

        String responseString = mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/problem+json"))
                .andReturn()
                .getResponse().getContentAsString();

        HttpResponseException.ResponseBody response = objectMapper.readValue(responseString, HttpResponseException.ResponseBody.class);

        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo("Validation failed");
        assertThat(response.fields()).contains(MapEntry.entry("username", "Username is required"));

    }

    @Test
    void testLoginFailedWhenNoPasswordIsProvided() throws Exception {
        var request = post("/api/v1/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(new AuthRequest().setUsername("admin").setPassword(null)));

        String responseString = mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/problem+json"))
                .andReturn()
                .getResponse().getContentAsString();

        HttpResponseException.ResponseBody response = objectMapper.readValue(responseString, HttpResponseException.ResponseBody.class);

        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo("Validation failed");
        assertThat(response.fields()).contains(MapEntry.entry("password", "Password is required"));

    }

    @Test
    void testLoginFailedWhenPasswordIsTooShort() throws Exception {
        var request = post("/api/v1/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(new AuthRequest().setUsername("admin").setPassword("1234")));

        String responseString = mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/problem+json"))
                .andReturn()
                .getResponse().getContentAsString();

        HttpResponseException.ResponseBody response = objectMapper.readValue(responseString, HttpResponseException.ResponseBody.class);

        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo("Validation failed");
        assertThat(response.fields()).contains(MapEntry.entry("password", "Password must be at least 8 characters long"));
    }

    @Test
    void testLoginFailedWhenUserIsDisabled() throws Exception {

        String password = "password12345";
        User user = new User();
        user.setEmail("email@domain.com");
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(false);
        userRepository.save(user);

        String responseString = mockMvc.perform(
                        post("/api/v1/authenticate").contentType(MediaType.APPLICATION_JSON)
                                .content(toJsonString(new AuthRequest().setUsername("email@domain.com").setPassword(password)))
                ).andExpect(status().isForbidden())
                .andExpect(header().string("Content-Type", "application/problem+json"))
                .andReturn()
                .getResponse().getContentAsString();

        HttpResponseException.ResponseBody response = objectMapper.readValue(responseString, HttpResponseException.ResponseBody.class);

        assertThat(response.message()).isEqualTo("User is disabled");

    }

    @Test
    void testLoginFailedWhenUserIsDeleted() throws Exception {

        String password = "password12345";
        User user = new User();
        user.setEmail("email1@domain.com");
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setDeletedAt(Instant.now(clock));
        userRepository.save(user);

        String responseString = mockMvc.perform(
                        post("/api/v1/authenticate").contentType(MediaType.APPLICATION_JSON)
                                .content(toJsonString(new AuthRequest().setUsername("email1@domain.com").setPassword(password)))
                ).andExpect(status().isForbidden())
                .andExpect(header().string("Content-Type", "application/problem+json"))
                .andReturn()
                .getResponse().getContentAsString();

        HttpResponseException.ResponseBody response = objectMapper.readValue(responseString, HttpResponseException.ResponseBody.class);

        assertThat(response.message()).isEqualTo("User is disabled");
    }

    @Test
    void testLoginFailedWhenUserDoesNotExist() throws Exception {

        String responseString = mockMvc.perform(
                        post("/api/v1/authenticate").contentType(MediaType.APPLICATION_JSON)
                                .content(toJsonString(new AuthRequest().setUsername("notfound@domain.com").setPassword("password12345")))
                ).andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", "application/problem+json"))
                .andReturn()
                .getResponse().getContentAsString();

        HttpResponseException.ResponseBody response = objectMapper.readValue(responseString, HttpResponseException.ResponseBody.class);

        assertThat(response.message()).isEqualTo("Not found");
        assertThat(response.fields()).contains(MapEntry.entry("username", "Invalid username or email"));
    }

    @Test
    void testLoginFailedWhenPasswordIsInvalid() throws Exception {

        String responseString = mockMvc.perform(
                        post("/api/v1/authenticate").contentType(MediaType.APPLICATION_JSON)
                                .content(toJsonString(new AuthRequest().setUsername("admin@domain.com").setPassword("invalidpassword")))
                ).andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/problem+json"))
                .andReturn()
                .getResponse().getContentAsString();

        HttpResponseException.ResponseBody response = objectMapper.readValue(responseString, HttpResponseException.ResponseBody.class);

        assertThat(response.message()).isEqualTo("Validation failed");
        assertThat(response.fields()).contains(MapEntry.entry("password", "Invalid password"));
    }

    @Test
    void testRefreshTokenSuccessfully() throws Exception {

        mockMvc.perform(post("/api/v1/refresh")
                .with(jwt().authorities(new SimpleGrantedAuthority(CommonSecurityConfig.SCOPE_PREFIX + CommonSecurityConfig.SCOPE_REFRESH_TOKEN))
                        .jwt(jwt ->
                                jwt.claim(CommonSecurityConfig.JWT_CLAIM_USER_ID, UserTestDataHandler.ADMIN_USER_ID.toHexString())
                        )
                )).andExpect(status().isOk());
    }

    @Test
    void testRefreshTokenFailedWhenNoJwtTokenIsProvided() throws Exception {
        mockMvc.perform(post("/api/v1/refresh"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRefreshTokenFailedWhenNoRefreshScopeIsProvided() throws Exception {
        mockMvc.perform(post("/api/v1/refresh")
                .with(jwt().authorities(new SimpleGrantedAuthority(CommonSecurityConfig.SCOPE_PREFIX + "USER"))
                        .jwt(jwt ->
                                jwt.claim(CommonSecurityConfig.JWT_CLAIM_USER_ID, UserTestDataHandler.ADMIN_USER_ID.toHexString())
                        )
                )
        ).andExpect(status().isForbidden());
    }
}