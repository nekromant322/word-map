package com.margot.word_map.service.users_service;

import com.margot.word_map.model.User;
import org.springframework.transaction.annotation.Transactional;

public interface UsersService {

    User findById(Long id);

    User findByEmail(String email);

    @Transactional
    void save(User user);

    @Transactional
    void update(Long id, User updatedPerson);

    @Transactional
    void delete(Long id);
}
