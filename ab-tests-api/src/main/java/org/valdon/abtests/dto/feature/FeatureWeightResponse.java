package org.valdon.abtests.dto.feature;

public record FeatureWeightResponse(

        Long featureId,
        String featureCode,
        String featureName,
        Double weight

) { }