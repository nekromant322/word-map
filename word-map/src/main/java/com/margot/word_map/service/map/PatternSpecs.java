package com.margot.word_map.service.map;

import com.margot.word_map.dto.request.PatternSearchRequest;
import com.margot.word_map.dto.request.PatternSortingType;
import com.margot.word_map.model.Pattern;
import com.margot.word_map.model.enums.PatternType;
import jakarta.persistence.criteria.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PatternSpecs {

    public Specification<Pattern> hasWeightBetween(Short min, Short max) {
        return (root, query, cb) -> {
            if (min == null && max == null) {
                return null;
            }

            if (min != null && max != null && min > max) {
                return cb.disjunction();
            }

            if (min != null && max != null) {
                return cb.between(root.get("weight"), min, max);
            } else if (min != null) {
                return cb.ge(root.get("weight"), min);
            } else {
                return cb.le(root.get("weight"), max);
            }
        };
    }

    public Specification<Pattern> hasAnyType(List<PatternType> types) {
        return (root, query, cb) ->
                (types == null || types.isEmpty())
                        ? null
                        : root.get("patternType").in(types);
    }

    public Specification<Pattern> isDraft(Boolean isDraft) {
        return (root, query, cb) ->
                isDraft == null ? null : cb.equal(root.get("isDraft"), isDraft);
    }

    public Specification<Pattern> orderBy(PatternSortingType sortingType) {
        return ((root, query, cb) -> {
            if (query == null) return null;

            List<Order> orders = new ArrayList<>();

            if (sortingType != null)
                switch (sortingType) {
                    case ID_LOW -> orders.add(cb.asc(root.get("id")));
                    case ID_HIGH -> orders.add(cb.desc(root.get("id")));
                    case WEIGHT_LOW -> orders.add(cb.asc(root.get("weight")));
                    case WEIGHT_HIGH -> orders.add(cb.desc(root.get("weight")));
                }

            orders.add(cb.asc(root.get("id")));
            query.orderBy(orders);

            return null;
        });
    }

    public Specification<Pattern> fromRequest(PatternSearchRequest request) {
        return Specification
                .where(hasWeightBetween(request.getFilterWeightMin(), request.getFilterWeightMax()))
                .and(hasAnyType(request.getFilterPatternType()))
                .and(isDraft(request.getFilterIsDraft()))
                .and(orderBy(request.getSortingType()));
    }
}
