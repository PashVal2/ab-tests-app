package org.valdon.abtests.security;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.valdon.abtests.domain.role.Role;
import org.valdon.abtests.domain.user.User;
import org.valdon.abtests.ex.ResourceNotFoundException;
import org.valdon.abtests.repository.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@NullMarked
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new ResourceNotFoundException("user not exists"));
        return createUserDetail(user);
    }

    private UserPrincipal createUserDetail(User user) {
        return new UserPrincipal(user.getId(),
                user.getName(),
                user.getUsername(),
                user.getPassword(),
                mapToGrantedAuthority(user.getRoles()));
    }

    private Set<GrantedAuthority> mapToGrantedAuthority(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

}
