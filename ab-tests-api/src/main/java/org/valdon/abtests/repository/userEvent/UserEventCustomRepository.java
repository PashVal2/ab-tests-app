package org.valdon.abtests.repository.userEvent;

import org.valdon.abtests.controller.userEvent.params.EventSearchCriteria;
import org.valdon.abtests.dto.userEvent.EventTypeCountResponse;
import org.valdon.abtests.dto.userEvent.UserEventPageResponse;

import java.util.List;

public interface UserEventCustomRepository {

    UserEventPageResponse findByFilter(Long projectId, EventSearchCriteria criteria);
    List<EventTypeCountResponse> countByFilter(Long projectId, EventSearchCriteria criteria);

}