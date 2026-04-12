package org.valdon.abtests.controller.userEvent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.valdon.abtests.controller.userEvent.params.EventSearchCriteria;
import org.valdon.abtests.dto.userEvent.EventSummaryResponse;
import org.valdon.abtests.dto.userEvent.UserEventPageResponse;
import org.valdon.abtests.dto.userEvent.UserEventResponse;
import org.valdon.abtests.security.UserPrincipal;
import org.valdon.abtests.service.userEvent.UserEventService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class UserEventController {

    private static final String PROJECT_ID_HEADER = "X-Project-Id";

    private final UserEventService eventService;

    @GetMapping
    public UserEventPageResponse getAll(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestHeader(PROJECT_ID_HEADER) Long projectId,
            @ModelAttribute EventSearchCriteria criteria
    ) {
        return eventService.getAllByFilter(user.getId(), projectId, criteria);
    }

    @GetMapping("/summary")
    public EventSummaryResponse getSummary(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestHeader(PROJECT_ID_HEADER) Long projectId,
            EventSearchCriteria criteria
    ) {
        return eventService.getSummary(user.getId(), projectId, criteria);
    }

}
