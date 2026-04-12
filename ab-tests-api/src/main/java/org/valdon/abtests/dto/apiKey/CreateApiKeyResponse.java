package org.valdon.abtests.dto.apiKey;

import org.valdon.abtests.domain.api.enums.ApiKeyStatus;

public record CreateApiKeyResponse(

        Long id,
        String name,
        ApiKeyStatus status,
        String rawKey

) { }
