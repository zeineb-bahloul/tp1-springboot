import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Plat } from '../model/plat.model';
import { PlatService } from '../services/plat';
import { SearchFilterPipe } from '../search-filter-pipe';

@Component({
  selector: 'app-recherche-par-nom',
  imports: [FormsModule, CommonModule, SearchFilterPipe],
  templateUrl: './recherche-par-nom.html',
  styles: ``
})
export class RechercheParNom implements OnInit {
  intitulePlat!: string;
  searchTerm: string = '';
  plats!: Plat[];
  allPlats!: Plat[];

  constructor(private platService: PlatService) {}

  ngOnInit(): void {
    this.platService.listePlats().subscribe({
      next: (prods) => {
        console.log(prods);
        this.plats = prods;
        this.allPlats = prods;
      },
      error: (err) => {
        console.error('Erreur chargement plats :', err);
      }
    });
  }

  rechercherPlats(): void {
    if (this.intitulePlat) {
      this.platService.rechercherParNom(this.intitulePlat).subscribe({
        next: (prods) => {
          console.log(prods);
          this.plats = prods;
        },
        error: (err) => {
          console.error('Erreur recherche par nom :', err);
        }
      });
    } else {
      this.platService.listePlats().subscribe({
        next: (prods) => {
          console.log(prods);
          this.plats = prods;
        },
        error: (err) => {
          console.error('Erreur chargement plats :', err);
        }
      });
    }
  }

  onKeyUp(filterText: string): void {
    if (this.allPlats) {
      this.plats = this.allPlats.filter((item) =>
        (item.intitulePlat || '').toLowerCase().includes(filterText.toLowerCase())
      );
    }
  }
}
