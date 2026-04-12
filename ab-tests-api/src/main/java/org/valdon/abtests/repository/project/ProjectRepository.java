package org.valdon.abtests.repository.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.valdon.abtests.domain.project.Project;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByOwnerIdOrderByIdDesc(Long ownerId);

    Optional<Project> findByIdAndOwnerId(Long projectId, Long ownerId);

    Optional<Project> findByCodeAndOwnerId(String code, Long ownerId);

    boolean existsByIdAndOwnerId(Long projectId, Long ownerId);

    boolean existsByCode(String code);

}