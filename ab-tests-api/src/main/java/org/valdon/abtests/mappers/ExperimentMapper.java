package org.valdon.abtests.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.valdon.abtests.domain.experiment.Experiment;
import org.valdon.abtests.domain.experiment.Feature;
import org.valdon.abtests.domain.experiment.Variant;
import org.valdon.abtests.domain.experiment.VariantFeatureWeight;
import org.valdon.abtests.dto.experiment.ExperimentDetailsResponse;
import org.valdon.abtests.dto.experiment.ExperimentResponse;
import org.valdon.abtests.dto.feature.FeatureResponse;
import org.valdon.abtests.dto.feature.FeatureWeightResponse;
import org.valdon.abtests.dto.variant.VariantResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExperimentMapper {

    FeatureResponse toFeatureResponse(Feature feature);

    @Mapping(target = "featureId", source = "feature.id")
    @Mapping(target = "featureCode", source = "feature.code")
    @Mapping(target = "featureName", source = "feature.name")
    @Mapping(target = "weight", source = "weight")
    FeatureWeightResponse toWeightResponse(VariantFeatureWeight weight);

    default VariantResponse toVariantResponse(Variant variant, List<VariantFeatureWeight> weights) {
        return new VariantResponse(
                variant.getId(),
                variant.getName(),
                variant.isControl(),
                variant.getTrafficPercent(),
                variant.getExternalCode(),
                weights.stream().map(this::toWeightResponse).toList()
        );
    }

    default ExperimentDetailsResponse toExperimentDetailsResponse(
            Experiment experiment,
            List<VariantResponse> variants
    ) {
        return new ExperimentDetailsResponse(
                experiment.getId(),
                experiment.getName(),
                experiment.getExternalKey(),
                experiment.getStatus().name(),
                experiment.getNullHypothesis(),
                experiment.getAlternativeHypothesis(),
                experiment.getPrimaryMetric(),
                variants
        );
    }

}
