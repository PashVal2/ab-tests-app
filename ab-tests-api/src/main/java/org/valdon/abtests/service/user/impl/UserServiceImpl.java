package org.valdon.abtests.service.user.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.valdon.abtests.domain.role.Role;
import org.valdon.abtests.domain.user.User;
import org.valdon.abtests.dto.user.UserRequest;
import org.valdon.abtests.dto.user.UserResponse;
import org.valdon.abtests.ex.UsernameAlreadyExistsException;
import org.valdon.abtests.ex.ResourceNotFoundException;
import org.valdon.abtests.ex.ValidationException;
import org.valdon.abtests.mappers.UserMapper;
import org.valdon.abtests.repository.RoleRepository;
import org.valdon.abtests.repository.UserRepository;
import org.valdon.abtests.service.user.UserService;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    private final static String ROLE_USER = "ROLE_USER";

    @Override
    @Transactional
    public void createUser(UserRequest userDto) {
        if (!userDto.password().equals(userDto.passwordConfirmation())) {
            throw new ValidationException("passwords must match");
        }
        if (userRepository.existsByUsername(userDto.username())) {
            throw new UsernameAlreadyExistsException("email already taken");
        }
        Role userRole = roleRepository.getByName(ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("role not exists"));
        User user = new User(
                null,
                userDto.name(),
                userDto.username(),
                passwordEncoder.encode(userDto.password()),
                Set.of(userRole)
        );
        userRepository.save(user);
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
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user not exists"));
        return userMapper.toDto(user);
    }

}
