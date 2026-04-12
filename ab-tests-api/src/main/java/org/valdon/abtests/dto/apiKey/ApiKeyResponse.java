package org.valdon.abtests.dto.apiKey;

import org.valdon.abtests.domain.api.enums.ApiKeyStatus;

import java.time.LocalDateTime;

public record ApiKeyResponse(

        Long id,
        String name,
        String keyPrefix,
        ApiKeyStatus status,
        LocalDateTime createdAt

) { }
