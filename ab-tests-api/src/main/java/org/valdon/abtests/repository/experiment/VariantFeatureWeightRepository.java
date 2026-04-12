package org.valdon.abtests.repository.experiment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.valdon.abtests.domain.experiment.Variant;
import org.valdon.abtests.domain.experiment.VariantFeatureWeight;

import java.util.List;

@Repository
public interface VariantFeatureWeightRepository extends JpaRepository<VariantFeatureWeight, Long> {

    List<VariantFeatureWeight> findAllByVariantId(Long variantId);

    void deleteAllByVariantId(Long variantId);

    List<VariantFeatureWeight> findAllByVariantExperimentId(Long experimentId);

}