package org.valdon.abtests.service.metric;

import org.valdon.abtests.dto.metricResult.ExperimentResultsResponse;

public interface MetricService {

    ExperimentResultsResponse getResults(Long userId, Long projectId, Long experimentId);

    void recalculateAndStore(Long projectId, Long experimentId);

}
