package org.valdon.abtests.service.variant.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.valdon.abtests.domain.experiment.Feature;
import org.valdon.abtests.domain.experiment.Variant;
import org.valdon.abtests.domain.experiment.VariantFeatureWeight;
import org.valdon.abtests.dto.integration.ConfigFeatureResponse;
import org.valdon.abtests.repository.experiment.VariantFeatureWeightRepository;
import org.valdon.abtests.service.variant.VariantFeatureWeightService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VariantFeatureWeightServiceImpl implements VariantFeatureWeightService {

    private final VariantFeatureWeightRepository variantFeatureWeightRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ConfigFeatureResponse> getConfigFeature(Long variantId) {
        return variantFeatureWeightRepository
                .findAllByVariantId(variantId)
                .stream()
                .map(weight -> new ConfigFeatureResponse(
                        weight.getFeature().getCode(),
                        weight.getFeature().getName(),
                        weight.getWeight().toString()
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VariantFeatureWeight> getAllByVariantId(Long variantId) {
        return variantFeatureWeightRepository.findAllByVariantId(variantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VariantFeatureWeight> getAllByExperimentId(Long experimentId) {
        return variantFeatureWeightRepository.findAllByVariantExperimentId(experimentId);
    }

    @Override
    @Transactional
    public VariantFeatureWeight create(Variant variant, Feature feature, Double weight) {
        VariantFeatureWeight entity = new VariantFeatureWeight(
                null,
                variant,
                feature,
                weight
        );
        return variantFeatureWeightRepository.save(entity);
    }

}
