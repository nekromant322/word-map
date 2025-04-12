package com.margot.word_map.repository;

import com.margot.word_map.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RuleRepository extends JpaRepository<Rule, Long> {

    Optional<Rule> findRuleByAuthority(Rule.RULE rule);

}
