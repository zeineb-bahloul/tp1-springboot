package com.example.restaurant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.restaurant.entities.Plat;
import com.example.restaurant.repos.PlatRepository;
import com.example.restaurant.security.SecParams;

import jakarta.servlet.Filter;

/**
 * Atelier 15 : règles de sécurité des api.
 * Les tokens sont fabriqués ici avec le même SECRET que users-microservice
 * (pas besoin de lancer users-microservice pour ces tests).
 * @Transactional : les plats créés sont annulés à la fin de chaque test.
 */
@SpringBootTest
@Transactional
class SecuriteApiTests {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private PlatRepository platRepository;

	private MockMvc mockMvc;

	private static String token(String username, String... roles) {
		return "Bearer " + JWT.create().withSubject(username).withArrayClaim("roles", roles)
				.withExpiresAt(new Date(System.currentTimeMillis() + SecParams.EXP_TIME))
				.sign(Algorithm.HMAC256(SecParams.SECRET));
	}

	private static final String ADMIN = token("admin", "ADMIN", "USER");
	private static final String USER = token("zeineb", "USER");

	@BeforeEach
	void setUp() {
		Filter securityFilterChain = context.getBean("springSecurityFilterChain", Filter.class);
		mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(securityFilterChain).build();
	}

	private Plat unPlat() {
		return platRepository.save(new Plat("Test Sécurité", 10.0, new Date()));
	}

	@Test
	void sans_token_tout_est_refuse() throws Exception {
		mockMvc.perform(get("/api/all")).andExpect(status().isForbidden());
		mockMvc.perform(get("/restos")).andExpect(status().isForbidden());
	}

	@Test
	void token_falsifie_est_refuse() throws Exception {
		String faux = "Bearer " + JWT.create().withSubject("pirate").withArrayClaim("roles", new String[] { "ADMIN" })
				.sign(Algorithm.HMAC256("autre-secret"));
		mockMvc.perform(get("/api/all").header("Authorization", faux)).andExpect(status().isForbidden());
	}

	@Test
	void USER_peut_consulter() throws Exception {
		Plat p = unPlat();
		mockMvc.perform(get("/api/all").header("Authorization", USER)).andExpect(status().isOk());
		mockMvc.perform(get("/api/getbyid/" + p.getReferencePlat()).header("Authorization", USER))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.intitulePlat").value("Test Sécurité"));
		mockMvc.perform(get("/api/platsByName/Test").header("Authorization", USER)).andExpect(status().isOk());
		mockMvc.perform(get("/api/platsRes/1").header("Authorization", USER)).andExpect(status().isOk());
		mockMvc.perform(get("/restos").header("Authorization", USER)).andExpect(status().isOk());
	}

	@Test
	void USER_ne_peut_pas_ajouter_modifier_supprimer() throws Exception {
		Plat p = unPlat();
		String json = "{\"intitulePlat\":\"Brik\",\"tarifPlat\":4}";
		mockMvc.perform(post("/api/addplat").header("Authorization", USER)
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isForbidden());
		mockMvc.perform(put("/api/updateplat").header("Authorization", USER)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"referencePlat\":" + p.getReferencePlat() + ",\"intitulePlat\":\"X\"}"))
				.andExpect(status().isForbidden());
		mockMvc.perform(delete("/api/delplat/" + p.getReferencePlat()).header("Authorization", USER))
				.andExpect(status().isForbidden());
		mockMvc.perform(post("/restos").header("Authorization", USER)
				.contentType(MediaType.APPLICATION_JSON).content("{\"enseigneResto\":\"X\"}"))
				.andExpect(status().isForbidden());
	}

	@Test
	void ADMIN_peut_tout_faire() throws Exception {
		mockMvc.perform(post("/api/addplat").header("Authorization", ADMIN)
				.contentType(MediaType.APPLICATION_JSON).content("{\"intitulePlat\":\"Brik\",\"tarifPlat\":4}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.intitulePlat").value("Brik"));

		Plat p = unPlat();
		mockMvc.perform(put("/api/updateplat").header("Authorization", ADMIN)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"referencePlat\":" + p.getReferencePlat() + ",\"intitulePlat\":\"Modifié\",\"tarifPlat\":5}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.intitulePlat").value("Modifié"));

		mockMvc.perform(delete("/api/delplat/" + p.getReferencePlat()).header("Authorization", ADMIN))
				.andExpect(status().isOk());

		mockMvc.perform(post("/restos").header("Authorization", ADMIN)
				.contentType(MediaType.APPLICATION_JSON).content("{\"enseigneResto\":\"Test Resto Sécurité\"}"))
				.andExpect(status().isCreated());
	}

	@Test
	void cors_autorise_angular() throws Exception {
		mockMvc.perform(options("/api/all").header("Origin", "http://localhost:4200")
				.header("Access-Control-Request-Method", "GET")
				.header("Access-Control-Request-Headers", "authorization"))
				.andExpect(status().isOk())
				.andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
	}
}
