package com.jeffrey.example.demospringjwtvalidator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service("JwtVerifierService")
public class JwtVerifierService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtVerifierService.class);

    private static final String LOCAL_PUBLIC_KEY_URI = "http://localhost:8080/jwk";

    private static final String ISSUER_URI =
            "https://manulife-development-dev.apigee.net/v1/mg/oauth2/token";

    @Autowired
    @Qualifier("jwtDecoder")
    JwtDecoder jwtDecoder;

    public boolean verify(String tokenString) {
        try {
            /**
             * standard validation of the JWT
             * - Validate its signature against a public key obtained from the jwks_url
             *   endpoint during startup and matched against the JWTs header
             * - Validate the JWTs exp and nbf timestamps and the JWTs iss claim, and
             * - Map each scope to an authority with the prefix SCOPE_.
             */
            Jwt jwt = jwtDecoder.decode(tokenString);
            LOGGER.debug("jwt: {}", jwt.toString());

            /**
             * validate that the JWT is intended for your API by checking the aud claim of the JWT.
             */
            List<String> audienceList = jwt.getAudience();

            Map<String, Object> claims = jwt.getClaims();
            /**
             * even the JWT is valid, verify the scopes to further restrict access to protected API
             */
            Collection<String> scopes = (Collection<String>) claims.get("scopes");
            if (scopes.contains("READ") && scopes.contains("WRITE") && scopes.contains("DELETE")) {
                return true;
            }
            return false;

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

//    @Bean
//    public JwtAccessTokenConverter accessTokenConverter() throws Exception {
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//        Resource resource = new UrlResource(PUBLIC_KEY_URI);
//        String publicKey = IOUtils.toString(resource.getInputStream());
//        converter.setVerifierKey(publicKey);
//        return converter;
//    }

    @Bean("jwtDecoder")
    public JwtDecoder jwtDecoder() {
//        return NimbusJwtDecoder
//                .withJwkSetUri(PUBLIC_KEY_URI)
//                .jwsAlgorithm(SignatureAlgorithm.RS256)
//                .build();

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withJwkSetUri(LOCAL_PUBLIC_KEY_URI)
                .jwsAlgorithm(SignatureAlgorithm.RS256)
                .build();

        // custom clock skew to mitigate clock drift problem
        OAuth2TokenValidator<Jwt> withClockSkew = new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(Duration.ofDays(10)), //TODO: should be configurable
                new JwtIssuerValidator(ISSUER_URI));
        jwtDecoder.setJwtValidator(withClockSkew);

        return jwtDecoder;
    }
}
