package org.valdon.abtests.service.user;

import org.valdon.abtests.domain.user.User;
import org.valdon.abtests.dto.user.UserRequest;
import org.valdon.abtests.dto.user.UserResponse;

public interface UserService {

    void createUser(UserRequest userDto);
    void deleteUserById(Long id);
    UserResponse getUserById(Long id);

}
