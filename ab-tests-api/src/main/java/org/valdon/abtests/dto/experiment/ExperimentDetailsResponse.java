package org.valdon.abtests.dto.experiment;

import org.valdon.abtests.domain.experiment.enums.MetricType;
import org.valdon.abtests.dto.variant.VariantResponse;

import java.util.List;

public record ExperimentDetailsResponse(

        Long id,
        String name,
        String externalKey,
        String status,
        String nullHypothesis,
        String alternativeHypothesis,
        MetricType primaryMetric,
        List<VariantResponse> variants

) { }