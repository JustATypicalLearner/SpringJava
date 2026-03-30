package org.example.carrent;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VehicleRepositoryImpl implements IVehicleRepository{
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final String filePath = "vehicles.csv";

    public VehicleRepositoryImpl() {
        load();
    }

    @Override
    public boolean rentVehicle(String id) {
        for (Vehicle v : vehicles) {
            if (v.getId().equals(id)) {
                if (!v.isRented()) {
                    v.setRented(true);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean returnVehicle(String id) {
        for (Vehicle v : vehicles) {
            if (v.getId().equals(id)) {
                if (v.isRented()) {
                    v.setRented(false);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public List<Vehicle> getVehicles() {
        List<Vehicle> deepCopy = new ArrayList<>();
        for(Vehicle v: vehicles){
            deepCopy.add(v.copy());
        }
        return deepCopy;
    }

    @Override
    public Vehicle getVehicle(String id) {
        for(Vehicle v : vehicles){
            if (v.getId().equals(id)) {
                return v.copy();
            }
        }
        return null;
    }

    @Override
    public boolean add(Vehicle vehicle) {
        if (getVehicle(vehicle.getId()) != null) {
            return false;
        }
        return vehicles.add(vehicle);
    }

    @Override
    public boolean remove(String id) {
        Iterator<Vehicle> iterator = vehicles.iterator();
        while (iterator.hasNext()) {
            Vehicle v = iterator.next();
            if (v.getId().equals(id)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public void save(List<Vehicle> vehiclesToSave) throws IOException {
        try(FileWriter writer = new FileWriter(filePath)){
            writer.write("TYPE;ID;BRAND;MODEL;YEAR;PRICE;RENTED[;LICENCE]\n");
            for(Vehicle v: vehiclesToSave){
                writer.write(v.toCsv() + "\n");
            }
        }
    }

    public List<Vehicle> load() {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Vehicles file not found. Starting with an empty repository.");
            return vehicles;
        }

        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            reader.readLine(); 

            String line;
            while((line = reader.readLine()) != null){
                String[] parts = line.split(";");

                String vehicleType = parts[0]; 
                
                int i = 0;
                if(vehicleType.equals("CAR")){
                    String id = parts[++i];
                    String brand = parts[++i];
                    String model = parts[++i];
                    int year = Integer.parseInt(parts[++i]);
                    double price = Double.parseDouble(parts[++i]);
                    boolean rented = Boolean.parseBoolean(parts[++i]);

                    Car c = new Car(id,brand,model,year,price,rented);
                    vehicles.add(c);
                }
                else if (vehicleType.equals("MOTORCYCLE")){
                    String id = parts[++i];
                    String brand = parts[++i];
                    String model = parts[++i];
                    int year = Integer.parseInt(parts[++i]);
                    double price = Double.parseDouble(parts[++i]);
                    boolean rented = Boolean.parseBoolean(parts[++i]);
                    String drivingLicence = parts[++i];
                    Motorcycle m = new Motorcycle(id,brand,model,year,price,rented,drivingLicence);

                    vehicles.add(m);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading vehicles from file", e);
        }
        return vehicles;
    }
}
