package org.valdon.abtests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.valdon.abtests.domain.user.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

     Optional<User> findByUsername(String username);
     boolean existsByUsername(String username);

     @Query("SELECT u FROM User u " +
             "JOIN FETCH u.roles " +
             "WHERE u.username = :username")
     Optional<User> findByUsernameWithRoles(String username);

}
