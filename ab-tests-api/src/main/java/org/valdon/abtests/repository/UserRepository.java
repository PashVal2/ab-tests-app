package org.valdon.abtests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.valdon.abtests.domain.user.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

}
