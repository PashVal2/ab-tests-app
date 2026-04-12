package org.valdon.abtests.dto.experiment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.valdon.abtests.domain.experiment.enums.MetricType;
import org.valdon.abtests.dto.variant.VariantRequest;

import java.util.List;

@Validated
public record ExperimentRequest(

        @NotNull
        @NotBlank
        String name,

        @NotNull
        @NotBlank
        String nullHypothesis,

        @NotNull
        @NotBlank
        String alternativeHypothesis,

        @NotNull
        MetricType primaryMetric,

        @NotNull
        @Size(min = 2, max = 2)
        List<@Valid VariantRequest> variants

) { }
