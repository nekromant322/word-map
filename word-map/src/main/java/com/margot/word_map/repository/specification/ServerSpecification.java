package com.margot.word_map.repository.specification;

import com.margot.word_map.dto.request.ServerSortingType;
import com.margot.word_map.model.Server;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.margot.word_map.repository.specification.ServerField.*;

@Component
@RequiredArgsConstructor
public class ServerSpecification {

    private final EntityManager entityManager;
    private static final int PAGE_OFFSET_CORRECTION = 1;

    public Long countServers(Boolean filterStatus, Integer filterPlatform,
                             Integer filterLanguage, String search) {

        CriteriaRoot<Long> cr = createCriteria(Long.class);

        cr.query.select(cr.cb.count(cr.root));
        cr.query.where(buildPredicates(cr.cb, cr.root, filterStatus, filterPlatform, filterLanguage, search));

        return entityManager.createQuery(cr.query).getSingleResult();
    }

    public List<Server> findServers(Boolean filterStatus, Integer filterPlatform,
                                    Integer filterLanguage, String search,
                                    ServerSortingType sortingType, Integer page, Integer size) {

        CriteriaRoot<Server> cr = createCriteria(Server.class);

        cr.root.fetch(PLATFORM_ID.getEntityFieldName(), JoinType.LEFT);
        cr.root.fetch(LANGUAGE_ID.getEntityFieldName(), JoinType.LEFT);
        cr.query.distinct(true);

        cr.query.where(buildPredicates(cr.cb, cr.root, filterStatus, filterPlatform, filterLanguage, search));

        ServerSortingType type = sortingType != null ? sortingType : ServerSortingType.DATE_LATE;
        cr.query.orderBy(type.toOrder(cr.cb, cr.root));

        TypedQuery<Server> typedQuery = entityManager.createQuery(cr.query);
        typedQuery.setFirstResult((page - PAGE_OFFSET_CORRECTION) * size);
        typedQuery.setMaxResults(size);

        return typedQuery.getResultList();
    }

    private Predicate[] buildPredicates(CriteriaBuilder cb, Root<Server> root,
                                        Boolean filterStatus, Integer filterPlatform,
                                        Integer filterLanguage, String search) {
        List<Predicate> predicates = new ArrayList<>();

        if (filterStatus != null) {
            predicates.add(cb.equal(
                    root.get(IS_OPEN.getEntityFieldName()), filterStatus));
        }

        if (filterPlatform != null) {
            predicates.add(cb.equal(
                    root.get(PLATFORM_ID.getEntityFieldName()).get(ID.getEntityFieldName()), filterPlatform));
        }

        if (filterLanguage != null) {
            predicates.add(cb.equal
                    (root.get(LANGUAGE_ID.getEntityFieldName()).get(ID.getEntityFieldName()), filterLanguage));
        }

        if (search != null && !search.isBlank()) {
            predicates.add(cb.like(cb.lower(
                    root.get(NAME.getEntityFieldName())), "%" + search.toLowerCase() + "%"));
        }

        return predicates.toArray(Predicate[]::new);
    }

    @RequiredArgsConstructor
    private static class CriteriaRoot<T> {
        private final CriteriaBuilder cb;
        private final CriteriaQuery<T> query;
        private final Root<Server> root;
    }

    private <T> CriteriaRoot<T> createCriteria(Class<T> resultClass) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(resultClass);
        Root<Server> root = query.from(Server.class);
        return new CriteriaRoot<>(cb, query, root);
    }
}