package com.margot.word_map.repository;

import com.margot.word_map.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
