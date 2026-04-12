package org.valdon.abtests.controller.integration;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.valdon.abtests.dto.integration.AssignUserRequest;
import org.valdon.abtests.dto.integration.AssignUserResponse;
import org.valdon.abtests.dto.integration.ConfigRequest;
import org.valdon.abtests.dto.integration.ConfigResponse;
import org.valdon.abtests.dto.userEvent.UserEventRequest;
import org.valdon.abtests.security.IntegrationPrincipal;
import org.valdon.abtests.service.integration.IntegrationConfigService;
import org.valdon.abtests.service.userEvent.UserEventService;
import org.valdon.abtests.service.integration.UserAssignmentService;

@Slf4j
@RestController
@RequestMapping("/api/v1/integration")
@RequiredArgsConstructor
public class IntegrationController {

    private final UserAssignmentService userAssignmentService;
    private final UserEventService userEventService;
    private final IntegrationConfigService integrationConfigService;

    @PostMapping("/assign")
    public AssignUserResponse assign(
            @AuthenticationPrincipal IntegrationPrincipal principal,
            @Valid @RequestBody AssignUserRequest request
    ) {
        return userAssignmentService.assign(
                principal.getProjectId(),
                request.experimentExternalKey(),
                request.externalUserId()
        );
    }

    @PostMapping("/events")
    public ResponseEntity<Void> trackEvent(
            @AuthenticationPrincipal IntegrationPrincipal principal,
            @Valid @RequestBody UserEventRequest request
    ) {
        userEventService.track(principal.getProjectId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/config")
    public ConfigResponse getConfig(
            @AuthenticationPrincipal IntegrationPrincipal principal,
            @Valid @RequestBody ConfigRequest request
    ) {
        return integrationConfigService.getConfig(
                principal.getProjectId(),
                request.experimentExternalKey(),
                request.externalUserId()
        );
    }

}