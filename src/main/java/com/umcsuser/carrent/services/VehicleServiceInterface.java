package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Vehicle;
import java.util.List;
import java.util.Optional;

public interface VehicleServiceInterface {
    List<Vehicle> findAllVehicles();
    Optional<Vehicle> findVehicleById(String id);
    Vehicle addVehicle(Vehicle vehicle);
    void deleteVehicle(String id);
}