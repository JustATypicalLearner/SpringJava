package org.example.carrent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTest {

    private Authentication authentication;
    private IUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        authentication = new Authentication(userRepository);
    }

    @Test
    void successfulLogin() {
        User user = authentication.login("user", "user123");
        assertNotNull(user);
        assertEquals("user", user.getLogin());
    }

    @Test
    void failedLogin_wrongPassword() {
        User user = authentication.login("user", "wrongpassword");
        assertNull(user);
    }

    @Test
    void failedLogin_nonexistentUser() {
        User user = authentication.login("nonexistent", "password");
        assertNull(user);
    }
}
