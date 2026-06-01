package com.umcsuser.carrent;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.services.AuthServiceInterface;
import com.umcsuser.carrent.services.RentalServiceInterface;
import com.umcsuser.carrent.services.VehicleServiceInterface;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UI {

    private final VehicleServiceInterface vehicleService;
    private final AuthServiceInterface authService;
    private final RentalServiceInterface rentalService;
    private final Scanner scanner;

    public UI(VehicleServiceInterface vehicleService, AuthServiceInterface authService, RentalServiceInterface rentalService) {
        this.vehicleService = vehicleService;
        this.authService = authService;
        this.rentalService = rentalService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\n=== SYSTEM WYPOŻYCZALNI ===");
            displayAvailableVehicles();
            System.out.println("----------------------------------------");
            System.out.println("1. Zaloguj się");
            System.out.println("2. Zarejestruj się");
            System.out.println("0. Wyjdź");
            System.out.print("Wybór: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    handleLogin();
                    break;
                case "2":
                    handleRegistration();
                    break;
                case "0":
                    System.out.println("Do widzenia!");
                    return;
                default:
                    System.out.println("Nieprawidłowy wybór. Spróbuj ponownie.");
            }
        }
    }

    private void displayAvailableVehicles() {
        System.out.println("--- Dostępne pojazdy do wypożyczenia ---");
        List<Vehicle> allVehicles = vehicleService.findAllVehicles();
        boolean anyAvailable = false;

        for (Vehicle v : allVehicles) {
            if (!rentalService.vehicleHasActiveRental(v.getId())) {
                System.out.printf(" - [ID: %s] %s %s [%s] | Cena: %.2f PLN\n",
                        v.getId(), v.getBrand(), v.getModel(), v.getCategory(), v.getPrice());
                anyAvailable = true;
            }
        }

        if (!anyAvailable) {
            System.out.println(" (Obecnie wszystkie pojazdy są wypożyczone)");
        }
    }

    private void handleLogin() {
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Hasło: ");
        String password = scanner.nextLine();

        Optional<User> userOpt = authService.login(login, password);

        if (userOpt.isPresent()) {
            User loggedInUser = userOpt.get();
            System.out.println("Zalogowano pomyślnie! Witaj, " + loggedInUser.getLogin());
            showUserMenu(loggedInUser);
        } else {
            System.out.println("Błędne dane logowania!");
        }
    }

    private void handleRegistration() {
        System.out.print("Podaj nowy login: ");
        String login = scanner.nextLine();
        System.out.print("Podaj hasło: ");
        String password = scanner.nextLine();

        boolean success = authService.register(login, password);
        if (success) {
            System.out.println("Konto zostało utworzone! Możesz się teraz zalogować.");
        } else {
            System.out.println("Taki login już istnieje w bazie!");
        }
    }

    private void showUserMenu(User user) {
        while (true) {
            System.out.println("\n--- MENU UŻYTKOWNIKA (" + user.getRole() + ") ---");
            if (user.getRole() == Role.ADMIN) {
                System.out.println("1. Dodaj nowy pojazd");
                System.out.println("2. Usuń pojazd");
                System.out.println("3. Wyświetl wszystkie wypożyczenia w systemie");
            } else {
                System.out.println("1. Wypożycz pojazd");
                System.out.println("2. Oddaj pojazd");
                System.out.println("3. Historia moich wypożyczeń");
            }
            System.out.println("0. Wyloguj");
            System.out.print("Wybór: ");

            String choice = scanner.nextLine();

            if (choice.equals("0")) {
                System.out.println("Wylogowano.");
                break;
            }

            try {
                if (user.getRole() == Role.ADMIN) {
                    handleAdminChoice(choice);
                } else {
                    handleStandardUserChoice(choice, user);
                }
            } catch (Exception e) {
                System.out.println("Błąd operacji: " + e.getMessage());
            }
        }
    }

    private void handleAdminChoice(String choice) {
        switch (choice) {
            case "1":                System.out.print("ID Pojazdu: ");
                String id = scanner.nextLine();
                System.out.print("Kategoria (np. Car): ");
                String category = scanner.nextLine();
                System.out.print("Marka: ");
                String brand = scanner.nextLine();
                System.out.print("Model: ");
                String model = scanner.nextLine();
                System.out.print("Rok produkcji: ");
                int year = Integer.parseInt(scanner.nextLine());
                System.out.print("Rejestracja: ");
                String plate = scanner.nextLine();
                System.out.print("Cena za dzień: ");
                double price = Double.parseDouble(scanner.nextLine());

                Vehicle v = new Vehicle();
                v.setId(id);
                v.setCategory(category);
                v.setBrand(brand);
                v.setModel(model);
                v.setYear(year);
                v.setPlate(plate);
                v.setPrice(price);

                vehicleService.addVehicle(v);
                System.out.println("Pojazd dodany do bazy!");
                break;
            case "2":
                System.out.print("Podaj ID pojazdu do usunięcia: ");
                String delId = scanner.nextLine();
                vehicleService.deleteVehicle(delId);
                System.out.println("Wydano polecenie usunięcia.");
                break;
            case "3":
                List<Rental> allRentals = rentalService.findAllRentals();
                if (allRentals.isEmpty()) {
                    System.out.println("Brak wypożyczeń w systemie.");
                } else {
                    allRentals.forEach(r -> System.out.println(r.toString()));
                }
                break;
            default:
                System.out.println("Nieznana opcja administratora.");
        }
    }

    private void handleStandardUserChoice(String choice, User user) {
        switch (choice) {
            case "1":
                if (rentalService.userHasActiveRental(user.getId())) {
                    System.out.println("Masz już aktywne wypożyczenie! Zwróć najpierw obecny pojazd.");
                    return;
                }
                displayAvailableVehicles();
                System.out.print("Podaj ID pojazdu, który chcesz wypożyczyć: ");
                String vehicleId = scanner.nextLine();

                rentalService.rentVehicle(user.getId(), vehicleId);
                System.out.println("Pomyślnie wypożyczono pojazd!");
                break;
            case "2":
                rentalService.returnVehicle(user.getId());
                System.out.println("Pojazd został zwrócony. Dziękujemy!");
                break;
            case "3":
                List<Rental> myRentals = rentalService.findUserRentals(user.getId());
                if (myRentals.isEmpty()) {
                    System.out.println("Brak historii wypożyczeń.");
                } else {
                    System.out.println("--- Twoja historia ---");
                    myRentals.forEach(r -> {
                        String status = r.isActive() ? "[W TRAKCIE]" : "[ZWRÓCONY]";
                        System.out.printf("%s Pojazd ID: %s | Data wypożyczenia: %s\n",
                                status, r.getVehicleId(), r.getRentDateTime());
                    });
                }
                break;
            default:
                System.out.println("Nieznana opcja.");
        }
    }
}