package com.margot.word_map.repository;

import com.margot.word_map.dto.request.DictionaryListRequest;
import com.margot.word_map.dto.request.SymbolPosition;
import com.margot.word_map.model.Word;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class WordRepositoryImpl implements WordRepositoryCustom {
    private final EntityManager em;

    @Override
    public List<String> findAllByFilter(DictionaryListRequest request) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Word> root = cq.from(Word.class);
        Predicate filter = WordFilter.builder(cb)
                .add(request.getLanguageId(), id -> cb.equal(root.get("language").get("id"), id))
                .add(request.getWordLength(), length -> cb.equal(root.get("wordLength"), length))
                .add(request.getLettersUsed(), lettersUsed -> {
                    String regex = "^[" + lettersUsed + "]*$";
                    return cb.isTrue(
                            cb.function(
                                    "texticregexeq",
                                    Boolean.class,
                                    root.get("word"),
                                    cb.literal(regex)
                            )
                    );
                })
                .add(request.getLettersExclude(), lettersExclude -> {
                    List<Predicate> predicates = new ArrayList<>();
                    for (char c:lettersExclude.toCharArray()) {
                        predicates.add(
                                cb.notLike(
                                        cb.lower(root.get("word")),
                                        "%" + c + "%"
                                ));
                    }
                    return cb.and(predicates.toArray(new Predicate[0]));
                })
                .add(request.getPositions(), position -> {
                    List<Predicate> predicates = new ArrayList<>();
                    for (SymbolPosition pos : position) {
                        predicates.add(
                                cb.greaterThan(cb.length(root.get("word")), pos.getNumber())
                        );
                        predicates.add(
                                cb.equal(
                                        cb.lower(cb.substring(root.get("word"), pos.getNumber() + 1, 1)),
                                        String.valueOf(pos.getLetter()).toLowerCase()
                                )
                        );
                    }
                    return cb.and(predicates.toArray(new Predicate[0]));
                })
                .buildAnd();
        cq.select(root.get("word")).where(filter);
        return em.createQuery(cq).getResultList();
    }
}
