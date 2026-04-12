package org.valdon.abtests.repository.experiment;

import org.valdon.abtests.controller.experiment.params.ExperimentSearchCriteria;
import org.valdon.abtests.dto.experiment.ExperimentResponse;

import java.util.List;

public interface ExperimentCustomRepository {

    List<ExperimentResponse> findByFilter(Long projectId, ExperimentSearchCriteria criteria);

}
