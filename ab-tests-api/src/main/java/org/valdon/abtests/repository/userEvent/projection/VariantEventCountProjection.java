package org.valdon.abtests.repository.userEvent.projection;

import org.valdon.abtests.domain.integration.enums.EventType;

public interface VariantEventCountProjection {

    Long getVariantId();
    EventType getEventType();
    Long getCnt();

}
