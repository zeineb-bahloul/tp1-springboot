package com.example.restaurant.security;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Atelier 15 : toutes les opérations nécessitent un token JWT.
 *  - ADMIN : tout faire
 *  - USER  : seulement consulter (liste, un plat par id, recherches, restaurants)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(csrf -> csrf.disable())
				// éviter les erreurs Cross-Origin depuis Angular
				.cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
					@Override
					public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
						CorsConfiguration cors = new CorsConfiguration();
						cors.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
						cors.setAllowedMethods(Collections.singletonList("*"));
						cors.setAllowedHeaders(Collections.singletonList("*"));
						cors.setExposedHeaders(Collections.singletonList("Authorization"));
						return cors;
					}
				}))
				.authorizeHttpRequests(requests -> requests
						// page d'erreur de Spring Boot : visible pour afficher le vrai message d'erreur
						.requestMatchers("/error").permitAll()
						// Plats (produits dans le TP)
						.requestMatchers("/api/all/**").hasAnyAuthority("ADMIN", "USER")
						.requestMatchers(HttpMethod.GET, "/api/getbyid/**").hasAnyAuthority("ADMIN", "USER")
						.requestMatchers(HttpMethod.POST, "/api/addplat/**").hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/updateplat/**").hasAuthority("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/delplat/**").hasAuthority("ADMIN")
						// Recherches (prodscat = plats d'un restaurant, prodsByName = par nom)
						.requestMatchers(HttpMethod.GET, "/api/platsRes/**", "/api/prodscat/**", "/api/platscat/**")
							.hasAnyAuthority("ADMIN", "USER")
						.requestMatchers(HttpMethod.GET, "/api/platsByName/**", "/api/prodsByName/**")
							.hasAnyAuthority("ADMIN", "USER")
						// Restaurants (catégories dans le TP) : Spring Data REST /restos et /api/resto
						.requestMatchers(HttpMethod.GET, "/restos/**", "/api/resto/**", "/api/cat/**")
							.hasAnyAuthority("ADMIN", "USER")
						.requestMatchers("/restos/**", "/api/resto/**", "/api/cat/**").hasAuthority("ADMIN")
						// tout le reste (ex. /rest généré par Spring Data REST) : ADMIN
						.anyRequest().hasAuthority("ADMIN"))
				.addFilterBefore(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
