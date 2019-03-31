package com.vision.project.repositories.base;

import com.vision.project.models.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
}
