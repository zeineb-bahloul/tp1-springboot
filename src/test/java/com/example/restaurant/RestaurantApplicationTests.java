package com.example.restaurant;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.restaurant.entities.Plat;
import com.example.restaurant.entities.Restaurant;
import com.example.restaurant.repos.PlatRepository;

@SpringBootTest
class RestaurantApplicationTests {

	@Autowired
	private PlatRepository platRepository;

	@Test
	public void testCreatePlat() {
		Plat plat = new Plat("Couscous au poulet", 18.500, new Date());
		platRepository.save(plat);
	}

	@Test
	public void testFindPlat() {
		Plat p = platRepository.findById(1L).get();
		System.out.println(p);
	}

	@Test
	public void testUpdatePlat() {
		Plat p = platRepository.findById(1L).get();
		p.setTarifPlat(19.5);
		platRepository.save(p);
		System.out.println(p);
	}

	@Test
	public void testDeletePlat() {
		platRepository.deleteById(1L);
	}

	@Test
	public void testFindAllPlats() {
		List<Plat> plats = platRepository.findAll();
		for (Plat p : plats)
			System.out.println(p);
	}

	@Test
	public void testFindPlatByIntitule() {
		List<Plat> plats = platRepository.findByIntitulePlat("Couscous au poisson");
		for (Plat p : plats)
			System.out.println(p);
	}

	@Test
	public void testFindPlatByIntituleContains() {
		List<Plat> plats = platRepository.findByIntitulePlatContains("C");
		for (Plat p : plats)
			System.out.println(p);
	}

	@Test
	public void testFindByIntituleTarif() {
		List<Plat> plats = platRepository.findByIntituleTarif("Couscous", 10.0);
		for (Plat p : plats) {
			System.out.println(p);
		}
	}

	@Test
	public void testFindByRestaurant() {
		Restaurant resto = new Restaurant();
		resto.setCodeResto(1L);
		List<Plat> plats = platRepository.findByRestaurant(resto);
		for (Plat p : plats) {
			System.out.println(p);
		}
	}

	@Test
	public void testFindByRestaurantCodeResto() {
		List<Plat> plats = platRepository.findByRestaurantCodeResto(1L);
		for (Plat p : plats) {
			System.out.println(p);
		}
	}

	@Test
	public void testFindByOrderByIntitulePlatAsc() {
		List<Plat> plats = platRepository.findByOrderByIntitulePlatAsc();
		for (Plat p : plats) {
			System.out.println(p);
		}
	}

	@Test
	public void testTrierPlatsIntituleTarif() {
		List<Plat> plats = platRepository.trierPlatsIntituleTarif();
		for (Plat p : plats) {
			System.out.println(p);
		}
	}
}
