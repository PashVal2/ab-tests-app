package org.valdon.abtests.repository.metric;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.valdon.abtests.domain.integration.MetricResult;

import java.util.List;

@Repository
public interface MetricResultRepository extends JpaRepository<MetricResult, Long> {

    List<MetricResult> findAllByExperimentIdOrderByVariantIdAsc(Long experimentId);

    void deleteAllByExperimentId(Long experimentId);

}
