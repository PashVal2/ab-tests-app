package org.valdon.abtests.controller.project;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.valdon.abtests.dto.project.ProjectRequest;
import org.valdon.abtests.dto.project.ProjectResponse;
import org.valdon.abtests.security.UserPrincipal;
import org.valdon.abtests.service.project.ProjectService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project")
@RequiredArgsConstructor
@Validated
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectResponse create(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody ProjectRequest request
    ) {
        return projectService.create(user.getId(), request);
    }

    @GetMapping
    public List<ProjectResponse> getMyProjects(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return projectService.getAllByOwnerId(user.getId());
    }

    @GetMapping("/{projectId}")
    public ProjectResponse getById(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long projectId
    ) {
        return projectService.getProjectById(user.getId(), projectId);
    }

    @GetMapping("/by-code/{projectCode}")
    public ProjectResponse getByCode(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String projectCode
    ) {
        return projectService.getProjectByCode(user.getId(), projectCode);
    }

}