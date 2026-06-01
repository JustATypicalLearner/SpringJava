package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Rental;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Profile("jpa")
public interface RentalJpaRepository extends JpaRepository<Rental, String> {
    
    Optional<Rental> findByVehicle_IdAndReturnDateTimeIsNull(String vehicleId);
}