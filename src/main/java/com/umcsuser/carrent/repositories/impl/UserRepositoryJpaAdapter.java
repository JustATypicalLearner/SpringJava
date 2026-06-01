package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("jpa")
public class UserRepositoryJpaAdapter {

    private final UserJpaRepository delegate;

    public UserRepositoryJpaAdapter(UserJpaRepository delegate) {
        this.delegate = delegate;
    }

    public List<User> findAll() {
        return delegate.findAll();
    }

    public Optional<User> findById(String id) {
        return delegate.findById(id);
    }

    public Optional<User> findByLogin(String login) {
        return delegate.findByLogin(login);
    }

    public User save(User user) {
        return delegate.save(user);
    }

    public void deleteById(String id) {
        delegate.deleteById(id);
    }
}