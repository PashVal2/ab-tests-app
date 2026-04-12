package org.valdon.abtests.service.integration.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.valdon.abtests.domain.experiment.Experiment;
import org.valdon.abtests.domain.experiment.Variant;
import org.valdon.abtests.domain.experiment.enums.ExperimentStatus;
import org.valdon.abtests.domain.integration.UserAssignment;
import org.valdon.abtests.dto.integration.AssignUserResponse;
import org.valdon.abtests.ex.ResourceNotFoundException;
import org.valdon.abtests.ex.ValidationException;
import org.valdon.abtests.repository.experiment.ExperimentRepository;
import org.valdon.abtests.repository.experiment.VariantRepository;
import org.valdon.abtests.repository.integration.UserAssignmentRepository;
import org.valdon.abtests.repository.integration.projection.VariantAssignmentCountProjection;
import org.valdon.abtests.service.experiment.ExperimentService;
import org.valdon.abtests.service.integration.UserAssignmentService;
import org.valdon.abtests.service.variant.VariantService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAssignmentServiceImpl implements UserAssignmentService {

    private final VariantService variantService;
    private final ExperimentService experimentService;
    private final UserAssignmentRepository userAssignmentRepository;

    @Override
    @Transactional
    public AssignUserResponse assign(Long projectId, String experimentExternalKey, String externalUserId) {
        Experiment experiment = experimentService
                .getExperimentEntity(projectId, experimentExternalKey);

        if (experiment.getStatus() != ExperimentStatus.RUNNING) {
            throw new ValidationException("experiment is not running");
        }

        UserAssignment existingAssignment = userAssignmentRepository
                .findByExperimentIdAndExternalUserId(experiment.getId(), externalUserId)
                .orElse(null);

        if (existingAssignment != null) {
            return toResponse(existingAssignment.getVariant());
        }

        List<Variant> variants = variantService.getAllVariants(experiment.getId());
        if (variants.isEmpty()) {
            throw new ValidationException("experiment has no variants");
        }

        Variant selectedVariant = selectVariant(externalUserId, variants);

        UserAssignment assignment = new UserAssignment(
                null,
                experiment,
                selectedVariant,
                externalUserId
        );

        userAssignmentRepository.save(assignment);

        return toResponse(selectedVariant);
    }

    @Override
    @Transactional(readOnly = true)
    public UserAssignment getUserAssignmentEntity(Long experimentId, String externalUserId) {
        return userAssignmentRepository
                .findByExperimentIdAndExternalUserId(experimentId, externalUserId)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Long> countByVariant(Long experimentId) {
        return userAssignmentRepository.countByVariant(experimentId)
                .stream()
                .collect(Collectors.toMap(
                        VariantAssignmentCountProjection::getVariantId,
                        VariantAssignmentCountProjection::getCnt
                ));
    }

    private Variant selectVariant(String externalUserId, List<Variant> variants) {
        List<Variant> sortedVariants = variants.stream()
                .sorted(Comparator.comparing(Variant::getId))
                .toList();

        int bucket = Math.abs(externalUserId.hashCode()) % 100;
        double cumulative = 0.0;

        for (Variant variant : sortedVariants) {
            cumulative += variant.getTrafficPercent();
            if (bucket < cumulative) {
                return variant;
            }
        }

        return sortedVariants.getLast();
    }

    private AssignUserResponse toResponse(Variant variant) {
        return new AssignUserResponse(
                variant.getId(),
                variant.getName(),
                variant.getExternalCode(),
                variant.isControl()
        );
    }

}
