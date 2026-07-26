package com.margot.word_map.repository.specification;

import com.margot.word_map.model.Server;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


import static com.margot.word_map.repository.specification.ServerField.*;

@Component
public class ServerSpecification {

    public Specification<Server> filter(
            Boolean filterStatus, Integer filterPlatform,
            Integer filterLanguage, String search) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filterStatus != null) {
                predicates.add(
                        cb.equal(root.get(IS_OPEN.getEntityFieldName()), filterStatus));
            }
            if (filterPlatform != null) {
                predicates.add(
                        cb.equal(root.get(PLATFORM_ID.getEntityFieldName())
                                .get(ID.getEntityFieldName()), filterPlatform));
            }
            if (filterLanguage != null) {
                predicates.add(
                        cb.equal(root.get(LANGUAGE_ID.getEntityFieldName())
                                .get(ID.getEntityFieldName()), filterLanguage));
            }
            if (search != null && !search.isBlank()) {
                predicates.add(
                        cb.like(cb.lower(root.get(NAME.getEntityFieldName())),
                                "%" + search.toLowerCase() + "%"));
            }
            if (!Long.class.equals(query.getResultType())) {
                root.fetch(PLATFORM_ID.getEntityFieldName(), JoinType.LEFT);
                root.fetch(LANGUAGE_ID.getEntityFieldName(), JoinType.LEFT);
                query.distinct(true);
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}