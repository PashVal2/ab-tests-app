package org.valdon.abtests.service.user;

import org.valdon.abtests.domain.user.User;
import org.valdon.abtests.dto.auth.RegisterRequest;
import org.valdon.abtests.dto.user.UserResponse;
import org.valdon.abtests.dto.user.UserWithRolesResponse;
import org.valdon.abtests.ex.ResourceNotFoundException;

import java.util.Set;

public interface UserService {

    void deleteUserById(Long id);

    UserResponse getUserById(Long id);

    UserWithRolesResponse getUserWithRoles(Long id);

    Set<String> getRolesByUserId(Long id);

    void enableUser(Long id);

    /**
     * Создание пользователя на основе данных регистрации
     *
     * @return сущность User
     */
    User createUser(String name, String username, String password);

    /**
     * Используется только внутри сервисного слоя
     *
     * @param id идентификатор пользователя
     * @return сущность User
     * @throws ResourceNotFoundException если пользователь не найден
     */
    User getUserEntity(Long id);

    /**
     * Используется только внутри сервисного слоя
     *
     * @param username почта пользователя
     * @return сущность User
     * @throws ResourceNotFoundException если пользователь не найден
     */
    User getUserEntity(String username);

}
