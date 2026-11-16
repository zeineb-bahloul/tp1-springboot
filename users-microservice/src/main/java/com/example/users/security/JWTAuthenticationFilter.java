package com.example.users.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.users.entities.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// Spring Boot 4 utilise Jackson 3 (package tools.jackson au lieu de com.fasterxml.jackson.databind)
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Atelier 13 : intercepte POST /login, vérifie username/password
 * et renvoie le token JWT dans le header "Authorization".
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authenticationManager;
	private final ObjectMapper objectMapper = JsonMapper.builder().build();

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
		this.authenticationManager = authenticationManager;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		User user = null;
		try {
			user = objectMapper.readValue(request.getInputStream(), User.class);
		} catch (Exception e) {
			e.printStackTrace();
			user = new User();
		}
		return authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain, Authentication authResult) throws IOException, ServletException {
		org.springframework.security.core.userdetails.User springUser =
				(org.springframework.security.core.userdetails.User) authResult.getPrincipal();

		List<String> roles = new ArrayList<>();
		springUser.getAuthorities().forEach(au -> {
			roles.add(au.getAuthority());
		});

		String jwt = JWT.create()
				.withSubject(springUser.getUsername())
				.withArrayClaim("roles", roles.toArray(new String[roles.size()]))
				.withExpiresAt(new Date(System.currentTimeMillis() + SecParams.EXP_TIME))
				.sign(Algorithm.HMAC256(SecParams.SECRET));

		response.addHeader("Authorization", jwt);
	}

	// Atelier Registration : message clair si l'email n'est pas encore validé
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		if (failed instanceof DisabledException) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			Map<String, Object> data = new HashMap<>();
			data.put("errorCause", "disabled");
			data.put("message", "L'utilisateur est désactivé !");
			String json = objectMapper.writeValueAsString(data);
			PrintWriter writer = response.getWriter();
			writer.println(json);
			writer.flush();
		} else {
			super.unsuccessfulAuthentication(request, response, failed);
		}
	}
}
