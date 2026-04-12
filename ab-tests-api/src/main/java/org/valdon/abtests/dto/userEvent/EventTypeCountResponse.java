package org.valdon.abtests.dto.userEvent;

import org.valdon.abtests.domain.integration.enums.EventType;

public record EventTypeCountResponse(

        EventType eventType,
        Long cnt

) { }
