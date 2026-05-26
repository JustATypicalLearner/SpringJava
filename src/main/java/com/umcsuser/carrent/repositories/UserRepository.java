package com.umcsuser.carrent.repositories;

import com.umcsuser.carrent.models.User;

import java.util.List;

public interface UserRepository {
    User getUser(String login);
    List<User> getUsers();
    void update(User user);
    boolean addUser(String login, String password);
    boolean removeUser(String login);
}