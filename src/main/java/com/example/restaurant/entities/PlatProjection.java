package com.example.restaurant.entities;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "nomPlat", types = { Plat.class })
public interface PlatProjection {
	public String getIntitulePlat();
}
