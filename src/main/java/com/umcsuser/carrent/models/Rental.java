package com.umcsuser.carrent.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Rental implements Serializable {
    private String userLogin;
    private String vehicleId;
    private LocalDateTime rentalDate;

    public Rental(String userLogin, String vehicleId) {
        this.userLogin = userLogin;
        this.vehicleId = vehicleId;
        this.rentalDate = LocalDateTime.now();
    }

    public String getUserLogin() { return userLogin; }
    public String getVehicleId() { return vehicleId; }
    public LocalDateTime getRentalDate() { return rentalDate; }
}