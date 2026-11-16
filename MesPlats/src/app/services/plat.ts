import { Injectable } from '@angular/core';
import { Plat } from '../model/plat.model';
import { Restaurant } from '../model/restaurant.model';
import { RestaurantWrapped } from '../model/restaurant-wrapped.model';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
// Le token JWT est ajouté automatiquement par tokenInterceptor (atelier 17)
export class PlatService {
  apiURL: string = environment.apiURL || 'http://localhost:8082/restaurant/api';
  apiURLResto: string = 'http://localhost:8082/restaurant/restos';

  constructor(private http: HttpClient) {}

  listePlats(): Observable<Plat[]> {
    return this.http.get<Plat[]>(this.apiURL + '/all');
  }

  ajouterPlat(plat: Plat): Observable<Plat> {
    return this.http.post<Plat>(this.apiURL + '/addplat', plat, httpOptions);
  }

  supprimerPlat(id: number): Observable<any> {
    const url = `${this.apiURL}/delplat/${id}`;
    return this.http.delete(url, httpOptions);
  }

  consulterPlat(id: number): Observable<Plat> {
    const url = `${this.apiURL}/getbyid/${id}`;
    return this.http.get<Plat>(url);
  }

  updatePlat(plat: Plat): Observable<Plat> {
    return this.http.put<Plat>(this.apiURL + '/updateplat', plat, httpOptions);
  }

  listeRestaurants(): Observable<RestaurantWrapped> {
    // tri par code : l'ordre reste stable après une modification
    return this.http.get<RestaurantWrapped>(`${this.apiURLResto}?sort=codeResto,asc`);
  }

  consulterRestaurant(id: number): Observable<Restaurant> {
    return this.http.get<Restaurant>(`${this.apiURLResto}/${id}`);
  }

  ajouterRestaurant(resto: Restaurant): Observable<Restaurant> {
    // Le code est généré par la base : on ne l'envoie pas.
    // Avec codeResto = 0, Spring croit à une modification du restaurant n°0 et échoue.
    const { codeResto, ...nouveauResto } = resto;
    return this.http.post<Restaurant>(this.apiURLResto, nouveauResto, httpOptions);
  }

  ajouterCategorie(cat: Restaurant): Observable<Restaurant> {
    return this.ajouterRestaurant(cat);
  }

  updateRestaurant(resto: Restaurant): Observable<Restaurant> {
    return this.http.put<Restaurant>(`${this.apiURLResto}/${resto.codeResto}`, resto, httpOptions);
  }

  rechercherParRestaurant(codeResto: number): Observable<Plat[]> {
    const url = `${this.apiURL}/platsRes/${codeResto}`;
    return this.http.get<Plat[]>(url);
  }

  rechercherParCategorie(idCat: number): Observable<Plat[]> {
    return this.rechercherParRestaurant(idCat);
  }

  rechercherParNom(nom: string): Observable<Plat[]> {
    const url = `${this.apiURL}/platsByName/${nom}`;
    return this.http.get<Plat[]>(url);
  }
}
