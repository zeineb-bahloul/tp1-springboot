import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Plat } from '../model/plat.model';
import { PlatService } from '../services/plat';
import { AuthService } from '../services/auth';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-plats',
  imports: [CommonModule, RouterLink],
  templateUrl: './plats.html',
  styleUrl: './plats.css',
})
export class Plats implements OnInit {
  plats: Plat[] = [];

  constructor(
    private platService: PlatService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.chargerPlats();
  }

  chargerPlats(): void {
    this.platService.listePlats().subscribe({
      next: (prods) => {
        console.log(prods);
        // tri par référence : un plat modifié garde sa place
        this.plats = [...prods].sort((a, b) => (a.referencePlat ?? 0) - (b.referencePlat ?? 0));
      },
      error: (err) => {
        console.error('Erreur lors du chargement des plats :', err);
      }
    });
  }

  supprimerPlat(p: Plat): void {
    let conf = confirm('Êtes-vous sûr ?');
    if (conf) {
      this.platService.supprimerPlat(p.referencePlat!).subscribe({
        next: () => {
          console.log('plat supprimé');
          this.chargerPlats();
        },
        error: (err) => {
          console.error('Erreur lors de la suppression :', err);
        }
      });
    }
  }
}
