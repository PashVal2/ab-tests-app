package org.valdon.abtests.dto.metricResult;

import java.util.List;

public record ExperimentResultsResponse(

        Long experimentId,
        String experimentName,
        String externalKey,
        String status,
        String primaryMetric,
        Long winnerVariantId,
        String winnerVariantName,
        boolean statisticallySignificant,
        List<VariantMetricResponse> variants

) { }
