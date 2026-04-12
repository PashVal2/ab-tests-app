package org.valdon.abtests.service.experiment.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.valdon.abtests.controller.experiment.params.ExperimentSearchCriteria;
import org.valdon.abtests.domain.experiment.Experiment;
import org.valdon.abtests.domain.experiment.Feature;
import org.valdon.abtests.domain.experiment.Variant;
import org.valdon.abtests.domain.experiment.VariantFeatureWeight;
import org.valdon.abtests.domain.experiment.enums.ExperimentStatus;
import org.valdon.abtests.domain.project.Project;
import org.valdon.abtests.domain.user.User;
import org.valdon.abtests.dto.experiment.ExperimentRequest;
import org.valdon.abtests.dto.experiment.ExperimentDetailsResponse;
import org.valdon.abtests.dto.experiment.ExperimentResponse;
import org.valdon.abtests.dto.feature.FeatureWeightRequest;
import org.valdon.abtests.dto.variant.VariantRequest;
import org.valdon.abtests.dto.variant.VariantResponse;
import org.valdon.abtests.ex.ResourceNotFoundException;
import org.valdon.abtests.ex.ValidationException;
import org.valdon.abtests.mappers.ExperimentMapper;
import org.valdon.abtests.repository.experiment.ExperimentRepository;
import org.valdon.abtests.repository.experiment.FeatureRepository;
import org.valdon.abtests.repository.experiment.VariantFeatureWeightRepository;
import org.valdon.abtests.repository.experiment.VariantRepository;
import org.valdon.abtests.repository.user.UserRepository;
import org.valdon.abtests.service.experiment.ExperimentService;
import org.valdon.abtests.service.feature.FeatureService;
import org.valdon.abtests.service.project.ProjectService;
import org.valdon.abtests.service.user.UserService;
import org.valdon.abtests.service.variant.VariantFeatureWeightService;
import org.valdon.abtests.service.variant.VariantService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperimentServiceImpl implements ExperimentService {

    private final ExperimentRepository experimentRepository;
    private final UserService userService;
    private final ProjectService projectService;
    private final FeatureService featureService;
    private final VariantService variantService;
    private final VariantFeatureWeightService variantFeatureWeightService;
    private final ExperimentMapper experimentMapper;

    @Override
    @Transactional
    public void create(Long userId, Long projectId, ExperimentRequest request) {
        Project project = projectService.getOwnedProject(userId, projectId);
        User user = userService.getUserEntity(userId);

        validateVariants(request.variants());

        String externalKey = UUID.randomUUID().toString();

        Experiment experiment = new Experiment(
                null,
                project,
                request.name(),
                externalKey,
                ExperimentStatus.DRAFT,
                LocalDateTime.now(),
                null,
                null,
                request.nullHypothesis(),
                request.alternativeHypothesis(),
                request.primaryMetric(),
                user
        );

        Experiment savedExperiment = experimentRepository.save(experiment);

        for (VariantRequest variantRequest : request.variants()) {
            Variant savedVariant = variantService.create(savedExperiment, variantRequest);

            for (FeatureWeightRequest featureRequest : variantRequest.features()) {
                Feature feature = featureService.getFeatureEntity(
                        featureRequest.featureId(),
                        project.getId(),
                        userId
                );

                variantFeatureWeightService.create(
                        savedVariant,
                        feature,
                        featureRequest.weight()
                );
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperimentResponse> getByFilter(Long userId, Long projectId, ExperimentSearchCriteria criteria) {
        projectService.validateProjectOwnership(userId, projectId);
        return experimentRepository.findByFilter(projectId, criteria);
    }

    @Override
    @Transactional(readOnly = true)
    public ExperimentDetailsResponse getById(Long userId, Long projectId, Long experimentId) {
        projectService.validateProjectOwnership(userId, projectId);

        Experiment experiment = experimentRepository.findByIdAndProjectId(experimentId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("experiment not found"));

        return buildExperimentResponse(experiment);
    }

    @Override
    @Transactional(readOnly = true)
    public ExperimentDetailsResponse getByExternalKey(Long userId, Long projectId, String externalKey) {
        projectService.validateProjectOwnership(userId, projectId);

        Experiment experiment = experimentRepository
                .findByExternalKeyAndProjectId(externalKey, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("experiment not found"));

        List<Variant> variants = variantService.getAllVariants(experiment.getId());

        List<VariantFeatureWeight> weights =
                variantFeatureWeightService.getAllByExperimentId(experiment.getId());

        Map<Long, List<VariantFeatureWeight>> weightsByVariantId =
                weights.stream().collect(Collectors.groupingBy(w -> w.getVariant().getId()));

        List<VariantResponse> variantResponses = variants.stream()
                .map(variant -> experimentMapper.toVariantResponse(
                        variant,
                        weightsByVariantId.getOrDefault(variant.getId(), List.of())
                ))
                .toList();

        return experimentMapper.toExperimentDetailsResponse(experiment, variantResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public Experiment getExperimentEntity(Long projectId, String experimentExternalKey) {
        return experimentRepository
                .findByExternalKeyAndProjectId(experimentExternalKey, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("experiment not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Experiment getExperimentEntity(Long projectId, Long experimentId) {
        return experimentRepository
                .findByIdAndProjectId(experimentId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("experiment not found"));
    }

    private ExperimentDetailsResponse buildExperimentResponse(Experiment experiment) {
        List<VariantResponse> variants = variantService.getAllVariants(experiment.getId())
                .stream()
                .map(variant -> experimentMapper.toVariantResponse(
                        variant,
                        variantFeatureWeightService.getAllByVariantId(variant.getId())
                ))
                .toList();

        return experimentMapper.toExperimentDetailsResponse(experiment, variants);
    }

    private void validateVariants(List<VariantRequest> variants) {
        if (variants.size() != 2) {
            throw new ValidationException("experiment must contain exactly 2 variants");
        }

        long controlCount = variants.stream()
                .filter(VariantRequest::control)
                .count();

        if (controlCount != 1) {
            throw new ValidationException("experiment must contain exactly 1 control variant");
        }

        double trafficSum = variants.stream()
                .mapToDouble(VariantRequest::trafficPercent)
                .sum();

        if (Math.abs(trafficSum - 100.0) > 0.0001) {
            throw new ValidationException("traffic percent sum must be 100");
        }
    }

}