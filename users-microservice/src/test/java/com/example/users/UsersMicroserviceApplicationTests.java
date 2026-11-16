package com.example.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.users.entities.User;
import com.example.users.entities.VerificationToken;
import com.example.users.repos.UserRepository;
import com.example.users.repos.VerificationTokenRepository;
import com.example.users.security.SecParams;
import com.example.users.util.EmailSender;

import jakarta.servlet.Filter;

/**
 * Tests des ateliers 13 (génération du JWT), 14 (vérification du JWT, rôles)
 * et Registration (inscription, code par email, validation, utilisateur désactivé).
 * Base H2 en mémoire (src/test/resources/application.properties).
 */
@SpringBootTest
class UsersMicroserviceApplicationTests {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private VerificationTokenRepository tokenRepository;

	// on n'envoie pas de vrai email pendant les tests
	@MockitoBean
	private EmailSender emailSender;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		Filter securityFilterChain = context.getBean("springSecurityFilterChain", Filter.class);
		mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(securityFilterChain).build();
	}

	private MvcResult login(String username, String password) throws Exception {
		return mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
				.andReturn();
	}

	private String tokenDe(String username, String password) throws Exception {
		MvcResult res = login(username, password);
		assertEquals(200, res.getResponse().getStatus(), "le login de " + username + " doit réussir");
		return res.getResponse().getHeader("Authorization");
	}

	@Test
	void init_users_a_cree_admin_et_zeineb() {
		User admin = userRepository.findByUsername("admin");
		assertNotNull(admin);
		assertEquals(2, admin.getRoles().size());
		assertNotNull(userRepository.findByUsername("zeineb"));
		// mot de passe chiffré avec BCrypt
		assertTrue(admin.getPassword().startsWith("$2"));
	}

	@Test
	void login_admin_renvoie_un_jwt_avec_le_nom_et_les_roles() throws Exception {
		String jwt = tokenDe("admin", "123");
		assertNotNull(jwt);

		DecodedJWT decoded = JWT.require(Algorithm.HMAC256(SecParams.SECRET)).build().verify(jwt);
		assertEquals("admin", decoded.getSubject());
		List<String> roles = decoded.getClaims().get("roles").asList(String.class);
		assertTrue(roles.contains("ADMIN"));
		assertTrue(roles.contains("USER"));
	}

	@Test
	void login_avec_mauvais_mot_de_passe_est_refuse() throws Exception {
		MvcResult res = login("admin", "faux");
		assertEquals(401, res.getResponse().getStatus());
		assertEquals(null, res.getResponse().getHeader("Authorization"));
	}

	@Test
	void api_all_sans_token_est_refusee() throws Exception {
		mockMvc.perform(get("/all")).andExpect(status().isForbidden());
	}

	@Test
	void api_all_refusee_pour_USER_autorisee_pour_ADMIN() throws Exception {
		String jwtUser = tokenDe("zeineb", "123");
		mockMvc.perform(get("/all").header("Authorization", "Bearer " + jwtUser))
				.andExpect(status().isForbidden());

		String jwtAdmin = tokenDe("admin", "123");
		mockMvc.perform(get("/all").header("Authorization", "Bearer " + jwtAdmin))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].username").exists())
				// le mot de passe n'est jamais renvoyé
				.andExpect(jsonPath("$[0].password").doesNotExist());
	}

	@Test
	void token_falsifie_est_refuse() throws Exception {
		String faux = JWT.create().withSubject("admin").withArrayClaim("roles", new String[] { "ADMIN" })
				.sign(Algorithm.HMAC256("mauvais-secret"));
		mockMvc.perform(get("/all").header("Authorization", "Bearer " + faux))
				.andExpect(status().isForbidden());
	}

	@Test
	void inscription_validation_email_puis_login() throws Exception {
		// 1. inscription : utilisateur créé mais désactivé, rôle USER
		mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"sami\",\"password\":\"123456\",\"email\":\"sami@test.tn\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("sami"))
				.andExpect(jsonPath("$.enabled").value(false))
				.andExpect(jsonPath("$.roles[0].role").value("USER"));

		User sami = userRepository.findByUsername("sami");
		assertFalse(sami.getEnabled());
		// le code a été "envoyé" par email
		org.mockito.Mockito.verify(emailSender).sendEmail(org.mockito.ArgumentMatchers.eq("sami@test.tn"),
				org.mockito.ArgumentMatchers.anyString());

		// 2. login avant validation : 403 + errorCause "disabled"
		mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"sami\",\"password\":\"123456\"}"))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.errorCause").value("disabled"));

		// 3. mauvais code : 404 INVALID_TOKEN
		mockMvc.perform(get("/verifyEmail/000000"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errorCode").value("INVALID_TOKEN"));

		// 4. bon code : l'utilisateur est activé
		String code = tokenRepository.findAll().stream()
				.filter(t -> t.getUser().getUsername().equals("sami"))
				.map(VerificationToken::getToken).findFirst().orElseThrow();
		assertEquals(6, code.length());
		mockMvc.perform(get("/verifyEmail/" + code))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.enabled").value(true));

		// 5. login maintenant possible, rôle USER dans le token
		String jwt = tokenDe("sami", "123456");
		List<String> roles = JWT.decode(jwt).getClaims().get("roles").asList(String.class);
		assertEquals(List.of("USER"), roles);

		// 6. même email une deuxième fois : 400 USER_EMAIL_ALREADY_EXISTS
		mockMvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"sami2\",\"password\":\"123456\",\"email\":\"sami@test.tn\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errorCode").value("USER_EMAIL_ALREADY_EXISTS"));
	}

	@Test
	void login_expose_le_header_Authorization_pour_angular() throws Exception {
		mockMvc.perform(post("/login").header("Origin", "http://localhost:4200")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"admin\",\"password\":\"123\"}"))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
						.string("Access-Control-Expose-Headers", org.hamcrest.Matchers.containsString("Authorization")));
	}
}
