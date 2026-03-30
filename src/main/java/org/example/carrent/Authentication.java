package org.example.carrent;

import org.apache.commons.codec.digest.DigestUtils;

public class Authentication {
    private final IUserRepository userRepository;

    public Authentication(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String login, String password) {
        User user = userRepository.getUser(login);
        if (user != null && user.getPassword().equals(DigestUtils.sha256Hex(password))) {
            return user;
        }
        return null;
    }
}
