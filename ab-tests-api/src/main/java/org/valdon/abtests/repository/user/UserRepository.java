package org.valdon.abtests.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.valdon.abtests.domain.user.User;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

     Optional<User> findByUsername(String username);
     boolean existsByUsername(String username);

     @Query("""
             SELECT u FROM User u
             JOIN FETCH u.roles
             WHERE u.username = :username
     """)
     Optional<User> findByUsernameWithRoles(String username);

     @Query("""
             SELECT r.name FROM User u
             JOIN u.roles r
             WHERE u.id = :id
     """)
     Set<String> findRolesByUserId(Long id);

    @Modifying
    @Query("""
            UPDATE User u
            SET u.enabled = true
            WHERE u.id = :id
    """)
    int enableByUserId(Long id);

    @Query("""
            SELECT u
            FROM User u
            JOIN FETCH u.roles
            WHERE u.id = :id
    """)
    Optional<User> findUserWithRoles(Long id);

    @Query("""
        SELECT u.enabled
        FROM User u
        WHERE u.username = :username
    """)
    Optional<Boolean> findEnabledByUsername(String username);

}
