package com.umcsuser.carrent;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.models.VehicleCategoryConfig;
import com.umcsuser.carrent.services.*;

import java.util.List;
import java.util.Scanner;

public class UI {

    private final VehicleCategoryConfigService configService;
    private final VehicleService vehicleService;
    private final UserService userService;
    private final AuthService authService;
    private final RentalService rentalService;

    private final Scanner scanner = new Scanner(System.in);
    private User currentUser = null;

    public UI(VehicleCategoryConfigService configService,
              VehicleService vehicleService,
              UserService userService,
              AuthService authService,
              RentalService rentalService) {
        this.configService = configService;
        this.vehicleService = vehicleService;
        this.userService = userService;
        this.authService = authService;
        this.rentalService = rentalService;
    }

    public void start() {
        while (currentUser == null) {
            System.out.println("\n=== SYSTEM WYPOŻYCZALNI ===");
            System.out.println("--- Dostępne pojazdy do wypożyczenia ---");
            boolean anyAvailable = false;
            for (Vehicle v : vehicleService.findAllVehicles()) {
                if (!rentalService.isRented(v.getId())) {
                    System.out.println(" - [ID: " + v.getId() + "] " + v.getBrand() + " " + v.getModel() + " [" + v.getCategory() + "] | Cena: " + v.getPrice() + " PLN");
                    anyAvailable = true;
                }
            }
            if (!anyAvailable) {
                System.out.println(" (Obecnie wszystkie pojazdy są wypożyczone)");
            }
            System.out.println("----------------------------------------");

            System.out.println("1. Zaloguj się");
            System.out.println("2. Zarejestruj się");
            System.out.println("0. Wyjdź");
            System.out.print("Wybór: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                System.out.print("Login: "); String login = scanner.nextLine().trim();
                System.out.print("Hasło: "); String password = scanner.nextLine().trim();
                currentUser = authService.login(login, password);
                if (currentUser == null) System.out.println("Błędne dane!\n");
            } else if (choice.equals("2")) {
                System.out.print("Nowy login: "); String login = scanner.nextLine().trim();
                System.out.print("Nowe hasło: "); String password = scanner.nextLine().trim();
                if (authService.register(login, password)) System.out.println("Zarejestrowano!\n");
                else System.out.println("Login zajęty!\n");
            } else if (choice.equals("0")) {
                System.exit(0);
            } else {
                System.out.println("Nieznana opcja!");
            }
        }

        System.out.println("\nWitaj, " + currentUser.getLogin() + "!");
        if (currentUser.getRole() == Role.ADMIN) adminMenu();
        else userMenu();
    }

    private void adminMenu() {
        while (true) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. Dodaj pojazd");
            System.out.println("2. Usuń pojazd");
            System.out.println("3. Pokaż wszystkie pojazdy");
            System.out.println("4. Usuń użytkownika");
            System.out.println("0. Wyloguj");
            System.out.print("Wybór: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addVehicleInteractive();
                case "2" -> {
                    System.out.print("Podaj ID pojazdu do usunięcia: ");
                    vehicleService.deleteVehicle(scanner.nextLine().trim());
                }
                case "3" -> vehicleService.findAllVehicles().forEach(v -> System.out.println("ID: " + v.getId() + " | " + v.getBrand() + " " + v.getModel() + " (" + v.getCategory() + ")"));
                case "4" -> {
                    System.out.print("Podaj login usera do usunięcia: ");
                    userService.deleteUser(scanner.nextLine().trim());
                }
                case "0" -> { currentUser = null; start(); return; }
                default -> System.out.println("Nieznana opcja.");
            }
        }
    }

    private void userMenu() {
        while (true) {
            System.out.println("\n--- USER MENU ---");
            System.out.println("1. Zobacz dostępne pojazdy");
            System.out.println("2. Wyświetl mój wypożyczony pojazd");
            System.out.println("3. Wypożycz pojazd");
            System.out.println("4. Zwróć pojazd");
            System.out.println("0. Wyloguj");
            System.out.print("Wybór: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showAvailableVehicles();
                case "2" -> showMyRentedVehicle();
                case "3" -> rentVehicle();
                case "4" -> returnVehicle();
                case "0" -> { currentUser = null; start(); return; }
                default -> System.out.println("Nieznana opcja.");
            }
        }
    }

    private void showAvailableVehicles() {
        System.out.println("\n--- POJAZDY DOSTĘPNE DO WYPOŻYCZENIA ---");
        boolean any = false;
        for (Vehicle v : vehicleService.findAllVehicles()) {
            if (!rentalService.isRented(v.getId())) {
                System.out.println("ID: " + v.getId() + " | " + v.getBrand() + " " + v.getModel() + " [" + v.getCategory() + "] | Cena: " + v.getPrice() + " PLN/doba");
                any = true;
            }
        }
        if (!any) {
            System.out.println("Niestety, aktualnie brak dostępnych pojazdów w bazie.");
        }
    }

    private void showMyRentedVehicle() {
        System.out.println("\n--- TWÓJ AKTUALNIE WYPOŻYCZONY POJAZD ---");
        List<Rental> myRentals = rentalService.getRentalsByUser(currentUser.getLogin());

        if (myRentals.isEmpty()) {
            System.out.println("Nie posiadasz obecnie żadnych aktywnych wypożyczeń.");
            return;
        }

        String rentedVehicleId = myRentals.get(0).getVehicleId();

        vehicleService.findVehicleById(rentedVehicleId).ifPresentOrElse(
                vehicle -> {
                    System.out.println("ID Pojazdu: " + vehicle.getId());
                    System.out.println("Kategoria : " + vehicle.getCategory());
                    System.out.println("Model     : " + vehicle.getBrand() + " " + vehicle.getModel());
                    System.out.println("Rejestracja: " + vehicle.getPlate());
                    System.out.println("Cena za dobę: " + vehicle.getPrice() + " PLN");

                    if (!vehicle.getAttributes().isEmpty()) {
                        System.out.println("Specyfikacja:");
                        vehicle.getAttributes().forEach((cecha, wartosc) ->
                                System.out.println("  - " + cecha + ": " + wartosc)
                        );
                    }
                },
                () -> System.out.println("Błąd: Wypożyczyłeś pojazd o ID " + rentedVehicleId + ", ale nie ma go w bazie danych floty.")
        );
    }

    private void addVehicleInteractive() {
        System.out.println("Dostępne kategorie: ");
        configService.findAllCategories().forEach(c -> System.out.println("- " + c.getCategory()));

        try {
            System.out.print("Podaj kategorię: ");
            VehicleCategoryConfig config = configService.getByCategory(scanner.nextLine().trim());

            System.out.print("Marka: "); String brand = scanner.nextLine().trim();
            System.out.print("Model: "); String model = scanner.nextLine().trim();
            System.out.print("Rok: "); int year = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Rejestracja: "); String plate = scanner.nextLine().trim();
            System.out.print("Cena: "); double price = Double.parseDouble(scanner.nextLine().trim());

            Vehicle vehicle = Vehicle.builder()
                    .category(config.getCategory())
                    .brand(brand).model(model).year(year).plate(plate).price(price)
                    .build();

            config.getAttributes().forEach((attrName, attrType) -> {
                System.out.print("Podaj " + attrName + " (" + attrType + "): ");
                String raw = scanner.nextLine().trim();
                Object val = switch (attrType.toLowerCase()) {
                    case "string" -> raw;
                    case "integer" -> Integer.parseInt(raw);
                    case "number" -> Double.parseDouble(raw);
                    case "boolean" -> Boolean.parseBoolean(raw);
                    default -> raw;
                };
                vehicle.addAttribute(attrName, val);
            });

            vehicleService.addVehicle(vehicle);
            System.out.println("Dodano pojazd do bazy!");
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void rentVehicle() {
        System.out.print("Podaj ID pojazdu do wypożyczenia: ");
        String id = scanner.nextLine().trim();
        rentalService.rentVehicle(currentUser.getLogin(), id);
    }

    private void returnVehicle() {
        rentalService.returnVehicle(currentUser.getLogin());
    }
}