package org.valdon.abtests.repository.apiKey.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Repository;
import org.valdon.abtests.controller.apiKey.params.ApiKeySearchCriteria;
import org.valdon.abtests.dto.apiKey.ApiKeyResponse;
import org.valdon.abtests.repository.apiKey.ApiKeyCustomRepository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ApiKeyCustomRepositoryImpl implements ApiKeyCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ApiKeyResponse> findByFilter(Long projectId, ApiKeySearchCriteria criteria) {
        if (projectId == null) {
            throw new IllegalArgumentException("project id is null");
        }

        StringBuilder jpql = new StringBuilder("""
                SELECT new org.valdon.abtests.dto.apiKey.ApiKeyResponse(
                    a.id,
                    a.name,
                    a.keyPrefix,
                    a.status,
                    a.createdAt
                )
                FROM ApiKey a
                WHERE a.project.id = :projectId
                """);

        List<String> conditions = getStrings(criteria);

        for (String condition : conditions) {
            jpql.append(" and ").append(condition);
        }

        jpql.append(" order by a.createdAt desc");

        TypedQuery<ApiKeyResponse> query =
                entityManager.createQuery(jpql.toString(), ApiKeyResponse.class);

        query.setParameter("projectId", projectId);
        setParams(query, criteria);

        return query.getResultList();
    }

    private void setParams(TypedQuery<ApiKeyResponse> query, ApiKeySearchCriteria criteria) {
        if (criteria != null) {
            if (criteria.getName() != null && !criteria.getName().isBlank()) {
                query.setParameter("name", "%" + criteria.getName().trim() + "%");
            }
            if (criteria.getKeyPrefix() != null && !criteria.getKeyPrefix().isBlank()) {
                query.setParameter("keyPrefix", "%" + criteria.getKeyPrefix().trim() + "%");
            }
            if (criteria.getStatus() != null) {
                query.setParameter("status", criteria.getStatus());
            }
            if (criteria.getCreatedFrom() != null) {
                query.setParameter("createdFrom", criteria.getCreatedFrom());
            }
            if (criteria.getCreatedTo() != null) {
                query.setParameter("createdTo", criteria.getCreatedTo());
            }
        }
    }

    private static @NonNull List<String> getStrings(ApiKeySearchCriteria criteria){
        List<String> conditions = new ArrayList<>();

        if (criteria != null) {
            if (criteria.getName() != null && !criteria.getName().isBlank()) {
                conditions.add("lower(a.name) like lower(:name)");
            }
            if (criteria.getKeyPrefix() != null && !criteria.getKeyPrefix().isBlank()) {
                conditions.add("lower(a.keyPrefix) like lower(:keyPrefix)");
            }
            if (criteria.getStatus() != null) {
                conditions.add("a.status = :status");
            }
            if (criteria.getCreatedFrom() != null) {
                conditions.add("a.createdAt >= :createdFrom");
            }
            if (criteria.getCreatedTo() != null) {
                conditions.add("a.createdAt <= :createdTo");
            }
        }
        return conditions;
    }

}