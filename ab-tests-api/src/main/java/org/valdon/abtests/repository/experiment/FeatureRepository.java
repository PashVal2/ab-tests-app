package org.valdon.abtests.repository.experiment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.valdon.abtests.domain.experiment.Feature;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {

    @Query("""
        SELECT f
        FROM Feature f
        WHERE f.project.id = :projectId
            AND f.project.owner.id = :userId
        ORDER BY f.id ASC
    """)
    List<Feature> findAllOwned(Long projectId, Long userId);

    @Query("""
        SELECT f
        FROM Feature f
        WHERE f.project.id = :projectId
            AND f.project.owner.id = :userId
            AND f.active = true
        ORDER BY f.id ASC
    """)
    List<Feature> findAllActiveOwned(Long projectId, Long userId);

    @Query("""
        SELECT f
        FROM Feature f
        WHERE f.id = :id
            AND f.project.id = :projectId
            AND f.project.owner.id = :userId
    """)
    Optional<Feature> findOwned(Long id, Long projectId, Long userId);

    boolean existsByProjectIdAndCode(Long projectId, String code);

    boolean existsByProjectIdAndName(Long projectId, String name);

    boolean existsByProjectIdAndCodeAndIdNot(Long projectId, String code, Long featureId);

    boolean existsByProjectIdAndNameAndIdNot(Long projectId, String name, Long featureId);

}
