package com.umcsuser.carrent;

import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.VehicleCategoryConfigRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;

import com.umcsuser.carrent.repositories.impl.RentalRepositoryImpl;
import com.umcsuser.carrent.repositories.impl.UserRepositoryImpl;
import com.umcsuser.carrent.repositories.impl.VehicleCategoryConfigJsonRepository;
import com.umcsuser.carrent.repositories.impl.VehicleRepositoryJson;

import com.umcsuser.carrent.services.*;

public class Main {
    public static void main(String[] args) {

        VehicleCategoryConfigRepository configRepo = new VehicleCategoryConfigJsonRepository();
        VehicleRepository vehicleRepo = new VehicleRepositoryJson();

        UserRepository userRepo = new UserRepositoryImpl();
        RentalRepository rentalRepo = new RentalRepositoryImpl();

        VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepo);
        VehicleValidator validator = new VehicleValidator(configService);

        VehicleService vehicleService = new VehicleService(validator, vehicleRepo, rentalRepo);
        UserService userService = new UserService(userRepo, rentalRepo);
        AuthService authService = new AuthService(userRepo);

        RentalService rentalService = new RentalService(rentalRepo, vehicleService);

        UI ui = new UI(configService, vehicleService, userService, authService, rentalService);
        ui.start();
    }
}