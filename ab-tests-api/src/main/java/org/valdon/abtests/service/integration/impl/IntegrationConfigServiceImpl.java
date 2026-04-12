package org.valdon.abtests.service.integration.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.valdon.abtests.domain.experiment.Experiment;
import org.valdon.abtests.domain.experiment.Variant;
import org.valdon.abtests.domain.experiment.enums.ExperimentStatus;
import org.valdon.abtests.domain.integration.UserAssignment;
import org.valdon.abtests.dto.integration.AssignUserResponse;
import org.valdon.abtests.dto.integration.ConfigFeatureResponse;
import org.valdon.abtests.dto.integration.ConfigResponse;
import org.valdon.abtests.ex.ValidationException;
import org.valdon.abtests.service.experiment.ExperimentService;
import org.valdon.abtests.service.integration.IntegrationConfigService;
import org.valdon.abtests.service.integration.UserAssignmentService;
import org.valdon.abtests.service.variant.VariantFeatureWeightService;
import org.valdon.abtests.service.variant.VariantService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IntegrationConfigServiceImpl implements IntegrationConfigService {

    private final VariantService variantService;
    private final ExperimentService experimentService;
    private final UserAssignmentService userAssignmentService;
    private final VariantFeatureWeightService variantFeatureWeightService;

    @Override
    @Transactional(readOnly = true)
    public ConfigResponse getConfig(Long projectId, String experimentExternalKey, String externalUserId) {
        Experiment experiment = experimentService.getExperimentEntity(projectId, experimentExternalKey);

        if (experiment.getStatus() != ExperimentStatus.RUNNING) {
            throw new ValidationException("experiment is not running");
        }

        UserAssignment assignment = userAssignmentService
                .getUserAssignmentEntity(experiment.getId(), externalUserId);

        Variant variant;
        if (assignment == null) {
            AssignUserResponse assignResponse = userAssignmentService.assign(
                    projectId,
                    experimentExternalKey,
                    externalUserId
            );
            variant = variantService.getVariantEntity(assignResponse.variantId());
        } else {
            variant = assignment.getVariant();
        }

        List<ConfigFeatureResponse> features = variantFeatureWeightService
                .getConfigFeature(variant.getId());

        return new ConfigResponse(
                experiment.getExternalKey(),
                variant.getId(),
                variant.getName(),
                variant.getExternalCode(),
                variant.isControl(),
                features
        );
    }

}
