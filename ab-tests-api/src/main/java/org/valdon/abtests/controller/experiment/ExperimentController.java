package org.valdon.abtests.controller.experiment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.valdon.abtests.controller.experiment.params.ExperimentSearchCriteria;
import org.valdon.abtests.dto.experiment.ExperimentRequest;
import org.valdon.abtests.dto.experiment.ExperimentDetailsResponse;
import org.valdon.abtests.dto.experiment.ExperimentResponse;
import org.valdon.abtests.dto.metricResult.ExperimentResultsResponse;
import org.valdon.abtests.security.UserPrincipal;
import org.valdon.abtests.service.experiment.ExperimentLifecycleService;
import org.valdon.abtests.service.experiment.ExperimentService;
import org.valdon.abtests.service.metric.MetricService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/experiment")
@RequiredArgsConstructor
public class ExperimentController {

    private static final String PROJECT_ID_HEADER = "X-Project-Id";

    private final ExperimentService experimentService;
    private final ExperimentLifecycleService experimentLifecycleService;
    private final MetricService metricService;

    @PostMapping
    public ResponseEntity<Void> createExperiments(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestHeader(PROJECT_ID_HEADER) Long projectId,
            @Valid @RequestBody ExperimentRequest request
    ) {
        experimentService.create(user.getId(), projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public List<ExperimentResponse> getAll(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestHeader(PROJECT_ID_HEADER) Long projectId,
            @ModelAttribute ExperimentSearchCriteria criteria
    ) {
        return experimentService.getByFilter(user.getId(), projectId, criteria);
    }

    @GetMapping("/{id}")
    public ExperimentDetailsResponse getById(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestHeader(PROJECT_ID_HEADER) Long projectId,
            @PathVariable Long id
    ) {
        return experimentService.getById(user.getId(), projectId, id);
    }

    @GetMapping("/by-key/{externalKey}")
    public ExperimentDetailsResponse getByExternalKey(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestHeader("X-Project-Id") Long projectId,
            @PathVariable String externalKey
    ) {
        return experimentService.getByExternalKey(user.getId(), projectId, externalKey);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Void> start(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestHeader(PROJECT_ID_HEADER) Long projectId,
            @PathVariable Long id
    ) {
        experimentLifecycleService.start(user.getId(), projectId, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<Void> finish(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestHeader(PROJECT_ID_HEADER) Long projectId,
            @PathVariable Long id
    ) {
        experimentLifecycleService.finish(user.getId(), projectId, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/results")
    public ExperimentResultsResponse getResults(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestHeader(PROJECT_ID_HEADER) Long projectId,
            @PathVariable Long id
    ) {
        return metricService.getResults(user.getId(), projectId, id);
    }

}