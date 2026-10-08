package com.example.restaurant.entities;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Plat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long referencePlat;
	private String intitulePlat;
	private Double tarifPlat;
	private Date dateAjoutAuMenu;

	@ManyToOne
	private Restaurant restaurant;

	public Long getReferencePlat() {
		return referencePlat;
	}

	public void setReferencePlat(Long referencePlat) {
		this.referencePlat = referencePlat;
	}

	public String getIntitulePlat() {
		return intitulePlat;
	}

	public void setIntitulePlat(String intitulePlat) {
		this.intitulePlat = intitulePlat;
	}

	public Double getTarifPlat() {
		return tarifPlat;
	}

	public void setTarifPlat(Double tarifPlat) {
		this.tarifPlat = tarifPlat;
	}

	public Date getDateAjoutAuMenu() {
		return dateAjoutAuMenu;
	}

	public void setDateAjoutAuMenu(Date dateAjoutAuMenu) {
		this.dateAjoutAuMenu = dateAjoutAuMenu;
	}

	public Plat() {
		super();
	}

	public Plat(String intitulePlat, Double tarifPlat, Date dateAjoutAuMenu) {
		super();
		this.intitulePlat = intitulePlat;
		this.tarifPlat = tarifPlat;
		this.dateAjoutAuMenu = dateAjoutAuMenu;
	}

	@Override
	public String toString() {
		return "Plat [referencePlat=" + referencePlat + ", intitulePlat=" + intitulePlat + ", tarifPlat=" + tarifPlat
				+ ", dateAjoutAuMenu=" + dateAjoutAuMenu + "]";
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}
}
