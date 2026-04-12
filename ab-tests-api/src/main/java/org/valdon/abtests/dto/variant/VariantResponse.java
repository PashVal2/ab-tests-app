package org.valdon.abtests.dto.variant;

import org.valdon.abtests.dto.feature.FeatureWeightResponse;

import java.util.List;

public record VariantResponse(

        Long id,
        String name,
        boolean control,
        Double trafficPercent,
        String externalCode,
        List<FeatureWeightResponse> features

) { }