package com.mspr.clients.config.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class KeycloakJwtClaimsConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final Converter<Jwt, Collection<GrantedAuthority>> delegate = new JwtGrantedAuthoritiesConverter();
        @Override
        public AbstractAuthenticationToken convert(Jwt jwt) {
            var authoritiesList = extractRealmRoles(jwt);
            Collection<GrantedAuthority> authorities = delegate.convert(jwt);
            if (authorities != null) {
                authoritiesList.addAll(authorities);
            }
                return new JwtAuthenticationToken(jwt, authoritiesList, getPrincipalFromClaim(jwt));
        }

        private String getPrincipalFromClaim(Jwt jwt) {
            var claimName = "preferred_username";
            return jwt.getClaim(claimName);
        }

        private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
            Map<String, Object> resource = jwt.getClaim("realm_access");
            Collection<String> roles;
            if (resource == null
                    || (roles = (Collection<String>) resource.get("roles")) == null) {
                return Set.of();
            }
            return roles.stream()
                    .filter(role -> role.startsWith("ROLE_"))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }

}
