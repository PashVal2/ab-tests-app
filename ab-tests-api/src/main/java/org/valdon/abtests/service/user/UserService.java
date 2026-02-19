package org.valdon.abtests.service.user;

import org.valdon.abtests.dto.UserRecord;

public interface UserService {

    void createUser(UserRecord userDto);
    void deleteUser(Long userId);
    void updateUser(UserRecord userDto);
    UserRecord getUserById(Long id);
    UserRecord getUserByEmail(String username);

}
