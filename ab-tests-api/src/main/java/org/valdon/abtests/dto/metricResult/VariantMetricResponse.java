package org.valdon.abtests.dto.metricResult;

public record VariantMetricResponse(

        Long variantId,
        String variantName,
        String externalCode,
        boolean control,

        long assignedUsers,
        long denominatorUsers,
        long numeratorUsers,

        long impressions,
        long clicks,
        long views,
        long watchStarts,
        long watchFinishes,
        long likes,

        double metricValue,
        String metricName,

        Double upliftPercent,
        Double pValue,

        Double diffFromControl,
        Double ci95Lower,
        Double ci95Upper,
        boolean statisticallySignificant

) { }
