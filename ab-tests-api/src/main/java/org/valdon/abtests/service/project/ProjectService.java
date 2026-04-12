package org.valdon.abtests.service.project;

import org.valdon.abtests.domain.project.Project;
import org.valdon.abtests.dto.project.ProjectRequest;
import org.valdon.abtests.dto.project.ProjectResponse;

import java.util.List;

public interface ProjectService {

    ProjectResponse create(Long userId, ProjectRequest request);

    List<ProjectResponse> getAllByOwnerId(Long userId);

    ProjectResponse getProjectById(Long userId, Long projectId);

    ProjectResponse getProjectByCode(Long userId, String projectCode);

    Project getOwnedProject(Long userId, Long projectId);

    Project getOwnedProjectByCode(Long userId, String projectCode);

    void validateProjectOwnership(Long userId, Long projectId);

}
