package com.jeffrey.example.demospringjwtvalidator.filter;

import com.jeffrey.example.demospringjwtvalidator.service.JwtVerifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("JwtValidationFilter")
public final class JwtValidationFilter extends OncePerRequestFilter {

    private JwtVerifierService jwtVerifierService;

    public JwtValidationFilter(
        @Autowired
        @Qualifier("JwtVerifierService")
        JwtVerifierService jwtVerifierService
    ) {
        super();
        this.jwtVerifierService = jwtVerifierService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain) throws ServletException, IOException
    {
        final String requestUriString = httpServletRequest.getRequestURI();
        if (requestUriString.startsWith("/actuator") || requestUriString.startsWith("/jwk")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            final String authorizationHeaderString = httpServletRequest.getHeader("Authorization");
            if (StringUtils.isEmpty(authorizationHeaderString) || authorizationHeaderString.indexOf("Bearer ")!=0) {
                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "NOT AUTHORIZED");
            } else {
                // validate access token
                String tokenString = authorizationHeaderString.substring("Bearer ".length());
                boolean result = jwtVerifierService.verify(tokenString);
                if (result) {
                    filterChain.doFilter(httpServletRequest, httpServletResponse);
                } else {
                    httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "NOT AUTHORIZED");
                }
            }
        }
    }
}
