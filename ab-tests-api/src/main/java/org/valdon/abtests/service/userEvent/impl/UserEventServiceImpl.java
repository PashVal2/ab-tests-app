package org.valdon.abtests.service.userEvent.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.valdon.abtests.controller.userEvent.params.EventSearchCriteria;
import org.valdon.abtests.domain.experiment.Experiment;
import org.valdon.abtests.domain.experiment.Variant;
import org.valdon.abtests.domain.experiment.enums.ExperimentStatus;
import org.valdon.abtests.domain.integration.UserAssignment;
import org.valdon.abtests.domain.integration.UserEvent;
import org.valdon.abtests.domain.integration.enums.EventType;
import org.valdon.abtests.dto.integration.AssignUserResponse;
import org.valdon.abtests.dto.userEvent.EventSummaryResponse;
import org.valdon.abtests.dto.userEvent.EventTypeCountResponse;
import org.valdon.abtests.dto.userEvent.UserEventPageResponse;
import org.valdon.abtests.dto.userEvent.UserEventRequest;
import org.valdon.abtests.ex.ResourceNotFoundException;
import org.valdon.abtests.ex.ValidationException;
import org.valdon.abtests.repository.experiment.ExperimentRepository;
import org.valdon.abtests.repository.experiment.VariantRepository;
import org.valdon.abtests.repository.integration.UserAssignmentRepository;
import org.valdon.abtests.repository.userEvent.UserEventRepository;
import org.valdon.abtests.service.experiment.ExperimentService;
import org.valdon.abtests.service.integration.UserAssignmentService;
import org.valdon.abtests.service.project.ProjectService;
import org.valdon.abtests.service.userEvent.UserEventService;
import org.valdon.abtests.service.variant.VariantService;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserEventServiceImpl implements UserEventService {

    private final UserEventRepository userEventRepository;
    private final VariantService variantService;
    private final ProjectService projectService;
    private final ExperimentService experimentService;
    private final UserAssignmentService userAssignmentService;

    @Override
    @Transactional
    public void track(Long projectId, UserEventRequest request) {
        Experiment experiment = experimentService
                .getExperimentEntity(projectId, request.experimentExternalKey());

        if (experiment.getStatus() != ExperimentStatus.RUNNING) {
            throw new ValidationException("experiment is not running");
        }

        UserAssignment assignment = userAssignmentService
                .getUserAssignmentEntity(experiment.getId(), request.externalUserId());

        if (assignment == null) {
            AssignUserResponse assignResponse = userAssignmentService.assign(
                    projectId,
                    request.experimentExternalKey(),
                    request.externalUserId()
            );

            Variant variant = variantService.getVariantEntity(assignResponse.variantId());

            assignment = new UserAssignment(
                    null,
                    experiment,
                    variant,
                    request.externalUserId()
            );
        }

        UserEvent userEvent = new UserEvent(
                null,
                experiment,
                assignment.getVariant(),
                request.externalUserId(),
                request.contentId(),
                request.eventType(),
                LocalDateTime.now()
        );

        userEventRepository.save(userEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEventPageResponse getAllByFilter(Long userId, Long projectId, EventSearchCriteria criteria) {
        projectService.validateProjectOwnership(userId, projectId);
        return userEventRepository.findByFilter(projectId, criteria);
    }

    @Override
    @Transactional(readOnly = true)
    public EventSummaryResponse getSummary(Long userId, Long projectId, EventSearchCriteria criteria) {
        projectService.validateProjectOwnership(userId, projectId);

        Map<EventType, Long> counts = userEventRepository.countByFilter(projectId, criteria)
                .stream()
                .collect(Collectors.toMap(
                        EventTypeCountResponse::eventType,
                        EventTypeCountResponse::cnt
                ));

        long impressions = counts.getOrDefault(EventType.IMPRESSION, 0L);
        long clicks = counts.getOrDefault(EventType.CLICK, 0L);
        long views = counts.getOrDefault(EventType.VIEW, 0L);
        long watchStarts = counts.getOrDefault(EventType.WATCH_START, 0L);
        long watchFinishes = counts.getOrDefault(EventType.WATCH_FINISH, 0L);
        long likes = counts.getOrDefault(EventType.LIKE, 0L);

        return new EventSummaryResponse(
                impressions + clicks + views + watchStarts + watchFinishes + likes,
                impressions,
                clicks,
                views,
                watchStarts,
                watchFinishes,
                likes
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Map<EventType, Long>> countByVariantAndEventType(Long experimentId) {
        Map<Long, Map<EventType, Long>> result = new HashMap<>();

        userEventRepository.countByVariantAndEventType(experimentId).forEach(p -> {
            result.computeIfAbsent(p.getVariantId(), k -> new EnumMap<>(EventType.class))
                    .put(p.getEventType(), p.getCnt());
        });

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Map<EventType, Long>> countDistinctByVariantAndEventType(Long experimentId) {
        Map<Long, Map<EventType, Long>> result = new HashMap<>();

        userEventRepository.countDistinctByVariantAndEventType(experimentId).forEach(p -> {
            result.computeIfAbsent(p.getVariantId(), k -> new EnumMap<>(EventType.class))
                    .put(p.getEventType(), p.getCnt());
        });
        return result;
    }

}