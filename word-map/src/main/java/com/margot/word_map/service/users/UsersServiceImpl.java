package com.margot.word_map.service.users;

import com.margot.word_map.model.User;
import com.margot.word_map.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;

    @Override
    public User findById(Long id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with id=" + id + " not found"));
    }

    @Override
    public User findByEmail(String email) {
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email=" + email + " not found"));
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
