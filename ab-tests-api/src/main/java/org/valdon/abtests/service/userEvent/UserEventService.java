package org.valdon.abtests.service.userEvent;

import org.valdon.abtests.controller.userEvent.params.EventSearchCriteria;
import org.valdon.abtests.domain.integration.enums.EventType;
import org.valdon.abtests.dto.userEvent.EventSummaryResponse;
import org.valdon.abtests.dto.userEvent.UserEventPageResponse;
import org.valdon.abtests.dto.userEvent.UserEventRequest;

import java.util.Map;

public interface UserEventService {

    void track(Long projectId, UserEventRequest request);

    UserEventPageResponse getAllByFilter(Long userId, Long projectId, EventSearchCriteria criteria);

    EventSummaryResponse getSummary(Long userId, Long projectId, EventSearchCriteria criteria);

    Map<Long, Map<EventType, Long>> countByVariantAndEventType(Long experimentId);

    Map<Long, Map<EventType, Long>> countDistinctByVariantAndEventType(Long experimentId);

}