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
@CrossOrigin
@AllArgsConstructor
public class PlatRESTController {
	//@Autowired
	private final PlatService platService;
	//@RequestMapping(method = RequestMethod.GET)
	@GetMapping
	public List<Plat> getAllPlat()
	{
	 return platService.getAllPlats();
	}
	//@RequestMapping(value="/{id}",method = RequestMethod.GET)
	@GetMapping("/{id}")
	public Plat getPlatById(@PathVariable("id") Long id) {
	return platService.getPlat(id);
	 }
	//@RequestMapping(method = RequestMethod.POST)
	@PostMapping()
	public Plat createPlat(@RequestBody Plat plat) {
	return platService.savePlat(plat);
	}
	
	//@RequestMapping(method = RequestMethod.PUT)
	@PutMapping()
	public Plat updatePlat(@RequestBody Plat plat) {
	return platService.updatePlat(plat);
	}
	//@RequestMapping(value="/{id}",method = RequestMethod.DELETE)
	 @DeleteMapping("/{id}")
	public void deletePlat(@PathVariable("id") Long id)
	{
	platService.deletePlatById(id);
	}
	 
	//@RequestMapping(value="/platsRes/{idRes}",method = RequestMethod.GET)
	 @GetMapping("/platsRes/{idRes}")
	public List<Plat> getPlatsByResId(@PathVariable("idRes") Long idRes) {
	return platService.findByRestaurantCodeResto(idRes);
	}
}
