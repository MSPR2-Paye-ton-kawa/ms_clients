package com.mspr.clients.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ApiErrorResponseAccessDeniedHandler;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.UnauthorizedEntryPoint;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.mapper.ErrorCodeMapper;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.mapper.ErrorMessageMapper;
import io.github.wimdeblauwe.errorhandlingspringbootstarter.mapper.HttpStatusMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public UnauthorizedEntryPoint unauthorizedEntryPoint(HttpStatusMapper httpStatusMapper, ErrorCodeMapper errorCodeMapper, ErrorMessageMapper errorMessageMapper, ObjectMapper objectMapper) {
        return new UnauthorizedEntryPoint(httpStatusMapper, errorCodeMapper, errorMessageMapper, objectMapper);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(HttpStatusMapper httpStatusMapper, ErrorCodeMapper errorCodeMapper, ErrorMessageMapper errorMessageMapper, ObjectMapper objectMapper) {
        return new ApiErrorResponseAccessDeniedHandler(objectMapper, httpStatusMapper, errorCodeMapper, errorMessageMapper);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AccessDeniedHandler accessDeniedHandler, UnauthorizedEntryPoint unauthorizedEntryPoint) throws Exception {
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers("/ms-clients/clients/**").authenticated()
                //                        .requestMatchers(HttpMethod.GET, "/ms-clients/clients/**").hasAnyRole("CLIENTS_READ")
                //                        .requestMatchers(HttpMethod.POST, "/ms-clients/clients/**").hasAnyRole("CLIENTS_ADD")
                //                        .requestMatchers(HttpMethod.PUT, "/ms-clients/clients/**").hasAnyRole("CLIENTS_EDIT")
                //                        .requestMatchers(HttpMethod.DELETE, "/ms-clients/clients/**").hasAnyRole("CLIENTS_DELETE")
                .anyRequest().permitAll());

        http.exceptionHandling(exceptionHandling -> exceptionHandling.accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(unauthorizedEntryPoint));

        http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(jwt -> jwt.jwtAuthenticationConverter(new KeycloakJwtClaimsConverter())));

        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(AbstractHttpConfigurer::disable);

        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
