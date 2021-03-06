package com.jeffrey.example.demospringjwtvalidator.service;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Null;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service("JwtVerifierService")
public class JwtVerifierService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtVerifierService.class);

    @SuppressWarnings("unused")
    @Value("${com.jeffrey.example.jwtValidation.local-jwk-set-uri:#{null}}")
    private String localPublicKeyUri;

    @SuppressWarnings("unused")
    @Value("${com.jeffrey.example.jwtValidation.token-issuer-uri:#{null}}")
    private String tokenIssuerUri;

    @Autowired
    @Qualifier("jwtDecoder")
    private JwtDecoder jwtDecoder;

    public HttpStatus verify(String tokenString) {
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
             * TODO:
             * validate that the JWT is intended for your API by checking the aud claim of the JWT.
             */
            List<String> audienceList = jwt.getAudience();

            Map<String, Object> claims = jwt.getClaims();
            /**
             * even the JWT is valid, verify the scopes to further restrict access to protected API
             */
            Collection<String> scopes = (Collection<String>) claims.get("scope");
            if (scopes == null) return HttpStatus.FORBIDDEN;
            if (scopes.contains("message:read") || scopes.contains("message:write")) {
                return HttpStatus.OK;
            }
            return HttpStatus.UNAUTHORIZED;

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return HttpStatus.UNAUTHORIZED;
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

        Assert.assertNotNull(localPublicKeyUri);

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withJwkSetUri(localPublicKeyUri)
                .jwsAlgorithm(SignatureAlgorithm.RS256)
                .build();

        // custom clock skew to mitigate clock drift problem
        OAuth2TokenValidator<Jwt> withClockSkew;
        if (tokenIssuerUri != null) {
            withClockSkew = new DelegatingOAuth2TokenValidator<>(
                    new JwtTimestampValidator(Duration.ofDays(10)), //TODO: should be configurable
                    new JwtIssuerValidator(tokenIssuerUri));
        } else {
            withClockSkew = new DelegatingOAuth2TokenValidator<>(
                    new JwtTimestampValidator(Duration.ofDays(10))); //TODO: should be configurable
        }
        jwtDecoder.setJwtValidator(withClockSkew);

        return jwtDecoder;
    }
}
