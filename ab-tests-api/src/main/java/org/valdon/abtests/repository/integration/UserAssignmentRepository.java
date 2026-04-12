package org.valdon.abtests.repository.integration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.valdon.abtests.domain.integration.UserAssignment;
import org.valdon.abtests.repository.integration.projection.VariantAssignmentCountProjection;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAssignmentRepository extends JpaRepository<UserAssignment, Long> {

    Optional<UserAssignment> findByExperimentIdAndExternalUserId(Long experimentId, String externalUserId);

    @Query("""
            SELECT a.variant.id as variantId, count(a) as cnt
            FROM UserAssignment a
            WHERE a.experiment.id = :experimentId
            GROUP BY a.variant.id
    """)
    List<VariantAssignmentCountProjection> countByVariant(Long experimentId);

}
