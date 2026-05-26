package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalRepository;

import java.util.List;

public class RentalService {

    private final RentalRepository rentalRepository;
    private final VehicleService vehicleService;

    public RentalService(RentalRepository rentalRepository, VehicleService vehicleService) {
        this.rentalRepository = rentalRepository;
        this.vehicleService = vehicleService;
    }

    public boolean rentVehicle(String login, String vehicleId) {
        if (vehicleService.findVehicleById(vehicleId).isEmpty()) {
            System.out.println("BŁĄD: Pojazd o ID '" + vehicleId + "' nie istnieje w naszej flocie!");
            return false;
        }

        if (!rentalRepository.getRentalsByUser(login).isEmpty()) {
            System.out.println("BŁĄD: Masz już wypożyczony pojazd! Najpierw go zwróć.");
            return false;
        }

        if (rentalRepository.isRented(vehicleId)) {
            System.out.println("BŁĄD: Pojazd o ID '" + vehicleId + "' jest obecnie niedostępny.");
            return false;
        }

        rentalRepository.addRental(new Rental(login, vehicleId));
        System.out.println("Pojazd został pomyślnie wypożyczony.");
        return true;
    }

    public boolean returnVehicle(String login) {
        List<Rental> myRentals = rentalRepository.getRentalsByUser(login);

        if (myRentals.isEmpty()) {
            System.out.println("BŁĄD: Nie masz żadnego aktywnego wypożyczenia do zwrotu.");
            return false;
        }

        String vehicleId = myRentals.get(0).getVehicleId();

        if (rentalRepository.removeRentalByVehicleId(vehicleId)) {
            System.out.println("Pojazd o ID '" + vehicleId + "' został zwrócony.");
            return true;
        }

        System.out.println("Wystąpił błąd podczas próby zwrotu pojazdu.");
        return false;
    }

    public boolean isRented(String vehicleId) {
        return rentalRepository.isRented(vehicleId);
    }

    public List<Rental> getRentalsByUser(String login) {
        return rentalRepository.getRentalsByUser(login);
    }
}