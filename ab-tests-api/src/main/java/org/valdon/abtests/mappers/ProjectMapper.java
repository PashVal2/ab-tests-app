package org.valdon.abtests.mappers;

import org.mapstruct.Mapper;
import org.valdon.abtests.domain.project.Project;
import org.valdon.abtests.dto.project.ProjectResponse;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponse toDto(Project project);

}