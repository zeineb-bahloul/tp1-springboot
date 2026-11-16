import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { PlatService } from './plat';
import { Plat } from '../model/plat.model';
import { Restaurant } from '../model/restaurant.model';

describe('PlatService', () => {
  let service: PlatService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(PlatService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('listeRestaurants() demande la liste triée par codeResto', () => {
    service.listeRestaurants().subscribe();
    const req = httpMock.expectOne(r => r.url.startsWith(service.apiURLResto));
    expect(req.request.method).toBe('GET');
    expect(req.request.urlWithParams).toContain('sort=codeResto,asc');
    req.flush({ _embedded: { restaurants: [] } });
  });

  it('updatePlat() envoie un PUT sur /api/updateplat', () => {
    const plat: Plat = { referencePlat: 1, intitulePlat: 'Couscous', tarifPlat: 18 };
    service.updatePlat(plat).subscribe(p => expect(p.referencePlat).toBe(1));
    const req = httpMock.expectOne(service.apiURL + '/updateplat');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(plat);
    req.flush(plat);
  });

  it('ajouterRestaurant() envoie un POST sans codeResto', () => {
    const resto = { codeResto: 0, enseigneResto: 'Nouveau' } as Restaurant;
    service.ajouterRestaurant(resto).subscribe();
    const req = httpMock.expectOne(service.apiURLResto);
    expect(req.request.method).toBe('POST');
    expect(req.request.body.codeResto).toBeUndefined();
    expect(req.request.body.enseigneResto).toBe('Nouveau');
    req.flush({ ...resto, codeResto: 4 });
  });

  it('updateRestaurant() envoie un PUT sur /restos/{code}', () => {
    const resto = { codeResto: 3, enseigneResto: 'Dar Zmen' } as Restaurant;
    service.updateRestaurant(resto).subscribe();
    const req = httpMock.expectOne(`${service.apiURLResto}/3`);
    expect(req.request.method).toBe('PUT');
    req.flush(resto);
  });

  it('utilise les URL sécurisées de l\'atelier 15', () => {
    service.listePlats().subscribe();
    httpMock.expectOne(service.apiURL + '/all').flush([]);
    service.consulterPlat(3).subscribe();
    httpMock.expectOne(service.apiURL + '/getbyid/3').flush({});
    service.ajouterPlat({ intitulePlat: 'Brik' }).subscribe();
    const add = httpMock.expectOne(service.apiURL + '/addplat');
    expect(add.request.method).toBe('POST');
    add.flush({});
    service.supprimerPlat(3).subscribe();
    const del = httpMock.expectOne(service.apiURL + '/delplat/3');
    expect(del.request.method).toBe('DELETE');
    del.flush(null);
  });
});
