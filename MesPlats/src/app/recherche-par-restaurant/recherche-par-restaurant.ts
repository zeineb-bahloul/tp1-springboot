import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Plat } from '../model/plat.model';
import { Restaurant } from '../model/restaurant.model';
import { PlatService } from '../services/plat';

@Component({
  selector: 'app-recherche-par-restaurant',
  imports: [FormsModule, CommonModule],
  templateUrl: './recherche-par-restaurant.html',
  styles: ``
})
export class RechercheParRestaurant implements OnInit {
  plats!: Plat[];
  idRestaurant!: number;
  restaurants!: Restaurant[];

  constructor(private platService: PlatService) {}

  ngOnInit(): void {
    this.platService.listeRestaurants().subscribe({
      next: (cats) => {
        this.restaurants = cats._embedded.restaurants;
        console.log(cats);
      },
      error: (err) => {
        console.error('Erreur chargement restaurants :', err);
      }
    });
  }

  onChange(): void {
    this.platService.rechercherParRestaurant(this.idRestaurant).subscribe({
      next: (prods) => {
        this.plats = prods;
      },
      error: (err) => {
        console.error('Erreur recherche par restaurant :', err);
      }
    });
  }
}
