package io.github.ilyaslabs.foodstack.authservice.web;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import io.github.ilyaslabs.foodstack.authservice.web.config.RsaKeyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;


/**
 *
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
@Configuration("auht-security-config")
class SecurityConfig {

    /**
     * Provides a PasswordEncoder bean for encoding passwords.
     *
     * @return a PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates a JwtDecoder bean for decoding JWT tokens.
     *
     * @return a JwtDecoder instance
     */
    @Bean
    public JwtDecoder jwtDecoder(RsaKeyProperties rsaKeyProperties) {
        return NimbusJwtDecoder.withPublicKey(rsaKeyProperties.getRsaPublicKey()).build();
    }

    /**
     * Creates a JwtEncoder bean for encoding JWT tokens.
     *
     * @return a JwtEncoder instance
     */
    @Bean
    public JwtEncoder jwtEncoder(RsaKeyProperties rsaKeyProperties) {
        RSAKey rsaKey = new RSAKey.Builder(rsaKeyProperties.getRsaPublicKey()).privateKey(rsaKeyProperties.getRsaPrivateKey()).build();
        ImmutableJWKSet<SecurityContext> securityContextImmutableJWKSet = new ImmutableJWKSet<>(new JWKSet(rsaKey));
        return new NimbusJwtEncoder(securityContextImmutableJWKSet);
    }
}
