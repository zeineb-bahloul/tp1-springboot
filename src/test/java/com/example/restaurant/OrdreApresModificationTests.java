package com.example.restaurant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.example.restaurant.entities.Plat;
import com.example.restaurant.entities.Restaurant;
import com.example.restaurant.repos.PlatRepository;
import com.example.restaurant.repos.RestaurantRepository;
import com.example.restaurant.restcontrollers.RestaurantRESTController;
import com.example.restaurant.service.PlatService;

/**
 * Vérifie qu'un plat ou un restaurant modifié garde sa place dans la liste.
 * Avant la correction, PostgreSQL renvoyait la ligne modifiée à la fin
 * (findAll() sans ORDER BY).
 *
 * Les tests sont @Transactional : les données créées sont annulées à la fin,
 * la base spring_db n'est pas modifiée.
 */
@SpringBootTest
@Transactional
class OrdreApresModificationTests {

	@Autowired
	private PlatService platService;

	@Autowired
	private PlatRepository platRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private RestaurantRESTController restaurantController;

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	private Restaurant resto;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
		resto = new Restaurant();
		resto.setEnseigneResto("Resto Test");
		resto = restaurantRepository.save(resto);
	}

	private List<Plat> creerTroisPlats() {
		List<Plat> plats = new ArrayList<>();
		for (String nom : new String[] { "Test Couscous", "Test Lablabi", "Test Ojja" }) {
			Plat p = new Plat(nom, 10.0, new Date());
			p.setRestaurant(resto);
			plats.add(platService.savePlat(p));
		}
		platRepository.flush();
		return plats;
	}

	private List<Long> references(List<Plat> plats) {
		return plats.stream().map(Plat::getReferencePlat).toList();
	}

	private static <T extends Comparable<T>> void assertTrieCroissant(List<T> ids) {
		List<T> tries = new ArrayList<>(ids);
		tries.sort(null);
		assertEquals(tries, ids, "La liste doit être triée par identifiant croissant");
	}

	@Test
	void plat_modifie_garde_sa_place_dans_getAllPlats() {
		List<Plat> crees = creerTroisPlats();
		Plat premier = crees.get(0);

		List<Long> avant = references(platService.getAllPlats());

		premier.setIntitulePlat("Test Couscous MODIFIÉ");
		premier.setTarifPlat(25.0);
		platService.updatePlat(premier);
		platRepository.flush();

		List<Long> apres = references(platService.getAllPlats());

		assertEquals(avant, apres, "L'ordre des plats ne doit pas changer après une modification");
		assertTrieCroissant(apres);
	}

	@Test
	void plat_modifie_garde_sa_place_dans_recherche_par_restaurant() {
		List<Plat> crees = creerTroisPlats();
		Plat premier = crees.get(0);
		premier.setTarifPlat(30.0);
		platService.updatePlat(premier);
		platRepository.flush();

		List<Plat> resultat = platService.findByRestaurantCodeResto(resto.getCodeResto());
		assertEquals(references(crees), references(resultat));
		assertEquals(30.0, resultat.get(0).getTarifPlat());
	}

	@Test
	void plat_modifie_garde_sa_place_dans_recherche_par_nom() {
		List<Plat> crees = creerTroisPlats();
		Plat premier = crees.get(0);
		premier.setTarifPlat(31.0);
		platService.updatePlat(premier);
		platRepository.flush();

		List<Long> idsCrees = references(crees);
		List<Long> resultat = references(platService.findByIntitulePlatContains("Test ")).stream()
				.filter(idsCrees::contains).toList();
		assertEquals(idsCrees, resultat);
	}

	@Test
	void put_api_plat_puis_get_api_garde_l_ordre() throws Exception {
		List<Plat> crees = creerTroisPlats();
		Plat premier = crees.get(0);
		List<Long> avant = references(platService.getAllPlats());

		String json = """
				{"referencePlat": %d, "intitulePlat": "Test via API", "tarifPlat": 12.5,
				 "restaurant": {"codeResto": %d}}
				""".formatted(premier.getReferencePlat(), resto.getCodeResto());
		mockMvc.perform(put("/api/updateplat").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
		platRepository.flush();

		mockMvc.perform(get("/api/all")).andExpect(status().isOk());
		assertEquals(avant, references(platService.getAllPlats()));
	}

	@Test
	void consulter_plat_inexistant_renvoie_null_au_lieu_d_une_exception() {
		assertEquals(null, platService.getPlat(-1L));
	}

	private List<Long> creerTroisRestos() {
		List<Long> ids = new ArrayList<>();
		for (String nom : new String[] { "Test Resto A", "Test Resto B", "Test Resto C" }) {
			Restaurant r = new Restaurant();
			r.setEnseigneResto(nom);
			ids.add(restaurantRepository.save(r).getCodeResto());
		}
		restaurantRepository.flush();
		return ids;
	}

	@Test
	void restaurant_modifie_garde_sa_place_dans_api_resto() {
		List<Long> ids = creerTroisRestos();
		List<Long> avant = restaurantController.getAllRestaurants().stream().map(Restaurant::getCodeResto).toList();

		Restaurant r = restaurantRepository.findById(ids.get(0)).orElseThrow();
		r.setEnseigneResto("Test Resto A MODIFIÉ");
		restaurantController.updateRestaurant(r);
		restaurantRepository.flush();

		List<Long> apres = restaurantController.getAllRestaurants().stream().map(Restaurant::getCodeResto).toList();
		assertEquals(avant, apres);
		assertTrieCroissant(apres);
	}

	/** C'est l'URL utilisée par le front Angular : /restos?sort=codeResto,asc */
	@Test
	void restaurant_modifie_garde_sa_place_dans_restos_trie() throws Exception {
		List<Long> ids = creerTroisRestos();
		Long id = ids.get(0);

		mockMvc.perform(put("/restos/" + id).contentType(MediaType.APPLICATION_JSON)
				.content("{\"codeResto\": " + id + ", \"enseigneResto\": \"Test Resto A MODIFIÉ\"}"))
				.andExpect(status().is2xxSuccessful());
		restaurantRepository.flush();

		String body = mockMvc.perform(get("/restos?sort=codeResto,asc&size=1000"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		List<Long> codes = new ArrayList<>();
		java.util.regex.Matcher m = java.util.regex.Pattern.compile("\"codeResto\"\\s*:\\s*(\\d+)").matcher(body);
		while (m.find())
			codes.add(Long.parseLong(m.group(1)));

		assertNotNull(body);
		assertTrieCroissant(codes);
		assertEquals(true, body.contains("Test Resto A MODIFIÉ"));
	}
}
