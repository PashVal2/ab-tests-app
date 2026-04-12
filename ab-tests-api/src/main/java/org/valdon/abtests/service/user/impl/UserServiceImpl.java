package org.valdon.abtests.service.user.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.valdon.abtests.domain.role.Role;
import org.valdon.abtests.domain.role.enums.RoleEnum;
import org.valdon.abtests.domain.user.User;
import org.valdon.abtests.dto.user.UserResponse;
import org.valdon.abtests.dto.user.UserWithRolesResponse;
import org.valdon.abtests.ex.*;
import org.valdon.abtests.mappers.UserMapper;
import org.valdon.abtests.repository.user.RoleRepository;
import org.valdon.abtests.repository.user.UserRepository;
import org.valdon.abtests.service.user.UserService;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public User createUser(String name, String username, String password) {
        Boolean isUserEnabled = userRepository.findEnabledByUsername(username)
                .orElse(null);

        if (isUserEnabled != null) {
            if (isUserEnabled) {
                throw new UserAlreadyExistsException("user already exists");
            }
            throw new EmailNotConfirmedException("user exists but is not enabled");
        }

        Role userRole = roleRepository.getByName(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new InternalServerException("user role not exists"));

        User user = new User(
                null,
                name,
                username,
                passwordEncoder.encode(password),
                false,
                Set.of(userRole)
        );
        return userRepository.save(user);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("user not exists");
        }
        userRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @cse.canAccess(#id)")
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user not exists"));
        return userMapper.toUserResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @cse.canAccess(#id)")
    @Transactional(readOnly = true)
    public User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user not exists"));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserEntity(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("user not exits"));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @cse.canAccess(#id)")
    @Transactional(readOnly = true)
    public UserWithRolesResponse getUserWithRoles(Long id) {
        User user = userRepository.findUserWithRoles(id)
                .orElseThrow(() -> new ResourceNotFoundException("user not exists"));
        return userMapper.toUserWithRoles(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getRolesByUserId(Long id){
        Set<String> roles = userRepository.findRolesByUserId(id);
        if (roles.isEmpty()) {
            throw new UnauthorizedException("unauthorized");
        }
        return roles;
    }

    @Override
    @Transactional
    public void enableUser(Long id){
        int updated = userRepository.enableByUserId(id);
        if (updated == 0) {
            throw new ResourceNotFoundException("user not exists");
        }
    }

}
