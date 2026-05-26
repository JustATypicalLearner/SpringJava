package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.VehicleRepository;
import com.umcsuser.carrent.repositories.RentalRepository;

import java.util.List;

public class VehicleService {

    private final VehicleValidator vehicleValidator;
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;

    public VehicleService(VehicleValidator vehicleValidator, VehicleRepository vehicleRepository, RentalRepository rentalRepository) {
        this.vehicleValidator = vehicleValidator;
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        vehicleRepository.save(vehicle);
        return vehicle;
    }

    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }

    public java.util.Optional<Vehicle> findVehicleById(String id) {
        return vehicleRepository.findById(id);
    }

    public void deleteVehicle(String id) {
        if (rentalRepository.isRented(id)) {
            System.out.println("BŁĄD: Nie można usunąć pojazdu! Jest aktualnie wypożyczony.");
            return;
        }
        vehicleRepository.deleteById(id);
        System.out.println("Pojazd usunięty.");
    }
}