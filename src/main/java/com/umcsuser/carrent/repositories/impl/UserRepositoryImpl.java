package com.umcsuser.carrent.repositories.impl;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.db.JsonFileStorage;
import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserRepositoryImpl implements UserRepository {

    private final JsonFileStorage<User> storage =
            new JsonFileStorage<>("users.json", new TypeToken<List<User>>() {}.getType());
    private final List<User> users;

    public UserRepositoryImpl() {
        this.users = new ArrayList<>(storage.load());

        if (users.isEmpty()) {
            users.add(new User("user", BCrypt.hashpw("user123", BCrypt.gensalt()), Role.USER));
            users.add(new User("admin", BCrypt.hashpw("admin123", BCrypt.gensalt()), Role.ADMIN));
            storage.save(users);
        }
    }

    @Override
    public User getUser(String login) {
        return users.stream()
                .filter(user -> user.getLogin().equalsIgnoreCase(login))
                .findFirst()
                .map(User::copy)
                .orElse(null);
    }

    @Override
    public List<User> getUsers() {
        return users.stream()
                .map(User::copy)
                .collect(Collectors.toList());
    }

    @Override
    public void update(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getLogin().equalsIgnoreCase(user.getLogin())) {
                users.set(i, user);
                storage.save(users);
                return;
            }
        }
    }

    @Override
    public boolean addUser(String login, String passwordHash) {
        if (getUser(login) != null) {
            return false;
        }
        users.add(new User(login, passwordHash, Role.USER));
        storage.save(users);
        return true;
    }

    @Override
    public boolean removeUser(String login) {
        boolean removed = users.removeIf(u -> u.getLogin().equalsIgnoreCase(login));
        if (removed) {
            storage.save(users);
        }
        return removed;
    }
}