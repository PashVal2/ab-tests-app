package org.valdon.abtests.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.valdon.abtests.domain.user.EmailConfirmation;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailConfirmationRepository
        extends JpaRepository<EmailConfirmation, Long>
{

    Optional<EmailConfirmation> findByToken(String token);

    List<EmailConfirmation> findAllByUserId(Long userId);

    void deleteAllByUserId(Long userId);

}
