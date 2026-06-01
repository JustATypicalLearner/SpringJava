package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;
import java.util.Optional;

public class UserHibernateRepository {

    private Session session;

    public void setSession(Session session) {
        this.session = session;
    }

    public User save(User user) {
        return session.merge(user);
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(session.get(User.class, id));
    }

    public Optional<User> findByLogin(String login) {        Query<User> query = session.createQuery("FROM User u WHERE u.login = :login", User.class);
        query.setParameter("login", login);        return query.uniqueResultOptional();    }

    public List<User> findAll() {
        return session.createQuery("FROM User", User.class).list();    }
}