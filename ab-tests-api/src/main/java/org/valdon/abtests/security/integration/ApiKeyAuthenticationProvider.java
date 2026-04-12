package org.valdon.abtests.security.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.valdon.abtests.domain.api.ApiKey;
import org.valdon.abtests.security.ApiKeyAuthenticationToken;
import org.valdon.abtests.security.IntegrationPrincipal;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationProvider {

    private final IntegrationAuthService integrationAuthService;

    public Authentication authenticate(Authentication authentication) {
        String rawApiKey = (String) authentication.getCredentials();

        ApiKey apiKey = integrationAuthService.authenticate(rawApiKey);

        IntegrationPrincipal principal = IntegrationPrincipal.builder()
                .apiKeyId(apiKey.getId())
                .projectId(apiKey.getProject().getId())
                .build();

        return new ApiKeyAuthenticationToken(principal);
    }

    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }

}