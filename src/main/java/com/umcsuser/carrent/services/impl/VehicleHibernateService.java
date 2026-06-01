package com.umcsuser.carrent.services.impl;

import com.umcsuser.carrent.db.HibernateConfig;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.impl.VehicleHibernateRepository;
import com.umcsuser.carrent.services.VehicleServiceInterface;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class VehicleHibernateService implements VehicleServiceInterface {

    private final VehicleHibernateRepository vehicleRepo;

    public VehicleHibernateService(VehicleHibernateRepository vehicleRepo) {
        this.vehicleRepo = vehicleRepo;
    }

    @Override
    public List<Vehicle> findAllVehicles() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            vehicleRepo.setSession(session);
            return vehicleRepo.findAll();
        }
    }

    @Override
    public Optional<Vehicle> findVehicleById(String id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            vehicleRepo.setSession(session);
            return vehicleRepo.findById(id);
        }
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            vehicleRepo.setSession(session);

            Vehicle saved = vehicleRepo.save(vehicle);
            tx.commit();
            return saved;
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }

    @Override
    public void deleteVehicle(String id) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            vehicleRepo.setSession(session);

            vehicleRepo.deleteById(id);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }
}