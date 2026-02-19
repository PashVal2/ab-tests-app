package org.valdon.abtests.service.user.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.valdon.abtests.dto.UserRecord;
import org.valdon.abtests.service.user.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Override
    public void createUser(UserRecord userDto) {

    }

    @Override
    public void deleteUser(Long userId) {

    }

    @Override
    public void updateUser(UserRecord userDto) {

    }

    @Override
    @Cacheable(value = "users", key = "1")
    public UserRecord getUserById(Long id) {
        return new UserRecord(id,
                "a",
                "a",
                "a",
                "a");
    }

    @Override
    public UserRecord getUserByEmail(String username) {
        return null;
    }
}
