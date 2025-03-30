package com.margot.word_map.service.users_service;

import com.margot.word_map.model.User;
import com.margot.word_map.repository.UsersRepository;
import com.margot.word_map.service.jwt_service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UsersServiceImpl implements UsersService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public User findById(Long id) {
        Optional<User> user = usersRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new  UsernameNotFoundException("User with id=" + id + " not found");
        }
    }

    @Override
    public User findByEmail(String email) {
        Optional<User> user = usersRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new  UsernameNotFoundException("User with email=" + email + " not found");
        }
    }

    @Override
    @Transactional
    public void save(User user) {
        usersRepository.save(user);
    }

    @Override
    @Transactional
    public void update(Long id, User updatedPerson) {
        User userToUpdate = findById(id);
        userToUpdate.setEmail(updatedPerson.getEmail());
        userToUpdate.setAccess(userToUpdate.getAccess());
        usersRepository.save(userToUpdate);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        usersRepository.deleteById(id);
    }

}
