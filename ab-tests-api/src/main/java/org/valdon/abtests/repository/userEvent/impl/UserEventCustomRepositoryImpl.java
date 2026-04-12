package org.valdon.abtests.repository.userEvent.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Repository;
import org.valdon.abtests.controller.userEvent.params.EventSearchCriteria;
import org.valdon.abtests.dto.userEvent.EventTypeCountResponse;
import org.valdon.abtests.dto.userEvent.UserEventPageResponse;
import org.valdon.abtests.dto.userEvent.UserEventResponse;
import org.valdon.abtests.repository.userEvent.UserEventCustomRepository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserEventCustomRepositoryImpl implements UserEventCustomRepository {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 50;
    private static final int MAX_SIZE = 100;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserEventPageResponse findByFilter(Long projectId, EventSearchCriteria criteria) {
        if (projectId == null) {
            throw new IllegalArgumentException("project id is null");
        }

        int page = normalizePage(criteria);
        int size = normalizeSize(criteria);

        StringBuilder selectJpql = new StringBuilder("""
                SELECT new org.valdon.abtests.dto.userEvent.UserEventResponse(
                    e.id,
                    exp.name,
                    exp.externalKey,
                    v.name,
                    e.externalUserId,
                    e.contentId,
                    e.eventType,
                    e.createdAt
                )
                FROM UserEvent e
                JOIN e.experiment exp
                JOIN e.variant v
                WHERE exp.project.id = :projectId
                """);

        StringBuilder countJpql = new StringBuilder("""
                SELECT COUNT(e)
                FROM UserEvent e
                JOIN e.experiment exp
                JOIN e.variant v
                WHERE exp.project.id = :projectId
                """);

        List<String> conditions = buildConditions(criteria);

        for (String condition : conditions) {
            selectJpql.append(" AND ").append(condition);
            countJpql.append(" AND ").append(condition);
        }

        selectJpql.append(" ORDER BY e.createdAt DESC");

        TypedQuery<UserEventResponse> contentQuery = entityManager.createQuery(
                selectJpql.toString(),
                UserEventResponse.class
        );
        setParams(contentQuery, projectId, criteria);

        contentQuery.setFirstResult(page * size);
        contentQuery.setMaxResults(size);

        TypedQuery<Long> totalQuery = entityManager.createQuery(
                countJpql.toString(),
                Long.class
        );
        setParams(totalQuery, projectId, criteria);

        List<UserEventResponse> content = contentQuery.getResultList();
        long totalElements = totalQuery.getSingleResult();
        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / size);

        return new UserEventPageResponse(
                content,
                page,
                size,
                totalElements,
                totalPages,
                page == 0,
                totalPages == 0 || page >= totalPages - 1
        );
    }

    @Override
    public List<EventTypeCountResponse> countByFilter(Long projectId, EventSearchCriteria criteria){
        if (projectId == null) {
            throw new IllegalArgumentException("project id is null");
        }

        StringBuilder jpql = new StringBuilder("""
                SELECT new org.valdon.abtests.dto.userEvent.EventTypeCountResponse(
                    e.eventType,
                    COUNT(e)
                )
                FROM UserEvent e
                JOIN e.experiment exp
                WHERE exp.project.id = :projectId
                """);
        List<String> conditions = buildConditions(criteria);

        for (String condition: conditions) {
            jpql.append(" AND ").append(condition);
        }

        jpql.append(" GROUP BY e.eventType");
        TypedQuery<EventTypeCountResponse> query = entityManager.createQuery(
                jpql.toString(),
                EventTypeCountResponse.class
        );
        setParams(query, projectId, criteria);

        return query.getResultList();
    }

    private void setParams(Query query, Long projectId, EventSearchCriteria criteria) {
        query.setParameter("projectId", projectId);

        if (criteria != null) {
            if (criteria.getExternalKey() != null && !criteria.getExternalKey().isBlank()) {
                query.setParameter("externalKey", criteria.getExternalKey().trim());
            }
            if (criteria.getEventType() != null) {
                query.setParameter("eventType", criteria.getEventType());
            }
            if (criteria.getExternalUserId() != null && !criteria.getExternalUserId().isBlank()) {
                query.setParameter("externalUserId", "%" + criteria.getExternalUserId().trim() + "%");
            }
            if (criteria.getCreatedFrom() != null) {
                query.setParameter("createdFrom", criteria.getCreatedFrom());
            }
            if (criteria.getCreatedTo() != null) {
                query.setParameter("createdTo", criteria.getCreatedTo());
            }
        }
    }

    private static @NonNull List<String> buildConditions(EventSearchCriteria criteria){
        List<String> conditions = new ArrayList<>();

        if (criteria != null) {
            if (criteria.getExternalKey() != null && !criteria.getExternalKey().isBlank()) {
                conditions.add("exp.externalKey = :externalKey");
            }
            if (criteria.getEventType() != null) {
                conditions.add("e.eventType = :eventType");
            }
            if (criteria.getExternalUserId() != null && !criteria.getExternalUserId().isBlank()) {
                conditions.add("lower(e.externalUserId) like lower(:externalUserId)");
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

    private int normalizePage(EventSearchCriteria criteria) {
        if (criteria == null || criteria.getPage() == null || criteria.getPage() < 0) {
            return DEFAULT_PAGE;
        }
        return criteria.getPage();
    }

    private int normalizeSize(EventSearchCriteria criteria) {
        if (criteria == null || criteria.getSize() == null || criteria.getSize() <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(criteria.getSize(), MAX_SIZE);
    }

}