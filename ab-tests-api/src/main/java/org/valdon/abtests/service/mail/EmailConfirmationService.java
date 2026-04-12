package org.valdon.abtests.service.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.valdon.abtests.domain.user.EmailConfirmation;
import org.valdon.abtests.domain.user.User;
import org.valdon.abtests.ex.ResourceNotFoundException;
import org.valdon.abtests.ex.ValidationException;
import org.valdon.abtests.repository.auth.EmailConfirmationRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailConfirmationService {

    private final EmailConfirmationRepository emailConfirmationRepository;

    private static final Duration TOKEN_TTL = Duration.ofHours(24);

    @Transactional
    public String createTokenFor(User user){
        emailConfirmationRepository.deleteAllByUserId(user.getId());

        String token = UUID.randomUUID().toString();
        EmailConfirmation emailConfirm = new EmailConfirmation(
                null,
                token,
                user,
                LocalDateTime.now().plus(TOKEN_TTL)
        );

        emailConfirmationRepository.save(emailConfirm);
        return token;
    }

    @Transactional(readOnly = true)
    public User verifyToken(String token) {
        EmailConfirmation emailConfirm = emailConfirmationRepository
                .findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("token not found"));

        if (emailConfirm.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Token expired");
        }

        return emailConfirm.getUser();
    }

}
