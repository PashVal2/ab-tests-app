package org.valdon.abtests.repository.experiment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.valdon.abtests.domain.experiment.Experiment;
import org.valdon.abtests.domain.experiment.Variant;

import java.util.List;
import java.util.Optional;

@Repository
public interface VariantRepository extends JpaRepository<Variant, Long> {

    List<Variant> findAllByExperimentId(Long experimentId);

}