package com.umcsuser.carrent.services.impl;

import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.impl.UserHibernateRepository;
import com.umcsuser.carrent.repositories.impl.UserRepositoryJpaAdapter;
import com.umcsuser.carrent.services.AuthServiceInterface;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AuthHibernateService implements AuthServiceInterface {

    private final UserRepositoryJpaAdapter userRepo;

    public AuthHibernateService(UserRepositoryJpaAdapter userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public Optional<User> login(String login, String rawPassword) {
        Optional<User> userOpt = userRepo.findByLogin(login);
        if (userOpt.isPresent() && BCrypt.checkpw(rawPassword, userOpt.get().getPasswordHash())) {
            return userOpt;
        }
        return Optional.empty();
    }

    @Override
    public boolean register(String login, String rawPassword) {
        if (userRepo.findByLogin(login).isPresent()) {
            return false;
        }

        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        User newUser = new User(UUID.randomUUID().toString(), login, hashedPassword, Role.USER);

        userRepo.save(newUser);
        return true;
    }
}