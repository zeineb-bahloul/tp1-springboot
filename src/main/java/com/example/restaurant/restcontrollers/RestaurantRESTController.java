package com.example.restaurant.restcontrollers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant.entities.Restaurant;
import com.example.restaurant.repos.RestaurantRepository;

@RestController
@RequestMapping({"/api/resto", "/api/cat"})
@CrossOrigin(origins = "*")
public class RestaurantRESTController {

	@Autowired
	private RestaurantRepository restaurantRepository;

	@RequestMapping(method = RequestMethod.GET)
	public List<Restaurant> getAllRestaurants() {
		return restaurantRepository.findAll();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Restaurant getRestaurantById(@PathVariable("id") Long id) {
		return restaurantRepository.findById(id).orElse(null);
	}

	@PostMapping
	public Restaurant createRestaurant(@RequestBody Restaurant restaurant) {
		return restaurantRepository.save(restaurant);
	}

	@PutMapping
	public Restaurant updateRestaurant(@RequestBody Restaurant restaurant) {
		return restaurantRepository.save(restaurant);
	}
}
