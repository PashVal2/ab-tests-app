package org.valdon.abtests.service.metric.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.valdon.abtests.domain.experiment.Experiment;
import org.valdon.abtests.domain.experiment.Variant;
import org.valdon.abtests.domain.experiment.enums.MetricType;
import org.valdon.abtests.domain.integration.MetricResult;
import org.valdon.abtests.domain.integration.enums.EventType;
import org.valdon.abtests.dto.metricResult.ExperimentResultsResponse;
import org.valdon.abtests.dto.metricResult.VariantMetricResponse;
import org.valdon.abtests.ex.ResourceNotFoundException;
import org.valdon.abtests.repository.experiment.VariantRepository;
import org.valdon.abtests.repository.integration.UserAssignmentRepository;
import org.valdon.abtests.repository.integration.projection.VariantAssignmentCountProjection;
import org.valdon.abtests.repository.metric.MetricResultRepository;
import org.valdon.abtests.repository.userEvent.UserEventRepository;
import org.valdon.abtests.service.experiment.ExperimentService;
import org.valdon.abtests.service.integration.UserAssignmentService;
import org.valdon.abtests.service.metric.ABTestStatistics;
import org.valdon.abtests.service.metric.MetricService;
import org.valdon.abtests.service.project.ProjectService;
import org.valdon.abtests.service.userEvent.UserEventService;
import org.valdon.abtests.service.variant.VariantService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricServiceImpl implements MetricService {

    private final MetricResultRepository metricResultRepository;
    private final ProjectService projectService;
    private final ExperimentService experimentService;
    private final VariantService variantService;
    private final UserAssignmentService userAssignmentService;
    private final UserEventService userEventService;

    @Override
    @Transactional
    public ExperimentResultsResponse getResults(Long userId, Long projectId, Long experimentId) {
        projectService.validateProjectOwnership(userId, projectId);

        Experiment experiment = experimentService.getExperimentEntity(projectId, experimentId);

        List<MetricResult> stored = metricResultRepository.findAllByExperimentIdOrderByVariantIdAsc(experimentId);

        if (stored.isEmpty()) {
            recalculateAndStore(projectId, experimentId);
            stored = metricResultRepository.findAllByExperimentIdOrderByVariantIdAsc(experimentId);
        }

        return buildResultsFromStored(experiment, stored);
    }

    @Override
    @Transactional
    public void recalculateAndStore(Long projectId, Long experimentId) {
        Experiment experiment = experimentService.getExperimentEntity(projectId, experimentId);

        List<VariantMetricResponse> calculated = calculateVariantMetrics(experiment);

        metricResultRepository.deleteAllByExperimentId(experiment.getId());

        Map<Long, Variant> variantsById = variantService.getAllVariants(experiment.getId())
                .stream()
                .collect(Collectors.toMap(Variant::getId, v -> v));

        for (VariantMetricResponse metric : calculated) {
            Variant variant = variantsById.get(metric.variantId());

            if (variant == null) {
                throw new ResourceNotFoundException("variant not found");
            }

            MetricResult metricResult = new MetricResult(
                    null,
                    experiment,
                    variant,
                    experiment.getPrimaryMetric().name(),
                    metric.metricValue(),
                    metric.assignedUsers(),
                    metric.denominatorUsers(),
                    metric.numeratorUsers(),
                    metric.impressions(),
                    metric.clicks(),
                    metric.views(),
                    metric.watchStarts(),
                    metric.watchFinishes(),
                    metric.likes(),
                    metric.upliftPercent(),
                    metric.pValue(),
                    metric.diffFromControl(),
                    metric.ci95Lower(),
                    metric.ci95Upper(),
                    metric.statisticallySignificant()
            );

            metricResultRepository.save(metricResult);
        }
    }

    private ExperimentResultsResponse buildResultsFromStored(Experiment experiment, List<MetricResult> stored) {
        List<VariantMetricResponse> variants = stored.stream()
                .map(metric -> new VariantMetricResponse(
                        metric.getVariant().getId(),
                        metric.getVariant().getName(),
                        metric.getVariant().getExternalCode(),
                        metric.getVariant().isControl(),

                        metric.getAssignedUsers(),
                        metric.getDenominatorUsers(),
                        metric.getNumeratorUsers(),

                        metric.getImpressions(),
                        metric.getClicks(),
                        metric.getViews(),
                        metric.getWatchStarts(),
                        metric.getWatchFinishes(),
                        metric.getLikes(),

                        metric.getMetricValue(),
                        metric.getMetricName(),

                        metric.getUpliftPercent(),
                        metric.getPValue(),

                        metric.getDiffFromControl(),
                        metric.getCi95Lower(),
                        metric.getCi95Upper(),
                        metric.isStatisticallySignificant()
                ))
                .toList();

        VariantMetricResponse winner = variants.stream()
                .filter(v -> !v.control())
                .filter(VariantMetricResponse::statisticallySignificant)
                .max(Comparator.comparingDouble(VariantMetricResponse::metricValue))
                .orElse(null);

        boolean statisticallySignificant = winner != null;

        return new ExperimentResultsResponse(
                experiment.getId(),
                experiment.getName(),
                experiment.getExternalKey(),
                experiment.getStatus().name(),
                experiment.getPrimaryMetric().name(),
                winner != null ? winner.variantId() : null,
                winner != null ? winner.variantName() : null,
                statisticallySignificant,
                variants
        );
    }

    private List<VariantMetricResponse> calculateVariantMetrics(Experiment experiment) {
        MetricType metricType = experiment.getPrimaryMetric();

        List<Variant> variants = variantService.getAllVariants(experiment.getId());

        Map<Long, Long> assignedUsers = userAssignmentService.countByVariant(experiment.getId());

        Map<Long, Map<EventType, Long>> rawEventStats =
                userEventService.countByVariantAndEventType(experiment.getId());

        Map<Long, Map<EventType, Long>> distinctUserEventStats =
                userEventService.countDistinctByVariantAndEventType(experiment.getId());

        Variant controlVariant = variants.stream()
                .filter(Variant::isControl)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("control variant not found"));

        long controlNumeratorUsers = getUserEventCount(
                distinctUserEventStats,
                controlVariant.getId(),
                getNumeratorEvent(metricType)
        );

        long controlDenominatorUsers = getUserEventCount(
                distinctUserEventStats,
                controlVariant.getId(),
                getDenominatorEvent(metricType)
        );

        double controlMetricValue = rate(controlNumeratorUsers, controlDenominatorUsers);

        List<VariantMetricResponse> result = new ArrayList<>();

        for (Variant variant : variants) {
            long assigned = assignedUsers.getOrDefault(variant.getId(), 0L);

            long impressions = getRawEventCount(rawEventStats, variant.getId(), EventType.IMPRESSION);
            long clicks = getRawEventCount(rawEventStats, variant.getId(), EventType.CLICK);
            long views = getRawEventCount(rawEventStats, variant.getId(), EventType.VIEW);
            long watchStarts = getRawEventCount(rawEventStats, variant.getId(), EventType.WATCH_START);
            long watchFinishes = getRawEventCount(rawEventStats, variant.getId(), EventType.WATCH_FINISH);
            long likes = getRawEventCount(rawEventStats, variant.getId(), EventType.LIKE);

            long numeratorUsers = getUserEventCount(
                    distinctUserEventStats,
                    variant.getId(),
                    getNumeratorEvent(metricType)
            );

            long denominatorUsers = getUserEventCount(
                    distinctUserEventStats,
                    variant.getId(),
                    getDenominatorEvent(metricType)
            );

            double metricValue = rate(numeratorUsers, denominatorUsers);

            Double upliftPercent = null;
            Double pValue = null;
            Double diffFromControl = null;
            Double ci95Lower = null;
            Double ci95Upper = null;
            boolean significant = false;

            if (!variant.isControl()) {
                upliftPercent = ABTestStatistics.calculateUplift(controlMetricValue, metricValue);

                diffFromControl = ABTestStatistics.calculateAbsoluteDifference(
                        controlNumeratorUsers,
                        controlDenominatorUsers,
                        numeratorUsers,
                        denominatorUsers
                );

                pValue = ABTestStatistics.calculatePValue(
                        controlNumeratorUsers,
                        controlDenominatorUsers,
                        numeratorUsers,
                        denominatorUsers
                );

                significant = ABTestStatistics.isSignificant(pValue);

                ABTestStatistics.ConfidenceInterval ci =
                        ABTestStatistics.calculateDifferenceConfidenceInterval95(
                                controlNumeratorUsers,
                                controlDenominatorUsers,
                                numeratorUsers,
                                denominatorUsers
                        );

                if (ci != null) {
                    ci95Lower = ci.lowerBound();
                    ci95Upper = ci.upperBound();
                }
            }

            result.add(new VariantMetricResponse(
                    variant.getId(),
                    variant.getName(),
                    variant.getExternalCode(),
                    variant.isControl(),

                    assigned,
                    denominatorUsers,
                    numeratorUsers,

                    impressions,
                    clicks,
                    views,
                    watchStarts,
                    watchFinishes,
                    likes,

                    metricValue,
                    metricType.name(),

                    upliftPercent,
                    pValue,

                    diffFromControl,
                    ci95Lower,
                    ci95Upper,
                    significant
            ));
        }

        return result;
    }

    private EventType getNumeratorEvent(MetricType metricType) {
        return switch (metricType) {
            case CTR -> EventType.CLICK;
            case VIEW_RATE -> EventType.VIEW;
            case LIKE_RATE -> EventType.LIKE;
            case WATCH_START_RATE -> EventType.WATCH_START;
            case WATCH_FINISH_RATE, WATCH_THROUGH_RATE -> EventType.WATCH_FINISH;
        };
    }

    private EventType getDenominatorEvent(MetricType metricType) {
        return switch (metricType) {
            case CTR, VIEW_RATE, LIKE_RATE, WATCH_START_RATE, WATCH_FINISH_RATE -> EventType.IMPRESSION;
            case WATCH_THROUGH_RATE -> EventType.WATCH_START;
        };
    }

    private long getRawEventCount(Map<Long, Map<EventType, Long>> eventStats, Long variantId, EventType eventType) {
        return eventStats.getOrDefault(variantId, Collections.emptyMap())
                .getOrDefault(eventType, 0L);
    }

    private long getUserEventCount(Map<Long, Map<EventType, Long>> eventStats, Long variantId, EventType eventType) {
        return eventStats.getOrDefault(variantId, Collections.emptyMap())
                .getOrDefault(eventType, 0L);
    }

    private double rate(long numerator, long denominator) {
        if (denominator <= 0) {
            return 0.0;
        }
        return (double) numerator / denominator;
    }

}