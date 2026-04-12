package org.valdon.abtests.security.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.valdon.abtests.domain.api.ApiKey;
import org.valdon.abtests.domain.api.enums.ApiKeyStatus;
import org.valdon.abtests.ex.UnauthorizedException;
import org.valdon.abtests.repository.apiKey.ApiKeyRepository;
import org.valdon.abtests.service.apiKey.ApiKeyHashUtil;

@Service
@RequiredArgsConstructor
public class IntegrationAuthService {

    private final ApiKeyRepository apiKeyRepository;

    @Transactional(readOnly = true)
    public ApiKey authenticate(String rawApiKey) {
        if (rawApiKey == null || rawApiKey.isBlank()) {
            throw new UnauthorizedException("api key is missing");
        }

        String keyHash = ApiKeyHashUtil.sha256(rawApiKey);

        return apiKeyRepository.findByKeyHashAndStatus(keyHash, ApiKeyStatus.ACTIVE)
                .orElseThrow(() -> new UnauthorizedException("invalid api key"));
    }

}