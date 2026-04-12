package org.valdon.abtests.dto.feature;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record FeatureWeightRequest(

        @NotNull
        Long featureId,

        @NotNull
        @PositiveOrZero
        Double weight

) { }
