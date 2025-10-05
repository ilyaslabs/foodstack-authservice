package io.github.ilyaslabs.foodstack.authservice.authclient;

import org.junit.jupiter.api.Test;
import org.mockserver.matchers.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockserver.model.HttpError.error;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.verify.VerificationTimes.exactly;

/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TokenServiceTest extends BaseTest {

    @Autowired
    private TokenService tokenService;

    @Test
    void testGetTokenSuccessfully() {
        mockServerClient.when(request().withPath("/auth/token"))
                .respond(response()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                toJsonString(
                                        new AuthResponse()
                                                .setToken("testToken")
                                                .setExpiresIn(100L)
                                                .setRefreshToken("refreshToken")
                                                .setRefreshTokenExpiresIn(200L)
                                )
                        )
                );

        tokenService.getToken();
        //verify endpoint was called only once
        mockServerClient.verify(request().withPath("/auth/token"), exactly(1));

    }

    @Test
    void testGetTokenFailureRetry() {
        var request = request()
                .withMethod("POST")
                .withPath("/auth/token");

        mockServerClient.when(
                        request,
                        Times.exactly(1)
                )
                .respond(
                        response()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withStatusCode(401)
                );

        mockServerClient.when(request)
                .respond(
                        response()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withStatusCode(200)
                                .withBody(toJsonString(
                                        new AuthResponse()
                                                .setToken("testToken")
                                                .setExpiresIn(100L)
                                                .setRefreshToken("refreshToken")
                                                .setRefreshTokenExpiresIn(200L)
                                ))
                );

        String token = tokenService.getToken();
        assertThat(token).isEqualTo("testToken");
        mockServerClient.verify(request().withPath("/auth/token"), exactly(2));
    }

    @Test
    void testGetNewTokenWhenCurrentTokenIsExpired() {

        var expiresInSeconds = Duration.ofSeconds(30);

        mockServerClient.when(
                request().withPath("/auth/token")
        ).respond(response()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withStatusCode(200)
                .withBody(
                        toJsonString(
                                new AuthResponse()
                                        .setToken("testToken")
                                        .setExpiresIn(expiresInSeconds.getSeconds())
                                        .setRefreshToken("refreshToken")
                                        .setRefreshTokenExpiresIn(expiresInSeconds.getSeconds())
                        )
                )
        );

        String token = tokenService.getToken();
        assertThat(token).isEqualTo("testToken");
        mockServerClient.verify(request().withPath("/auth/token"), exactly(1));

        clock.add(Duration.ofSeconds(30));

        token = tokenService.getToken();
        assertThat(token).isEqualTo("testToken");
        mockServerClient.verify(request().withPath("/auth/token"), exactly(2));
    }

    @Test
    void testGetNewTokenUsingRefreshTokenWhenCurrentTokenIsExpired() {

        var expiresInSeconds = Duration.ofSeconds(30);
        var refreshTokenExpirySeconds = Duration.ofSeconds(60);

        mockServerClient.when(
                request().withPath("/auth/token")
        ).respond(response()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withStatusCode(200)
                .withBody(
                        toJsonString(
                                new AuthResponse()
                                        .setToken("testToken")
                                        .setExpiresIn(expiresInSeconds.getSeconds())
                                        .setRefreshToken("refreshToken")
                                        .setRefreshTokenExpiresIn(refreshTokenExpirySeconds.getSeconds())
                        )
                )
        );

        mockServerClient.when(
                request().withPath("/auth/refresh-token")
        ).respond(response()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withStatusCode(200)
                .withBody(
                        toJsonString(
                                new AuthResponse()
                                        .setToken("newTestToken")
                                        .setExpiresIn(expiresInSeconds.getSeconds())
                                        .setRefreshToken("newRefreshToken")
                                        .setRefreshTokenExpiresIn(expiresInSeconds.getSeconds())
                        )
                )
        );

        String token = tokenService.getToken();
        assertThat(token).isEqualTo("testToken");
        mockServerClient.verify(request().withPath("/auth/token"), exactly(1));

        clock.add(Duration.ofSeconds(30));

        token = tokenService.getToken();
        assertThat(token).isEqualTo("newTestToken");
        mockServerClient.verify(request().withPath("/auth/refresh-token"), exactly(1));
        // auth/token should be called only once
        mockServerClient.verify(request().withPath("/auth/token"), exactly(1));
    }

    @Test
    void testNoRetryWhen403StatusIsReturned() {
        var request = request()
                .withMethod("POST")
                .withPath("/auth/token");

        mockServerClient.when(
                        request,
                        Times.exactly(1)
                )
                .respond(
                        response()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withStatusCode(403)
                );

        assertThatThrownBy(() -> tokenService.getToken())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("403");

        mockServerClient.verify(request().withPath("/auth/token"), exactly(1));
    }

    @Test
    void testGetTokenRetryWhenNetworkIsUnreachable() {
        var request = request()
                .withMethod("POST")
                .withPath("/auth/token");

        mockServerClient.when(
                        request,
                        Times.exactly(2)
                )
                .error(
                        error()
                                .withDropConnection(true)
                );

        // success response
        mockServerClient.when(request)
                .respond(
                        response()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withStatusCode(200)
                                .withBody(toJsonString(
                                        new AuthResponse()
                                                .setToken("testToken")
                                                .setExpiresIn(100L)
                                                .setRefreshToken("refreshToken")
                                                .setRefreshTokenExpiresIn(200L)
                                ))
                );

        var token = tokenService.getToken();
        assertThat(token).isNotNull();

        mockServerClient.verify(request().withPath("/auth/token"), exactly(3));
    }

}