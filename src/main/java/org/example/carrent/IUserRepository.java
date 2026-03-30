package org.example.carrent;

import java.io.IOException;
import java.util.List;

public interface IUserRepository {
    User getUser(String login);
    List<User> getUsers();
    void save() throws IOException;
    void load();
    void update(User user);
}
