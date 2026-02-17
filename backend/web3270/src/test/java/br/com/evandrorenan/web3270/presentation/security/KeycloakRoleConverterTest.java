package br.com.evandrorenan.web3270.presentation.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KeycloakRoleConverterTest {

    private KeycloakRoleConverter converter;

    @BeforeEach
    void setUp() {
        converter = new KeycloakRoleConverter();
    }

    @Test
    void shouldConvertRolesToAuthorities() {
        Jwt jwt = mock(Jwt.class);
        Map<String, Object> realmAccess = Map.of("roles", List.of("ADMIN", "USER"));
        when(jwt.getClaims()).thenReturn(Map.of("realm_access", realmAccess));

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities).hasSize(2);
        assertThat(authorities.stream().map(GrantedAuthority::getAuthority))
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void shouldReturnEmptyIfNoRealmAccess() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaims()).thenReturn(Map.of());

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities).isEmpty();
    }

    @Test
    void shouldReturnEmptyIfNoRoles() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaims()).thenReturn(Map.of("realm_access", Map.of()));

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities).isEmpty();
    }
}
