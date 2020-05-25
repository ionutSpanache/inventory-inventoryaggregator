package net.metrosystems.inventoryaggregator.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    private final String resourceId;

    public CustomJwtAuthenticationConverter(String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public AbstractAuthenticationToken convert(final Jwt source) {
        Collection<GrantedAuthority> authorities = Stream.concat(defaultGrantedAuthoritiesConverter.convert(source)
                        .stream(),
                extractResourceRoles(source, resourceId).stream())
                .collect(Collectors.toSet());
        return new JwtAuthenticationToken(source, authorities);
    }

    private static Collection<? extends GrantedAuthority> extractResourceRoles(final Jwt jwt, final String resourceId) {
        Collection<Map<String, Object>> resource = jwt.getClaim(resourceId);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (resource != null) {
            resource.stream().forEach(map -> {
                map.entrySet()
                        .stream()
                        .forEach(entry -> {
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + entry.getKey()));
                        });
            });
            return authorities;
        }
        return Collections.emptySet();
    }
}
