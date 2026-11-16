import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { Plat } from '../model/plat.model';
import { Restaurant } from '../model/restaurant.model';
import { PlatService } from '../services/plat';

@Component({
  selector: 'app-add-plat',
  imports: [FormsModule],
  templateUrl: './add-plat.html'
})
export class AddPlat implements OnInit {
  newPlat = new Plat();
  restaurants!: Restaurant[];
  newIdResto!: number;
  newRestaurant!: Restaurant;
  message = '';

  constructor(
    private platService: PlatService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.platService.listeRestaurants().subscribe({
      next: (cats) => {
        console.log(cats);
        this.restaurants = cats._embedded.restaurants;
      },
      error: (err) => {
        console.error('Erreur chargement restaurants :', err);
      }
    });
  }

  addPlat(): void {
    if (this.newIdResto && this.restaurants) {
      this.newRestaurant = this.restaurants.find(resto => resto.codeResto == this.newIdResto)!;
      this.newPlat.restaurant = this.newRestaurant;
    }

    // La référence est générée par la base : on ne l'envoie jamais à l'ajout
    delete this.newPlat.referencePlat;

    if (!this.newPlat.intitulePlat) {
      this.message = "L'intitulé du plat est obligatoire.";
      return;
    }
    this.message = '';

    this.platService.ajouterPlat(this.newPlat).subscribe({
      next: (plat) => {
        console.log(plat);
        this.router.navigate(['plats']);
      },
      error: (err) => {
        console.error("Erreur lors de l'ajout du plat :", err);
        this.message = "Erreur lors de l'ajout du plat (le serveur Spring Boot est-il lancé ?)";
      }
    });
  }
}
