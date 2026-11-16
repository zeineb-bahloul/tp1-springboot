package com.example.users.security;

public interface SecParams {
	public static final long EXP_TIME = 10 * 24 * 60 * 60 * 1000L; // 10 jours en millisecondes
	public static final String SECRET = "MesPlats-Zeineb-Secret-JWT";
	public static final String PREFIX = "Bearer ";
}
