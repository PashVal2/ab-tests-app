package org.valdon.abtests.dto.variant;

import jakarta.validation.constraints.*;
import org.valdon.abtests.dto.feature.FeatureWeightRequest;

import java.util.List;

public record VariantRequest(

        @NotNull
        @NotBlank
        String name,

        @NotNull
        Boolean control,

        @NotNull
        @DecimalMin("0.0")
        @DecimalMax("100.0")
        Double trafficPercent,

        @NotNull
        @NotBlank
        String externalCode,

        @NotNull
        @Size(min = 1)
        List<FeatureWeightRequest> features

) { }