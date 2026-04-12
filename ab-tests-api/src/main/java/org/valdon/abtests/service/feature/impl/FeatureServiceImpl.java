package org.valdon.abtests.service.feature.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.valdon.abtests.domain.experiment.Feature;
import org.valdon.abtests.domain.project.Project;
import org.valdon.abtests.domain.user.User;
import org.valdon.abtests.dto.feature.FeatureRequest;
import org.valdon.abtests.dto.feature.FeatureResponse;
import org.valdon.abtests.ex.ResourceNotFoundException;
import org.valdon.abtests.ex.ValidationException;
import org.valdon.abtests.mappers.ExperimentMapper;
import org.valdon.abtests.repository.experiment.FeatureRepository;
import org.valdon.abtests.service.feature.FeatureService;
import org.valdon.abtests.service.project.ProjectService;
import org.valdon.abtests.service.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeatureServiceImpl implements FeatureService {

    private final FeatureRepository featureRepository;
    private final UserService userService;
    private final ExperimentMapper experimentMapper;
    private final ProjectService projectService;

    @Override
    @Transactional
    @CacheEvict(
            value = {"featuresByProjectId", "activeFeaturesByProjectId"},
            key = "#projectId"
    )
    public FeatureResponse create(Long userId, Long projectId, FeatureRequest request) {
        Project project = projectService.getOwnedProject(userId, projectId);

        if (featureRepository.existsByProjectIdAndCode(project.getId(), request.code())) {
            throw new ValidationException("feature code already exists");
        }

        if (featureRepository.existsByProjectIdAndName(project.getId(), request.name())) {
            throw new ValidationException("feature name already exists");
        }

        User user = userService.getUserEntity(userId);

        Feature feature = new Feature(
                null,
                project,
                request.code(),
                request.name(),
                true,
                user
        );
        return experimentMapper.toFeatureResponse(featureRepository.save(feature));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "featuresByProjectId", key = "#projectId")
    public List<FeatureResponse> getAll(Long userId, Long projectId) {
        return featureRepository.findAllOwned(projectId, userId)
                .stream()
                .map(experimentMapper::toFeatureResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "activeFeaturesByProjectId", key = "#projectId")
    public List<FeatureResponse> getActive(Long userId, Long projectId) {
        return featureRepository.findAllActiveOwned(projectId, userId)
                .stream()
                .map(experimentMapper::toFeatureResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"featuresByProjectId", "activeFeaturesByProjectId"}, key = "#projectId")
    public FeatureResponse activate(Long userId, Long projectId, Long featureId) {
        Feature feature = featureRepository.findOwned(featureId, projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("feature not found"));

        feature.activate();
        return experimentMapper.toFeatureResponse(feature);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"featuresByProjectId", "activeFeaturesByProjectId"}, key = "#projectId")
    public FeatureResponse deactivate(Long userId, Long projectId, Long featureId) {
        Feature feature = featureRepository.findOwned(featureId, projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("feature not found"));

        feature.deactivate();
        return experimentMapper.toFeatureResponse(feature);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"featuresByProjectId", "activeFeaturesByProject"}, key = "#projectId")
    public FeatureResponse update(Long userId, Long projectId, Long featureId, FeatureRequest request) {
        Feature feature = featureRepository.findOwned(featureId, projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("feature not found"));

        if (!feature.getCode().equals(request.code())
                && featureRepository.existsByProjectIdAndCodeAndIdNot(projectId, request.code(), featureId)) {
            throw new ValidationException("feature code already exists");
        }

        if (!feature.getName().equals(request.name())
                && featureRepository.existsByProjectIdAndNameAndIdNot(projectId, request.name(), featureId)) {
            throw new ValidationException("feature name already exists");
        }
        feature.update(request.code(), request.name());
        return experimentMapper.toFeatureResponse(feature);
    }

    @Override
    public Feature getFeatureEntity(Long featureId, Long projectId, Long userId){
        return featureRepository.findOwned(featureId, projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("feature not found"));
    }

}
