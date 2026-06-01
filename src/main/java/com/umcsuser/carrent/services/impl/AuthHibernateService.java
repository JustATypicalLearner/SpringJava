package com.umcsuser.carrent.services.impl;

import com.umcsuser.carrent.db.HibernateConfig;
import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.impl.UserHibernateRepository;
import com.umcsuser.carrent.services.AuthServiceInterface;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.UUID;

public class AuthHibernateService implements AuthServiceInterface {

    private final UserHibernateRepository userRepo;

    public AuthHibernateService(UserHibernateRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public Optional<User> login(String login, String rawPassword) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            userRepo.setSession(session);
            Optional<User> userOpt = userRepo.findByLogin(login);
            if (userOpt.isPresent() && BCrypt.checkpw(rawPassword, userOpt.get().getPasswordHash())) {
                return userOpt;
            }
            return Optional.empty();
        }
    }

    @Override
    public boolean register(String login, String rawPassword) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            userRepo.setSession(session);            if (userRepo.findByLogin(login).isPresent()) {
                return false;
            }            String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
            User newUser = new User(UUID.randomUUID().toString(), login, hashedPassword, Role.USER);

            userRepo.save(newUser);
            tx.commit();            return true;
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }
}