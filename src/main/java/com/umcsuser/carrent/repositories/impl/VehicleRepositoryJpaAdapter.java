package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Vehicle;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("jpa")
public class VehicleRepositoryJpaAdapter {

    private final VehicleJpaRepository delegate;

    public VehicleRepositoryJpaAdapter(VehicleJpaRepository delegate) {
        this.delegate = delegate;
    }

    public List<Vehicle> findAll() {
        return delegate.findAll();
    }

    public Optional<Vehicle> findById(String id) {
        return delegate.findById(id);
    }

    public Vehicle save(Vehicle vehicle) {
        return delegate.save(vehicle);
    }

    public void deleteById(String id) {
        delegate.deleteById(id);
    }
}