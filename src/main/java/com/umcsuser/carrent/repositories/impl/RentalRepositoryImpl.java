package com.umcsuser.carrent.repositories.impl;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.db.JsonFileStorage;
import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RentalRepositoryImpl implements RentalRepository {

    private final JsonFileStorage<Rental> storage =
            new JsonFileStorage<>("rentals.json", new TypeToken<List<Rental>>() {}.getType());
    private final List<Rental> rentals;

    public RentalRepositoryImpl() {
        this.rentals = new ArrayList<>(storage.load());
    }

    @Override
    public void addRental(Rental rental) {
        rentals.add(rental);
        storage.save(rentals);
    }

    @Override
    public boolean removeRentalByVehicleId(String vehicleId) {
        boolean removed = rentals.removeIf(r -> r.getVehicleId().equals(vehicleId));
        if (removed) {
            storage.save(rentals);
        }
        return removed;
    }

    @Override
    public Rental getRentalByVehicleId(String vehicleId) {
        return rentals.stream()
                .filter(r -> r.getVehicleId().equals(vehicleId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Rental> getRentalsByUser(String login) {
        return rentals.stream()
                .filter(r -> r.getUserLogin().equalsIgnoreCase(login))
                .collect(Collectors.toList());
    }

    @Override
    public List<Rental> getAllRentals() {
        return new ArrayList<>(rentals);
    }

    @Override
    public boolean isRented(String vehicleId) {
        return getRentalByVehicleId(vehicleId) != null;
    }
}