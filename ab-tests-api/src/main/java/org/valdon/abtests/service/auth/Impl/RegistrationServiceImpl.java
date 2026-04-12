package org.valdon.abtests.service.auth.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.valdon.abtests.domain.user.User;
import org.valdon.abtests.dto.auth.RegisterRequest;
import org.valdon.abtests.dto.user.UserCreatedEvent;
import org.valdon.abtests.ex.UserAlreadyVerifiedException;
import org.valdon.abtests.ex.ValidationException;
import org.valdon.abtests.pubsub.RedisMessagePublisher;
import org.valdon.abtests.service.auth.RegistrationService;
import org.valdon.abtests.service.mail.EmailConfirmationService;
import org.valdon.abtests.service.mail.EmailRateLimitService;
import org.valdon.abtests.service.user.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserService userService;
    private final EmailConfirmationService emailConfirmationService;
    private final EmailRateLimitService emailRateLimitService;
    private final RedisMessagePublisher publisher;

    @Override
    @Transactional
    public void registerUser(RegisterRequest user) {
        if (!user.password().equals(user.passwordConfirm())) {
            throw new ValidationException("passwords must match");
        }
        String normalizedEmail = user.username().trim().toLowerCase();

        User createdUser = userService.createUser(
                user.name(),
                normalizedEmail,
                user.password()
        );
        String token = emailConfirmationService.createTokenFor(createdUser);

        publisher.publishUserEvent(new UserCreatedEvent(
                createdUser.getId(),
                createdUser.getName(),
                normalizedEmail,
                token
        ));
    }

    @Override
    @Transactional
    public void verifyEmail(String token){
        User verifiedUser = emailConfirmationService.verifyToken(token);
        userService.enableUser(verifiedUser.getId());
    }

    @Override
    @Transactional
    public void resendConfirmation(String email){
        String normalizedEmail = email.trim().toLowerCase();
        User user = userService.getUserEntity(normalizedEmail);
        if (user.isEnabled()) {
            throw new UserAlreadyVerifiedException("user already verified");
        }

        emailRateLimitService.checkResend(normalizedEmail);
        String token = emailConfirmationService.createTokenFor(user);
        publisher.publishUserEvent(new UserCreatedEvent(
                user.getId(),
                user.getName(),
                normalizedEmail,
                token
        ));
    }

}
