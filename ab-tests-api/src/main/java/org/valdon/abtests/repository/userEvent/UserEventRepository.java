package org.valdon.abtests.repository.userEvent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.valdon.abtests.domain.integration.UserEvent;
import org.valdon.abtests.repository.userEvent.projection.VariantDistinctEventCountProjection;
import org.valdon.abtests.repository.userEvent.projection.VariantEventCountProjection;

import java.util.List;

@Repository
public interface UserEventRepository extends JpaRepository<UserEvent, Long>, UserEventCustomRepository {

    @Query("""
            SELECT
                e.variant.id as variantId,
                e.eventType as eventType,
                count(e) as cnt
            FROM UserEvent e
            WHERE e.experiment.id = :experimentId
            GROUP BY e.variant.id, e.eventType
    """)
    List<VariantEventCountProjection> countByVariantAndEventType(Long experimentId);

    @Query("""
        select
            ue.variant.id as variantId,
            ue.eventType as eventType,
            count(distinct ue.externalUserId) as cnt
        from UserEvent ue
        where ue.experiment.id = :experimentId
        group by ue.variant.id, ue.eventType
    """)
    List<VariantDistinctEventCountProjection> countDistinctByVariantAndEventType(Long experimentId);

}
