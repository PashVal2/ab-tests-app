package org.valdon.abtests.dto.userEvent;

import org.valdon.abtests.domain.integration.enums.EventType;

import java.time.LocalDateTime;

public record UserEventResponse(

        Long id,
        String experimentName,
        String experimentExternalKey,
        String variantName,
        String externalUserId,
        String contentId,
        EventType eventType,
        LocalDateTime createdAt

) { }
