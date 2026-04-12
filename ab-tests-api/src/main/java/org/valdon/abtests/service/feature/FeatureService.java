package org.valdon.abtests.service.feature;

import jakarta.validation.constraints.NotNull;
import org.valdon.abtests.domain.experiment.Feature;
import org.valdon.abtests.dto.feature.FeatureRequest;
import org.valdon.abtests.dto.feature.FeatureResponse;

import java.util.List;

public interface FeatureService {

    FeatureResponse create(Long userId, Long projectId, FeatureRequest request);

    List<FeatureResponse> getAll(Long userId, Long projectId);

    List<FeatureResponse> getActive(Long userId, Long projectId);

    FeatureResponse activate(Long userId, Long projectId, Long featureId);

    FeatureResponse deactivate(Long userId, Long projectId, Long featureId);

    FeatureResponse update(Long userId, Long projectId, Long featureId, FeatureRequest request);

    Feature getFeatureEntity(Long featureId, Long projectId, Long userId);

}
