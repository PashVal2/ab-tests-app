package org.valdon.abtests.service.experiment.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.valdon.abtests.domain.experiment.enums.ExperimentStatus;
import org.valdon.abtests.ex.ValidationException;
import org.valdon.abtests.repository.experiment.ExperimentRepository;
import org.valdon.abtests.service.experiment.ExperimentLifecycleService;
import org.valdon.abtests.service.metric.MetricService;
import org.valdon.abtests.service.project.ProjectService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExperimentLifecycleServiceImpl implements ExperimentLifecycleService {

    private final MetricService metricService;
    private final ProjectService projectService;
    private final ExperimentRepository experimentRepository;

    @Override
    @Transactional
    public void start(Long userId, Long projectId, Long experimentId) {
        projectService.validateProjectOwnership(userId, projectId);

        int updated = experimentRepository.startExperiment(
                experimentId,
                projectId,
                ExperimentStatus.DRAFT,
                ExperimentStatus.RUNNING,
                LocalDateTime.now()
        );

        if (updated == 0) {
            throw new ValidationException("experiment cannot be started");
        }
    }

    @Override
    @Transactional
    public void finish(Long userId, Long projectId, Long experimentId) {
        projectService.validateProjectOwnership(userId, projectId);

        int updated = experimentRepository.finishExperiment(
                experimentId,
                projectId,
                ExperimentStatus.RUNNING,
                ExperimentStatus.FINISHED,
                LocalDateTime.now()
        );

        if (updated == 0) {
            throw new ValidationException("experiment cannot be finished");
        }

        metricService.recalculateAndStore(projectId, experimentId);
    }

}