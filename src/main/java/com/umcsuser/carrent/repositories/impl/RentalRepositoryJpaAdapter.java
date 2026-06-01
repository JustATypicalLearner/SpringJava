package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Rental;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("jpa")
public class RentalRepositoryJpaAdapter {

    private final RentalJpaRepository delegate;

    public RentalRepositoryJpaAdapter(RentalJpaRepository delegate) {
        this.delegate = delegate;
    }

    public List<Rental> findAll() {
        return delegate.findAll();
    }

    public Optional<Rental> findById(String id) {
        return delegate.findById(id);
    }

    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        return delegate.findByVehicle_IdAndReturnDateTimeIsNull(vehicleId);
    }

    public Rental save(Rental rental) {
        return delegate.save(rental);
    }

    public void deleteById(String id) {
        delegate.deleteById(id);
    }
}