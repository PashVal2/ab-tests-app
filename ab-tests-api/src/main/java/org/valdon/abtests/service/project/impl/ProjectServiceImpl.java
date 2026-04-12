package org.valdon.abtests.service.project.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.valdon.abtests.domain.project.Project;
import org.valdon.abtests.domain.user.User;
import org.valdon.abtests.dto.project.ProjectRequest;
import org.valdon.abtests.dto.project.ProjectResponse;
import org.valdon.abtests.ex.ResourceNotFoundException;
import org.valdon.abtests.mappers.ProjectMapper;
import org.valdon.abtests.repository.project.ProjectRepository;
import org.valdon.abtests.repository.user.UserRepository;
import org.valdon.abtests.service.project.ProjectService;
import org.valdon.abtests.service.user.UserService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserService userService;

    @Override
    @Transactional
    @CacheEvict(value = "projectsByUser", key = "#userId")
    public ProjectResponse create(Long userId, ProjectRequest request) {
        User user = userService.getUserEntity(userId);

        String code = generateUniqueCode();

        Project project = new Project(
                null,
                user,
                request.name(),
                code
        );

        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "projectsByUser", key = "#userId")
    public List<ProjectResponse> getAllByOwnerId(Long userId) {
        return projectRepository.findAllByOwnerIdOrderByIdDesc(userId)
                .stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "projectById", key = "#userId + ':' + #projectId")
    public ProjectResponse getProjectById(Long userId, Long projectId) {
        return projectMapper.toDto(getOwnedProject(userId, projectId));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "projectByCode", key = "#userId + ':' + #projectCode")
    public ProjectResponse getProjectByCode(Long userId, String projectCode) {
        return projectMapper.toDto(getOwnedProjectByCode(userId, projectCode));
    }

    @Override
    @Transactional(readOnly = true)
    public Project getOwnedProject(Long userId, Long projectId) {
        return projectRepository.findByIdAndOwnerId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("project not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Project getOwnedProjectByCode(Long userId, String projectCode) {
        return projectRepository.findByCodeAndOwnerId(projectCode, userId)
                .orElseThrow(() -> new ResourceNotFoundException("project not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public void validateProjectOwnership(Long userId, Long projectId) {
        if (!projectRepository.existsByIdAndOwnerId(projectId, userId)) {
            throw new ResourceNotFoundException("project not found");
        }
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = UUID.randomUUID().toString();
        } while (projectRepository.existsByCode(code));

        return code;
    }

}