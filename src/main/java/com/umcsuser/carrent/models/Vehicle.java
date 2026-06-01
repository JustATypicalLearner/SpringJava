package com.umcsuser.carrent.models;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "vehicle")
public class Vehicle implements Serializable {

    @Id
    @Column(nullable = false, unique = true)
    private String id;

    private String category;
    private String brand;
    private String model;

    @Column(name = "year")
    private int year;

    private String plate;

    @Column(columnDefinition = "NUMERIC")
    private double price;    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> attributes = new HashMap<>();    public Vehicle() {
    }

    public Vehicle(String id, String category, String brand, String model, int year, String plate, double price, Map<String, Object> attributes) {
        this.id = id;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plate = plate;
        this.price = price;
        this.attributes = attributes == null ? new HashMap<>() : new HashMap<>(attributes);
    }    public String getId() { return id; }
    public String getCategory() { return category; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public String getPlate() { return plate; }
    public double getPrice() { return price; }    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }    public void setId(String id) { this.id = id; }
    public void setCategory(String category) { this.category = category; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setModel(String model) { this.model = model; }
    public void setYear(int year) { this.year = year; }
    public void setPlate(String plate) { this.plate = plate; }
    public void setPrice(double price) { this.price = price; }    public void addAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }

    public void removeAttribute(String key) {
        this.attributes.remove(key);
    }

    public Vehicle copy() {
        return new Vehicle(this.id, this.category, this.brand, this.model, this.year, this.plate, this.price, new HashMap<>(this.attributes));
    }    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(id, vehicle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", plate='" + plate + '\'' +
                ", price=" + price +
                '}';
    }
}