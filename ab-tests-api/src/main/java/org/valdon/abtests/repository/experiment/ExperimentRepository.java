package org.valdon.abtests.repository.experiment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.valdon.abtests.domain.experiment.Experiment;
import org.valdon.abtests.domain.experiment.enums.ExperimentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExperimentRepository extends JpaRepository<Experiment, Long>, ExperimentCustomRepository {

    List<Experiment> findAllByProjectId(Long projectId);

    Optional<Experiment> findByIdAndProjectId(Long id, Long projectId);

    Optional<Experiment> findByExternalKeyAndProjectId(String externalKey, Long projectId);

    @Modifying
    @Query("""
            UPDATE Experiment e
            SET e.status = :newStatus, e.startedAt = :startedAt
            WHERE e.id = :experimentId
            AND e.project.id = :projectId
            AND e.status = :currentStatus
    """)
    int startExperiment(
            Long experimentId,
            Long projectId,
            ExperimentStatus currentStatus,
            ExperimentStatus newStatus,
            LocalDateTime startedAt
    );

    @Modifying
    @Query("""
            UPDATE Experiment e
            SET e.status = :newStatus, e.endedAt = :endedAt
            WHERE e.id = :experimentId
            AND e.project.id = :projectId
            AND e.status = :currentStatus
    """)
    int finishExperiment(
            Long experimentId,
            Long projectId,
            ExperimentStatus currentStatus,
            ExperimentStatus newStatus,
            LocalDateTime endedAt
    );

}
