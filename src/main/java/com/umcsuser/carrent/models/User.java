package com.umcsuser.carrent.models;
import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String login;
    private String passwordHash;
    private Role role;

    public User(String login, String passwordHash, Role role) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getLogin() { return login; }
    public String getPassword() { return passwordHash; }
    public Role getRole() { return role; }

    public User copy() {
        return new User(this.login, this.passwordHash, this.role);
    }
}