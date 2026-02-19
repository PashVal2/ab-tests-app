package org.valdon.abtests.mappers;

import org.mapstruct.Mapper;
import org.valdon.abtests.domain.user.UserEntity;
import org.valdon.abtests.dto.UserRecord;

@Mapper(componentModel = "spring")
public interface UserMapper extends Mappable<UserEntity, UserRecord> { }
