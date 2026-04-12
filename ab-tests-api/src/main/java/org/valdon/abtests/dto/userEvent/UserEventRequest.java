package org.valdon.abtests.dto.userEvent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.valdon.abtests.domain.integration.enums.EventType;

public record UserEventRequest(

        @NotNull
        @NotBlank
        String experimentExternalKey,

        @NotNull
        @NotBlank
        String externalUserId,

        String contentId,

        @NotNull
        EventType eventType

) { }
