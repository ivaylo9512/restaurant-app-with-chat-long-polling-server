package com.vision.project.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "menu")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant")
    private Restaurant restaurant;

    private String name;

    public Menu() {
    }

    public Menu(String name, Restaurant restaurant) {
        this.name = name;
        this.restaurant = restaurant;
    }

    public Menu(long id, String name, Restaurant restaurant) {
        this.id = id;
        this.name = name;
        this.restaurant = restaurant;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }
}
