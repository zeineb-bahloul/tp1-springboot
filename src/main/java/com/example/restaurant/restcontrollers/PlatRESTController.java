package com.example.restaurant.restcontrollers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant.entities.Plat;
import com.example.restaurant.service.PlatService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class PlatRESTController {

	private final PlatService platService;

	// Atelier 15 : URL explicites pour pouvoir les sécuriser une par une
	@GetMapping("/all")
	public List<Plat> getAllPlat() {
		return platService.getAllPlats();
	}

	@GetMapping("/getbyid/{id}")
	public Plat getPlatById(@PathVariable("id") Long id) {
		return platService.getPlat(id);
	}

	@PostMapping("/addplat")
	public Plat createPlat(@RequestBody Plat plat) {
		// Ajout = nouvelle ligne : la référence est générée par la base.
		// Si une référence est envoyée, Hibernate ferait un merge et échouerait.
		plat.setReferencePlat(null);
		return platService.savePlat(plat);
	}

	@PutMapping("/updateplat")
	public Plat updatePlat(@RequestBody Plat plat) {
		return platService.updatePlat(plat);
	}

	@DeleteMapping("/delplat/{id}")
	public void deletePlat(@PathVariable("id") Long id) {
		platService.deletePlatById(id);
	}

	@GetMapping(value = {"/platsRes/{idRes}", "/prodscat/{idRes}", "/platscat/{idRes}"})
	public List<Plat> getPlatsByResId(@PathVariable("idRes") Long idRes) {
		return platService.findByRestaurantCodeResto(idRes);
	}

	@GetMapping(value = {"/platsByName/{nom}", "/prodsByName/{nom}"})
	public List<Plat> findByIntitulePlatContains(@PathVariable("nom") String nom) {
		return platService.findByIntitulePlatContains(nom);
	}
}
