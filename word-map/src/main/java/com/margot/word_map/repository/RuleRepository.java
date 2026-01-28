package com.margot.word_map.repository;

import com.margot.word_map.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RuleRepository extends JpaRepository<Rule, Long> {

    @Query("SELECT r from Rule r where r.id in :ids")
    Set<Rule> findAllByIds(List<Long> ids);

    Optional<Rule> findRuleByName(Rule.RULE rule);
}