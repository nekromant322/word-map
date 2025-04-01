package com.margot.word_map.service.users_service;

import com.margot.word_map.model.User;

public interface UsersService {

    User findById(Long id);

    User findByEmail(String email);

    void save(User user);

    void update(Long id, User updatedPerson);

    void delete(Long id);
}
