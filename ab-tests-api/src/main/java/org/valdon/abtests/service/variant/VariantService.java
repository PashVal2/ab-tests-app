package org.valdon.abtests.service.variant;

import org.valdon.abtests.domain.experiment.Experiment;
import org.valdon.abtests.domain.experiment.Variant;
import org.valdon.abtests.dto.variant.VariantRequest;

import java.util.List;
import java.util.Map;

public interface VariantService {

    Variant getVariantEntity(Long variantId);

    List<Variant> getAllVariants(Long experimentId);

    Variant create(Experiment experiment, VariantRequest request);

}
