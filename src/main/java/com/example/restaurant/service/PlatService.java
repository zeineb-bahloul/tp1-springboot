package com.example.restaurant.service;

import java.util.List;

import com.example.restaurant.entities.Plat;
import com.example.restaurant.entities.Restaurant;

public interface PlatService {
	Plat savePlat(Plat p);
	Plat updatePlat(Plat p);
	void deletePlat(Plat p);
	void deletePlatById(Long id);
	Plat getPlat(Long id);
	List<Plat> getAllPlats();

	List<Plat> findByIntitulePlat(String nom);
	List<Plat> findByIntitulePlatContains(String nom);
	List<Plat> findByIntituleTarif(String nom, Double prix);
	List<Plat> findByRestaurant(Restaurant restaurant);
	List<Plat> findByRestaurantCodeResto(Long id);
	List<Plat> findByOrderByIntitulePlatAsc();
	List<Plat> trierPlatsIntituleTarif();
}
