package com.example.restaurant.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.restaurant.entities.Plat;
import com.example.restaurant.entities.Restaurant;

@RepositoryRestResource(path = "rest")
public interface PlatRepository extends JpaRepository<Plat, Long> {

	List<Plat> findByIntitulePlat(String nom);

	List<Plat> findByIntitulePlatContains(String nom);

	@Query("select p from Plat p where p.intitulePlat like %:nom and p.tarifPlat > :prix")
	List<Plat> findByIntituleTarif(@Param("nom") String nom, @Param("prix") Double prix);

	@Query("select p from Plat p where p.restaurant = ?1")
	List<Plat> findByRestaurant(Restaurant restaurant);

	List<Plat> findByRestaurantCodeResto(Long id);

	List<Plat> findByOrderByIntitulePlatAsc();

	@Query("select p from Plat p order by p.intitulePlat ASC, p.tarifPlat DESC")
	List<Plat> trierPlatsIntituleTarif();

	// Versions triées par référence : l'ordre ne change plus après une modification
	List<Plat> findByRestaurantCodeRestoOrderByReferencePlatAsc(Long id);

	List<Plat> findByIntitulePlatContainsOrderByReferencePlatAsc(String nom);

}
