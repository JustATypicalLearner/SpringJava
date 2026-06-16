package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.impl.UserRepositoryJpaAdapter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepositoryJpaAdapter userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepositoryJpaAdapter userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));
    }

    @Transactional
    public User register(String login, String rawPassword, String roleName) {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new IllegalStateException("Użytkownik o podanym loginie już istnieje.");
        }
        Role role = resolveRole(roleName);
        String passwordHash = passwordEncoder.encode(rawPassword);
        User user = new User(UUID.randomUUID().toString(), login, passwordHash, role);
        return userRepository.save(user);
    }

    @Transactional
    public boolean deleteByLogin(String login) {
        return userRepository.findByLogin(login)
                .map(user -> {
                    userRepository.deleteById(user.getId());
                    return true;
                })
                .orElse(false);
    }

    private Role resolveRole(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            return Role.USER;
        }
        try {
            return Role.valueOf(roleName.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Nieprawidłowa rola: " + roleName);
        }
    }
}
