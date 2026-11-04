import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

import { Plat } from '../model/plat.model';
import { PlatService } from '../services/plat';
import { Restaurant } from '../model/restaurant.model';

@Component({
  selector: 'app-update-plat',
  imports: [FormsModule, DatePipe],
  templateUrl: './update-plat.html'
})
export class UpdatePlat implements OnInit {
  currentPlat = new Plat();
  restaurants!: Restaurant[];
  updatedRestoId!: number;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private platService: PlatService
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

    this.platService.consulterPlat(this.activatedRoute.snapshot.params['id']).subscribe({
      next: (prod) => {
        this.currentPlat = prod;
        this.updatedRestoId = this.currentPlat.restaurant?.codeResto!;
      },
      error: (err) => {
        console.error('Erreur consultation plat :', err);
      }
    });
  }

  changerDate(date: string): void {
    this.currentPlat.dateAjoutAuMenu = date ? new Date(date) : undefined;
  }

  updatePlat(): void {
    if (this.updatedRestoId && this.restaurants) {
      this.currentPlat.restaurant = this.restaurants.find(
        (resto) => resto.codeResto == this.updatedRestoId
      )!;
    }

    this.platService.updatePlat(this.currentPlat).subscribe({
      next: (plat) => {
        console.log(plat);
        this.router.navigate(['plats']);
      },
      error: (err) => {
        console.error('Erreur mise à jour plat :', err);
      }
    });
  }
}
