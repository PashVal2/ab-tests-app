package org.valdon.abtests.dto.feature;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FeatureRequest(

        @NotBlank
        @Size(max = 100)
        String code,

        @NotNull
        @Size(max = 150)
        String name

) { }
