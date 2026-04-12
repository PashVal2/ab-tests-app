package org.valdon.abtests.controller.feature;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.valdon.abtests.domain.user.User;
import org.valdon.abtests.dto.feature.FeatureRequest;
import org.valdon.abtests.dto.feature.FeatureResponse;
import org.valdon.abtests.security.UserPrincipal;
import org.valdon.abtests.service.feature.FeatureService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/features")
@RequiredArgsConstructor
@Validated
public class FeatureController {

    private static final String PROJECT_ID_HEADER = "X-Project-Id";

    private final FeatureService featureService;

    @PostMapping
    public FeatureResponse create(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestHeader(PROJECT_ID_HEADER) Long projectId,
            @Valid @RequestBody FeatureRequest request
    ) {
        return featureService.create(user.getId(), projectId, request);
    }

    @GetMapping
    public List<FeatureResponse> getAll(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestHeader(PROJECT_ID_HEADER) Long projectId
    ) {
        return featureService.getAll(user.getId(), projectId);
    }

    @GetMapping("/active")
    public List<FeatureResponse> getActive(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestHeader(PROJECT_ID_HEADER) Long projectId
    ) {
        return featureService.getActive(userPrincipal.getId(), projectId);
    }

    @PostMapping("/{featureId}/activate")
    public FeatureResponse activate(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestHeader(PROJECT_ID_HEADER) Long projectId,
            @PathVariable Long featureId
    ) {
        return featureService.activate(userPrincipal.getId(), projectId, featureId);
    }

    @PostMapping("/{featureId}/deactivate")
    public FeatureResponse deactivate(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestHeader(PROJECT_ID_HEADER) Long projectId,
            @PathVariable Long featureId
    ) {
        return featureService.deactivate(userPrincipal.getId(), projectId, featureId);
    }

    @PutMapping("/{featureId}")
    public FeatureResponse update(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody FeatureRequest request,
            @PathVariable Long featureId,
            @RequestHeader(PROJECT_ID_HEADER) Long projectId
    ) {
        return featureService.update(
                user.getId(),
                projectId,
                featureId,
                request
        );
    }

}
