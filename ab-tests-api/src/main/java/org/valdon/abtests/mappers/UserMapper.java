package org.valdon.abtests.mappers;

import org.mapstruct.Mapper;
import org.valdon.abtests.domain.role.Role;
import org.valdon.abtests.domain.role.enums.RoleEnum;
import org.valdon.abtests.domain.user.User;
import org.valdon.abtests.dto.user.UserResponse;
import org.valdon.abtests.dto.user.UserWithRolesResponse;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toUserResponse(User user);

    default UserWithRolesResponse toUserWithRoles(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .map(RoleEnum::name)
                .collect(Collectors.toSet());

        return new UserWithRolesResponse(
                user.getId(),
                user.getName(),
                user.getUsername(),
                roles
        );
    }

}
