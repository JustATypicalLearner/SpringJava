package com.umcsuser.carrent;

import com.umcsuser.carrent.db.HibernateConfig;
import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.impl.*;
import com.umcsuser.carrent.services.*;
import com.umcsuser.carrent.services.impl.AuthHibernateService;
import com.umcsuser.carrent.services.impl.RentalHibernateService;
import com.umcsuser.carrent.services.impl.VehicleHibernateService;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class Main {
    public static void main(String[] args) {        String mode = (args.length > 0) ? args[0].toLowerCase() : "hibernate";
        AuthServiceInterface authService;
        VehicleServiceInterface vehicleService;
        RentalServiceInterface rentalService;

        if ("hibernate".equals(mode)) {
            System.out.println("Uruchamianie w trybie: HIBERNATE (PostgreSQL ORM)");            UserHibernateRepository userRepo = new UserHibernateRepository();
            VehicleHibernateRepository vehicleRepo = new VehicleHibernateRepository();
            RentalHibernateRepository rentalRepo = new RentalHibernateRepository();            authService = new AuthHibernateService(userRepo);
            vehicleService = new VehicleHibernateService(vehicleRepo);
            rentalService = new RentalHibernateService(rentalRepo, vehicleRepo, userRepo);            HibernateConfig.getSessionFactory();

        } else {
            throw new IllegalArgumentException("Ten kod został zoptymalizowany pod tryb 'hibernate'.");        }        UI ui = new UI(vehicleService, authService, rentalService);
        ui.start();
    }
}