package org.valdon.abtests.repository.experiment.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Repository;
import org.valdon.abtests.controller.experiment.params.ExperimentSearchCriteria;
import org.valdon.abtests.dto.experiment.ExperimentResponse;
import org.valdon.abtests.repository.experiment.ExperimentCustomRepository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExperimentCustomRepositoryImpl implements ExperimentCustomRepository {

    private final EntityManager entityManager;

    @Override
    public List<ExperimentResponse> findByFilter(Long projectId, ExperimentSearchCriteria criteria) {
        if (projectId == null) {
            throw new IllegalArgumentException("project id is null");
        }

        StringBuilder jpql = new StringBuilder("""
                SELECT new org.valdon.abtests.dto.experiment.ExperimentResponse(
                    e.id,
                    e.name,
                    e.externalKey,
                    e.status,
                    e.primaryMetric
                )
                FROM Experiment e
                WHERE e.project.id = :projectId
                """);

        List<String> conditions = getStrings(criteria);

        for (String condition : conditions) {
            jpql.append(" and ").append(condition);
        }

        jpql.append(" order by e.createdAt desc");

        TypedQuery<ExperimentResponse> query =
                entityManager.createQuery(jpql.toString(), ExperimentResponse.class);

        query.setParameter("projectId", projectId);
        setParams(query, criteria);

        return query.getResultList();
    }

    private void setParams(TypedQuery<ExperimentResponse> query, ExperimentSearchCriteria criteria) {
        if (criteria != null) {

            if (criteria.getName() != null && !criteria.getName().isBlank()) {
                query.setParameter("name", "%" + criteria.getName().trim() + "%");
            }

            if (criteria.getStatus() != null) {
                query.setParameter("status", criteria.getStatus());
            }

            if (criteria.getPrimaryMetric() != null) {
                query.setParameter("primaryMetric", criteria.getPrimaryMetric());
            }

            if (criteria.getCreatedFrom() != null) {
                query.setParameter("createdFrom", criteria.getCreatedFrom());
            }

            if (criteria.getCreatedTo() != null) {
                query.setParameter("createdTo", criteria.getCreatedTo());
            }
        }
    }

    private static @NonNull List<String> getStrings(ExperimentSearchCriteria criteria){
        List<String> conditions = new ArrayList<>();

        if (criteria != null) {

            if (criteria.getName() != null && !criteria.getName().isBlank()) {
                conditions.add("lower(e.name) like lower(:name)");
            }

            if (criteria.getStatus() != null) {
                conditions.add("e.status = :status");
            }

            if (criteria.getPrimaryMetric() != null) {
                conditions.add("e.primaryMetric = :primaryMetric");
            }

            if (criteria.getCreatedFrom() != null) {
                conditions.add("e.createdAt >= :createdFrom");
            }

            if (criteria.getCreatedTo() != null) {
                conditions.add("e.createdAt <= :createdTo");
            }
        }
        return conditions;
    }

}