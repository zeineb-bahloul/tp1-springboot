import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Restaurant } from '../model/restaurant.model';
import { PlatService } from '../services/plat';
import { UpdateRestaurant } from '../update-restaurant/update-restaurant';

@Component({
  selector: 'app-liste-restaurants',
  imports: [CommonModule, UpdateRestaurant],
  templateUrl: './liste-restaurants.html',
  styles: ``
})
export class ListeRestaurants implements OnInit {
  restaurants!: Restaurant[];
  updatedResto: Restaurant = {
    codeResto: 0,
    enseigneResto: '',
    villeResto: '',
    noteMoyenne: 0,
    dateOuverture: new Date()
  };
  ajout: boolean = true;

  constructor(private platService: PlatService) {}

  ngOnInit(): void {
    this.chargerRestaurants();
  }

  chargerRestaurants(): void {
    this.platService.listeRestaurants().subscribe({
      next: (cats) => {
        // tri par code : un restaurant modifié garde sa place
        this.restaurants = [...cats._embedded.restaurants].sort((a, b) => a.codeResto - b.codeResto);
        console.log(cats);
      },
      error: (err) => {
        console.error('Erreur chargement restaurants :', err);
      }
    });
  }

  restaurantUpdated(resto: Restaurant): void {
    console.log('Resto updated event', resto);
    if (this.ajout) {
      this.platService.ajouterRestaurant(resto).subscribe({
        next: () => this.chargerRestaurants(),
        error: (err) => console.error('Erreur ajout restaurant :', err)
      });
    } else {
      this.platService.updateRestaurant(resto).subscribe({
        next: () => this.chargerRestaurants(),
        error: (err) => console.error('Erreur modification restaurant :', err)
      });
    }
    this.updatedResto = {
      codeResto: 0,
      enseigneResto: '',
      villeResto: '',
      noteMoyenne: 0,
      dateOuverture: new Date()
    };
    this.ajout = true;
  }

  categorieUpdated(cat: Restaurant): void {
    this.restaurantUpdated(cat);
  }

  updateResto(resto: Restaurant): void {
    this.updatedResto = resto;
    this.ajout = false;
  }

  updateCat(cat: Restaurant): void {
    this.updateResto(cat);
  }
}
