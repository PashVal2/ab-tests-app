package org.valdon.abtests.service.auth;

import org.valdon.abtests.dto.auth.RegisterRequest;

public interface RegistrationService {

    void registerUser(RegisterRequest user);

    void verifyEmail(String token);

    void resendConfirmation(String email);

}
