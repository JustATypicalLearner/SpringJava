package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.impl.UserRepositoryJpaAdapter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepositoryJpaAdapter userRepository;

    public UserService(UserRepositoryJpaAdapter userRepository) {
        this.userRepository = userRepository;
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));
    }
}
