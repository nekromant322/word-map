package com.margot.word_map.service.word;

import com.margot.word_map.dto.request.DictionaryListRequest;
import com.margot.word_map.model.Word;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class WordSpecs {

    public Specification<Word> hasLanguageId(DictionaryListRequest request) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("language").get("id"), request.getLanguageId());
    }

    public Specification<Word> hasWordLength(DictionaryListRequest request) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("wordLength"), request.getWordLength());
    }

    public Specification<Word> matchingLettersUsed(DictionaryListRequest request) {
        return (root, cq, cb)
                -> {
            String regex = "[" + request.getLettersUsed() + "]";
            return cb.isTrue(
                    cb.function(
                            "texticregexeq",
                            Boolean.class,
                            root.get("word"),
                            cb.literal(regex.toLowerCase())
                    ));
        };
    }

    public Specification<Word> lettersExcluded(DictionaryListRequest request) {
        return (root, cq, cb)
                -> {
            String regex = "[" + request.getLettersExclude() + "]";
            return cb.isFalse(
                    cb.function(
                            "texticregexeq",
                            Boolean.class,
                            root.get("word"),
                            cb.literal(regex.toLowerCase())
                    ));
        };
    }

    public Specification<Word> hasPositions(DictionaryListRequest request) {
        return (root, cq, cb)
                -> {
            Predicate[] predicates = request.getPositions().stream()
                    .map(pos -> cb.equal(
                            cb.lower(cb.substring(root.get("word"), pos.getNumber() + 1, 1)),
                            String.valueOf(pos.getLetter()).toLowerCase()
                    ))
                    .toArray(Predicate[]::new);
            return cb.and(predicates);
        };
    }
}
