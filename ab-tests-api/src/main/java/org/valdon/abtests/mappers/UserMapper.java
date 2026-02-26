package org.valdon.abtests.mappers;

import org.mapstruct.Mapper;
import org.valdon.abtests.domain.user.User;
import org.valdon.abtests.dto.user.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper extends Mappable<User, UserResponse> { }
