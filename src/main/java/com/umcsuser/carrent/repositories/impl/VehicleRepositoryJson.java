package com.umcsuser.carrent.repositories.impl;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.db.JsonFileStorage;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.VehicleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehicleRepositoryJson implements VehicleRepository {

    private final JsonFileStorage<Vehicle> storage =
            new JsonFileStorage<>("vehicles.json", new TypeToken<List<Vehicle>>() {}.getType());
    private final List<Vehicle> vehicles;

    public VehicleRepositoryJson() {
        this.vehicles = new ArrayList<>(storage.load());
    }

    @Override
    public List<Vehicle> findAll() {
        return new ArrayList<>(vehicles);
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return vehicles.stream()
                .filter(v -> v.getId() != null && v.getId().equals(id))
                .findFirst();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            int maxId = 0;
            for (Vehicle v : vehicles) {
                try {
                    int currentId = Integer.parseInt(v.getId());
                    if (currentId > maxId) {
                        maxId = currentId;
                    }
                } catch (NumberFormatException e) {
                }
            }
            vehicle.setId(String.valueOf(maxId + 1));
        }

        vehicles.removeIf(v -> v.getId() != null && v.getId().equals(vehicle.getId()));
        vehicles.add(vehicle);
        storage.save(vehicles);
        return vehicle;
    }

    @Override
    public void deleteById(String id) {
        vehicles.removeIf(v -> v.getId() != null && v.getId().equals(id));
        storage.save(vehicles);
    }
}