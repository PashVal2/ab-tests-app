package org.valdon.abtests.service.experiment;

public interface ExperimentLifecycleService {

    void start(Long userId, Long projectId, Long experimentId);

    void finish(Long userId, Long projectId, Long experimentId);

}
