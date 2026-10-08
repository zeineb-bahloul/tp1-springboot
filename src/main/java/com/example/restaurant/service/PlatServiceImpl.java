package com.example.restaurant.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.restaurant.entities.Plat;
import com.example.restaurant.entities.Restaurant;
import com.example.restaurant.repos.PlatRepository;

@Service
public class PlatServiceImpl implements PlatService {

	@Autowired
	PlatRepository platRepository;

	@Override
	public Plat savePlat(Plat p) {
		return platRepository.save(p);
	}

	@Override
	public Plat updatePlat(Plat p) {
		return platRepository.save(p);
	}

	@Override
	public void deletePlat(Plat p) {
		platRepository.delete(p);
	}

	@Override
	public void deletePlatById(Long id) {
		platRepository.deleteById(id);
	}

	@Override
	public Plat getPlat(Long id) {
		return platRepository.findById(id).get();
	}

	@Override
	public List<Plat> getAllPlats() {
		return platRepository.findAll();
	}

	@Override
	public List<Plat> findByIntitulePlat(String nom) {
		return platRepository.findByIntitulePlat(nom);
	}

	@Override
	public List<Plat> findByIntitulePlatContains(String nom) {
		return platRepository.findByIntitulePlatContains(nom);
	}

	@Override
	public List<Plat> findByIntituleTarif(String nom, Double prix) {
		return platRepository.findByIntituleTarif(nom, prix);
	}

	@Override
	public List<Plat> findByRestaurant(Restaurant restaurant) {
		return platRepository.findByRestaurant(restaurant);
	}

	@Override
	public List<Plat> findByRestaurantCodeResto(Long id) {
		return platRepository.findByRestaurantCodeResto(id);
	}

	@Override
	public List<Plat> findByOrderByIntitulePlatAsc() {
		return platRepository.findByOrderByIntitulePlatAsc();
	}

	@Override
	public List<Plat> trierPlatsIntituleTarif() {
		return platRepository.trierPlatsIntituleTarif();
	}
}
