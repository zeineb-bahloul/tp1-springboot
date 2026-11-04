package com.example.restaurant.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.example.restaurant.entities.Restaurant;

@RepositoryRestResource(path = "restos")
@CrossOrigin(origins = "*")
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}

