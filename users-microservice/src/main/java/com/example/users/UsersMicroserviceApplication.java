package com.example.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.users.entities.Role;
import com.example.users.entities.User;
import com.example.users.repos.RoleRepository;
import com.example.users.service.UserService;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class UsersMicroserviceApplication {

	@Autowired
	UserService userService;

	@Autowired
	RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(UsersMicroserviceApplication.class, args);
	}

	/*
	 * Atelier 13 - étape 10 : ajouter les rôles et les utilisateurs.
	 * Dans le TP, on commente cette méthode après le premier démarrage.
	 * Ici elle ne fait rien si les rôles existent déjà : pas besoin de la commenter.
	 */
	@PostConstruct
	void init_users() {
		if (roleRepository.findByRole("ADMIN") != null)
			return;

		// ajouter les rôles
		userService.addRole(new Role(null, "ADMIN"));
		userService.addRole(new Role(null, "USER"));

		// ajouter les users
		userService.saveUser(new User(null, "admin", "123", true, "admin@mesplats.tn", null));
		userService.saveUser(new User(null, "zeineb", "123", true, "zeineb@mesplats.tn", null));

		// ajouter les rôles aux users
		userService.addRoleToUser("admin", "ADMIN");
		userService.addRoleToUser("admin", "USER");
		userService.addRoleToUser("zeineb", "USER");
	}
}
