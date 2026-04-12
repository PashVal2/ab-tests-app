package org.valdon.abtests.service.variant.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.valdon.abtests.domain.experiment.Experiment;
import org.valdon.abtests.domain.experiment.Variant;
import org.valdon.abtests.dto.variant.VariantRequest;
import org.valdon.abtests.ex.ResourceNotFoundException;
import org.valdon.abtests.repository.experiment.VariantRepository;
import org.valdon.abtests.service.variant.VariantService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VariantServiceImpl implements VariantService {

    private final VariantRepository variantRepository;

    @Override
    @Transactional(readOnly = true)
    public Variant getVariantEntity(Long variantId) {
        return variantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("variant not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Variant> getAllVariants(Long experimentId) {
        return variantRepository.findAllByExperimentId(experimentId);
    }

    @Override
    @Transactional
    public Variant create(Experiment experiment, VariantRequest request) {
        Variant variant = new Variant(
                null,
                experiment,
                request.name(),
                request.control(),
                request.trafficPercent(),
                request.externalCode()
        );
        return variantRepository.save(variant);
    }

}