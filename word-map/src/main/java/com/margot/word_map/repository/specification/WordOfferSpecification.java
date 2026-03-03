package com.margot.word_map.repository.specification;

import com.margot.word_map.dto.request.SortingType;
import com.margot.word_map.dto.WordOfferPage;
import com.margot.word_map.model.WordOffer;
import com.margot.word_map.model.WordOfferStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WordOfferSpecification {

    private final EntityManager em;

    public Page<WordOfferPage> findAll(Long languageId,
                                       String search,
                                       WordOfferStatus status,
                                       SortingType sortingType,
                                       Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WordOfferPage> cq = cb.createQuery(WordOfferPage.class);
        Root<WordOffer> root = cq.from(WordOffer.class);

        List<Predicate> predicates = getPredicates(cb, root, languageId, search, status);

        cq.multiselect(
                root.get("word"),
                cb.greatest(root.<LocalDateTime>get("createdAt")),
                root.get("status"),
                cb.count(root)
        );

        cq.where(predicates.toArray(new Predicate[0]));
        cq.groupBy(root.get("word"), root.get("status"));
        applySorting(cq, cb, root, sortingType);

        List<WordOfferPage> responses = em.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
        long total = countTotalGroups(languageId, search, status);
        return new PageImpl<>(responses, pageable, total);
    }

    private long countTotalGroups(Long languageId, String search, WordOfferStatus status) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<WordOffer> root = countQuery.from(WordOffer.class);

        countQuery.where(getPredicates(cb, root, languageId, search, status).toArray(new Predicate[0]));

        countQuery.select(cb.countDistinct(root.get("word")));

        return em.createQuery(countQuery).getSingleResult();
    }

    private List<Predicate> getPredicates(CriteriaBuilder cb,
                                          Root<WordOffer> root,
                                          Long languageId,
                                          String search,
                                          WordOfferStatus status) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("languageId"), languageId));
        if (search != null && !search.isBlank()) {
            predicates.add(cb.like(root.get("word"), "%" + search + "%"));
        }
        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }
        return predicates;
    }

    private void applySorting(CriteriaQuery<?> cq, CriteriaBuilder cb, Root<WordOffer> root, SortingType sortingType) {
        if (sortingType == null) {
            cq.orderBy(cb.asc(root.get("word")));
            return;
        }
        Order order = switch (sortingType) {
            case DATE_LATE -> cb.desc(cb.greatest(root.<LocalDateTime>get("createdAt")));
            case DATE_EARLY -> cb.asc(cb.greatest(root.<LocalDateTime>get("createdAt")));
            case RATING_HIGH -> cb.desc(cb.count(root));
            case RATING_LOW -> cb.asc(cb.count(root));
        };

        cq.orderBy(order, cb.asc(root.get("word")));
    }
}
