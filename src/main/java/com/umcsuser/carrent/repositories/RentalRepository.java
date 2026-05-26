package com.umcsuser.carrent.repositories;
import com.umcsuser.carrent.models.Rental;

import java.util.List;

public interface RentalRepository {
    void addRental(Rental rental);
    boolean removeRentalByVehicleId(String vehicleId);
    Rental getRentalByVehicleId(String vehicleId);
    List<Rental> getRentalsByUser(String login);
    List<Rental> getAllRentals();
    boolean isRented(String vehicleId);
}