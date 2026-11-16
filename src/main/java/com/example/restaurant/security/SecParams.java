package com.example.restaurant.security;

// Mêmes valeurs que dans users-microservice : le SECRET doit être identique
// pour que ce projet puisse vérifier les tokens générés là-bas.
public interface SecParams {
	public static final long EXP_TIME = 10 * 24 * 60 * 60 * 1000L;
	public static final String SECRET = "MesPlats-Zeineb-Secret-JWT";
	public static final String PREFIX = "Bearer ";
}
