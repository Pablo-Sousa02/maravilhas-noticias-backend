package br.com.maravilhasnoticias.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Bean
    public SecretKey jwtSecretKey(
            @Value("${app.security.jwt.secret}") String encodedSecret
    ) {
        byte[] secretBytes = Base64.getDecoder().decode(encodedSecret);

        if (secretBytes.length < 32) {
            throw new IllegalArgumentException(
                    "A chave JWT deve possuir pelo menos 256 bits"
            );
        }

        return new SecretKeySpec(secretBytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey) {
        return NimbusJwtEncoder
                .withSecretKey(secretKey)
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey secretKey) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<Jwt>(
                new JwtTimestampValidator(),
                JwtValidators.createDefaultWithIssuer("maravilhas-noticias-api")
        ));
        return decoder;
    }
}
