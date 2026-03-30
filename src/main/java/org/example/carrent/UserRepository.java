package org.example.carrent;

import org.apache.commons.codec.digest.DigestUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserRepository implements IUserRepository {
    private final List<User> users = new ArrayList<>();
    private final String filePath = "C:/Users/Albert/IdeaProjects/SpringJava3/users.json";

    public UserRepository() {
        load();
        if (users.isEmpty()) {
            users.add(new User("user", DigestUtils.sha256Hex("user123"), Role.USER, null));
            users.add(new User("admin", DigestUtils.sha256Hex("admin123"), Role.ADMIN, null));
            try {
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    public void save() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(users);
        }
    }

    @Override
    public void load() {
        File file = new File(filePath);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
                users.clear();
                users.addAll((List<User>) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getLogin().equals(user.getLogin())) {
                users.set(i, user);
                try {
                    save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }
}
