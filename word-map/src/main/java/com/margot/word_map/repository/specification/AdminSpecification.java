package com.margot.word_map.repository.specification;

import com.margot.word_map.dto.request.AdminSearchRequest;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.enums.Role;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Nulls;
import jakarta.persistence.criteria.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdminSpecification {

    public Specification<Admin> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null) return null;

            Expression<String> emailInLower = cb.lower(root.get("email"));
            return cb.like(emailInLower, "%" + name.toLowerCase() + "%");
        };
    }

    public Specification<Admin> hasRole(String roleValue) {
        return (root, query, cb) ->
                Role.fromString(roleValue)
                        .map(role -> cb.equal(root.get("role"), role))
                        .orElse(null);
    }

    public Specification<Admin> hasLanguage(Long langId) {
        return (root, query, cb) -> {
            if (langId == null) return null;

            Join<Object, Object> adminLanguageJoin = root.join("languages");
            Join<Object, Object> languageJoin = adminLanguageJoin.join("language");

            return cb.equal(languageJoin.get("id"), langId);
        };
    }

    public Specification<Admin> hasAccess(Boolean access) {
        return (root, query, cb) ->
                access == null ? null : cb.equal(root.get("accessGranted"), access);
    }

    public Specification<Admin> orderBy(String sortFieldValue, boolean asc) {
        return (root, query, cb) -> {
            if (query == null) return null;

            List<Order> orders = new ArrayList<>();

            AdminSortField.fromString(sortFieldValue).ifPresent((field) -> {
                String entityField = field.getEntityFieldName();

                if (field == AdminSortField.DATE) {
                    orders.add(cb.desc(root.get("dateActive"), Nulls.LAST));
                } else {
                    orders.add(asc ? cb.asc(root.get(entityField)) : cb.desc(root.get(entityField)));
                }
            });

            orders.add(cb.asc(root.get("id")));
            query.orderBy(orders);

            return null;
        };
    }

    public Specification<Admin> fromRequest(AdminSearchRequest request) {
        return Specification
                .where(hasName(request.getSearch()))
                .and(hasRole(request.getFilterRole()))
                .and(hasLanguage(request.getFilterLanguage()))
                .and(hasAccess(request.getFilterAccess()))
                .and(orderBy(request.getSortingType(), true));
    }
}
