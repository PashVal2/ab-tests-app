package org.valdon.abtests.controller.experiment.params;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.valdon.abtests.domain.experiment.enums.ExperimentStatus;
import org.valdon.abtests.domain.experiment.enums.MetricType;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExperimentSearchCriteria {

    private String name;
    private ExperimentStatus status;
    private MetricType primaryMetric;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdTo;

}