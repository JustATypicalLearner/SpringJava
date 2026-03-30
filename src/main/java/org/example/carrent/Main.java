package org.example.carrent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        ensureVehiclesFile();

        UserRepository userRepo = new UserRepository();
        VehicleRepositoryImpl vehicleRepo = new VehicleRepositoryImpl();
        Authentication auth = new Authentication(userRepo);

        User currentUser = null;
        while (currentUser == null) {
            System.out.print("Login: ");
            String login = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            currentUser = auth.login(login, password);
            if (currentUser == null) {
                System.out.println("Invalid credentials. Try again.\n");
            }
        }

        System.out.println("\nWelcome, " + currentUser.getLogin() +
                           " [" + currentUser.getRole() + "]\n");

        if (currentUser.getRole() == Role.ADMIN) {
            adminMenu(currentUser, vehicleRepo, userRepo);
        } else {
            userMenu(currentUser, vehicleRepo, userRepo);
        }
    }

    private static void userMenu(User user,
                                 VehicleRepositoryImpl vehicleRepo,
                                 UserRepository userRepo) throws IOException {
        boolean running = true;
        while (running) {
            System.out.println("""
                    ---- USER MENU ----
                    1. Rent a vehicle
                    2. Return a vehicle
                    3. My profile
                    0. Exit
                    """);
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> rentVehicle(user, vehicleRepo, userRepo);
                case "2" -> returnVehicle(user, vehicleRepo, userRepo);
                case "3" -> showUserProfile(user, vehicleRepo);
                case "0" -> running = false;
                default  -> System.out.println("Unknown option.\n");
            }
        }
    }

    private static void rentVehicle(User user,
                                    VehicleRepositoryImpl vehicleRepo,
                                    UserRepository userRepo) throws IOException {
        if (user.getRentedVehicleId() != null) {
            System.out.println("You already have a rented vehicle: " +
                               user.getRentedVehicleId() + "\n");
            return;
        }

        List<Vehicle> available = vehicleRepo.getVehicles().stream()
                .filter(v -> !v.isRented()).toList();

        if (available.isEmpty()) {
            System.out.println("No vehicles available.\n");
            return;
        }

        System.out.println("Available vehicles:");
        for (Vehicle v : available) {
            System.out.println("  " + v);
        }

        System.out.print("Enter vehicle ID to rent: ");
        String id = scanner.nextLine().trim();

        if (vehicleRepo.getVehicle(id) == null) {
            System.out.println("Vehicle not found.\n");
            return;
        }

        boolean success = vehicleRepo.rentVehicle(id);
        if (success) {
            user.setRentedVehicleId(id);
            userRepo.update(user);
            vehicleRepo.save(vehicleRepo.getVehicles());
            System.out.println("Vehicle " + id + " rented successfully.\n");
        } else {
            System.out.println("Vehicle is already rented or not found.\n");
        }
    }

    private static void returnVehicle(User user,
                                      VehicleRepositoryImpl vehicleRepo,
                                      UserRepository userRepo) throws IOException {
        if (user.getRentedVehicleId() == null) {
            System.out.println("You don't have any rented vehicle.\n");
            return;
        }

        String rentedVehicleId = user.getRentedVehicleId();
        
        if (vehicleRepo.getVehicle(rentedVehicleId) == null) {
            System.out.println("Error: rented vehicle not found in repository.\n");
            return;
        }

        boolean success = vehicleRepo.returnVehicle(rentedVehicleId);
        if (success) {
            user.setRentedVehicleId(null);
            userRepo.update(user);
            vehicleRepo.save(vehicleRepo.getVehicles());
            System.out.println("Vehicle returned successfully.\n");
        } else {
            System.out.println("Vehicle was not rented or not found.\n");
        }
    }

    private static void showUserProfile(User user,
                                        VehicleRepositoryImpl vehicleRepo) {
        System.out.println("---- My Profile ----");
        System.out.println("Login : " + user.getLogin());
        System.out.println("Role  : " + user.getRole());

        if (user.getRentedVehicleId() != null) {
            Vehicle v = vehicleRepo.getVehicle(user.getRentedVehicleId());
            System.out.println("Rented: " + (v != null ? v : "Error: Rented vehicle details not found. ID: " + user.getRentedVehicleId()));
        } else {
            System.out.println("Rented: (none)");
        }
        System.out.println();
    }

    private static void adminMenu(User admin,
                                  VehicleRepositoryImpl vehicleRepo,
                                  UserRepository userRepo) throws IOException {
        boolean running = true;
        while (running) {
            System.out.println("""
                    ---- ADMIN MENU ----
                    1. List all vehicles
                    2. Add vehicle
                    3. Remove vehicle
                    4. List users with rented vehicles
                    0. Exit
                    """);
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> listVehicles(vehicleRepo);
                case "2" -> addVehicle(vehicleRepo);
                case "3" -> removeVehicle(vehicleRepo);
                case "4" -> listUsersWithVehicles(userRepo, vehicleRepo);
                case "0" -> running = false;
                default  -> System.out.println("Unknown option.\n");
            }
        }
    }

    private static void listVehicles(VehicleRepositoryImpl vehicleRepo) {
        List<Vehicle> list = vehicleRepo.getVehicles();
        if (list.isEmpty()) {
            System.out.println("No vehicles.\n");
            return;
        }
        System.out.println("---- Vehicles ----");
        list.forEach(v -> System.out.println("  " + v));
        System.out.println();
    }

    private static void addVehicle(VehicleRepositoryImpl vehicleRepo) throws IOException {
        System.out.print("Type (CAR/MOTORCYCLE): ");
        String type = scanner.nextLine().trim().toUpperCase();

        System.out.print("ID: ");     String id    = scanner.nextLine().trim();
        System.out.print("Brand: ");  String brand  = scanner.nextLine().trim();
        System.out.print("Model: ");  String model  = scanner.nextLine().trim();
        System.out.print("Year: ");   int year      = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Price: ");  double price  = Double.parseDouble(scanner.nextLine().trim());

        Vehicle v;
        if (type.equals("CAR")) {
            v = new Car(id, brand, model, year, price, false);
        } else if (type.equals("MOTORCYCLE")) {
            System.out.print("Driving licence category (AM/A1/A2/B/A): ");
            String licence = scanner.nextLine().trim();
            v = new Motorcycle(id, brand, model, year, price, false, licence);
        } else {
            System.out.println("Unknown vehicle type.\n");
            return;
        }

        if (vehicleRepo.add(v)) {
            vehicleRepo.save(vehicleRepo.getVehicles());
            System.out.println("Vehicle added.\n");
        } else {
            System.out.println("Vehicle with ID " + id + " already exists.\n");
        }
    }

    private static void removeVehicle(VehicleRepositoryImpl vehicleRepo) throws IOException {
        System.out.print("Enter vehicle ID to remove: ");
        String id = scanner.nextLine().trim();

        if (vehicleRepo.remove(id)) {
            vehicleRepo.save(vehicleRepo.getVehicles());
            System.out.println("Vehicle removed.\n");
        } else {
            System.out.println("Vehicle not found.\n");
        }
    }

    private static void listUsersWithVehicles(UserRepository userRepo,
                                              VehicleRepositoryImpl vehicleRepo) {
        System.out.println("---- Users ----");
        for (User u : userRepo.getUsers()) {
            System.out.print("  " + u.getLogin() + " [" + u.getRole() + "]");
            if (u.getRentedVehicleId() != null) {
                Vehicle v = vehicleRepo.getVehicle(u.getRentedVehicleId());
                System.out.print(" → rented: " + (v != null ? v : "Error: Rented vehicle details not found. ID: " + u.getRentedVehicleId()));
            } else {
                System.out.print(" → no vehicle rented");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void ensureVehiclesFile() throws IOException {
        Path p = Path.of("vehicles.csv");
        if (!Files.exists(p)) {
            Files.writeString(p,
                "TYPE;ID;BRAND;MODEL;YEAR;PRICE;RENTED[;LICENCE]\n" +
                "CAR;1;Toyota;Corolla;2020;150.0;false\n" +
                "CAR;2;BMW;320i;2022;250.0;false\n" +
                "MOTORCYCLE;3;Honda;CBR500;2021;120.0;false;A2\n");
            System.out.println("Created default vehicles.csv");
        }
    }
}
