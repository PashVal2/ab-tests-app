package org.valdon.abtests.service.experiment;

import org.springframework.transaction.annotation.Transactional;
import org.valdon.abtests.controller.experiment.params.ExperimentSearchCriteria;
import org.valdon.abtests.domain.experiment.Experiment;
import org.valdon.abtests.dto.experiment.ExperimentRequest;
import org.valdon.abtests.dto.experiment.ExperimentDetailsResponse;
import org.valdon.abtests.dto.experiment.ExperimentResponse;

import java.util.List;

public interface ExperimentService {

    void create(Long userId, Long projectId, ExperimentRequest request);

    List<ExperimentResponse> getByFilter(Long userId, Long projectId, ExperimentSearchCriteria criteria);

    ExperimentDetailsResponse getById(Long userId, Long projectId, Long experimentId);

    ExperimentDetailsResponse getByExternalKey(Long id, Long projectId, String externalKey);

    Experiment getExperimentEntity(Long projectId, String experimentExternalKey);

    Experiment getExperimentEntity(Long projectId, Long experimentId);
}
