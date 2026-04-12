package org.valdon.abtests.dto.experiment;

import org.valdon.abtests.domain.experiment.enums.ExperimentStatus;
import org.valdon.abtests.domain.experiment.enums.MetricType;

public record ExperimentResponse(

        Long id,
        String name,
        String externalKey,
        ExperimentStatus status,
        MetricType primaryMetric

) { }
