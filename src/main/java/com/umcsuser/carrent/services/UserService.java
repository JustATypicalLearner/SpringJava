package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.RentalRepository;

import java.util.List;

public class UserService {

    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    public UserService(UserRepository userRepository, RentalRepository rentalRepository) {
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    public void deleteUser(String login) {
        if (login.equals("admin")) {
            System.out.println("Nie można usunąć głównego administratora.");
            return;
        }
        if (!rentalRepository.getRentalsByUser(login).isEmpty()) {
            System.out.println("BŁĄD: Nie można usunąć użytkownika! Posiada aktywne wypożyczenie.");
            return;
        }
        if (userRepository.removeUser(login)) {
            System.out.println("Użytkownik usunięty.");
        } else {
            System.out.println("Nie znaleziono użytkownika.");
        }
    }
}