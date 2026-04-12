package org.valdon.abtests.service.variant;

import org.valdon.abtests.domain.experiment.Feature;
import org.valdon.abtests.domain.experiment.Variant;
import org.valdon.abtests.domain.experiment.VariantFeatureWeight;
import org.valdon.abtests.dto.integration.ConfigFeatureResponse;

import java.util.List;

public interface VariantFeatureWeightService {

    List<ConfigFeatureResponse> getConfigFeature(Long variantId);

    List<VariantFeatureWeight> getAllByVariantId(Long variantId);

    List<VariantFeatureWeight> getAllByExperimentId(Long experimentId);

    VariantFeatureWeight create(Variant variant, Feature feature, Double weight);

}
