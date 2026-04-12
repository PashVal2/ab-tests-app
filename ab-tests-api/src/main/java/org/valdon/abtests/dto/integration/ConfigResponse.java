package org.valdon.abtests.dto.integration;

import java.util.List;

public record ConfigResponse(

        String experimentExternalKey,
        Long variantId,
        String variantName,
        String externalCode,
        boolean control,
        List<ConfigFeatureResponse> features

) { }
