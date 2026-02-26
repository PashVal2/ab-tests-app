package org.valdon.abtests.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Builder
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private Long id;
    private String name;
    private String username;
    private String password;
    private final Collection<? extends GrantedAuthority> authorities;

}
