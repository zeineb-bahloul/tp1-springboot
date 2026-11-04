import { Routes } from '@angular/router';
import { Plats } from './plats/plats';
import { AddPlat } from './add-plat/add-plat';
import { UpdatePlat } from './update-plat/update-plat';
import { RechercheParRestaurant } from './recherche-par-restaurant/recherche-par-restaurant';
import { RechercheParNom } from './recherche-par-nom/recherche-par-nom';
import { ListeRestaurants } from './liste-restaurants/liste-restaurants';
import { Login } from './login/login';
import { Forbidden } from './forbidden/forbidden';
import { platGuard } from './plat-guard';

export const routes: Routes = [
  { path: 'plats', component: Plats },
  { path: 'add-plat', component: AddPlat, canActivate: [platGuard] },
  { path: 'add-produit', redirectTo: 'add-plat' },
  { path: 'updatePlat/:id', component: UpdatePlat, canActivate: [platGuard] },
  { path: 'rechercheParRestaurant', component: RechercheParRestaurant },
  { path: 'rechercheParCategorie', redirectTo: 'rechercheParRestaurant' },
  { path: 'rechercheParNom', component: RechercheParNom },
  { path: 'listeRestaurants', component: ListeRestaurants },
  { path: 'listeCategories', redirectTo: 'listeRestaurants' },
  { path: 'login', component: Login },
  { path: 'app-forbidden', component: Forbidden },
  { path: '', redirectTo: 'plats', pathMatch: 'full' }
];
