import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { UpdatePlat } from './update-plat';
import { PlatService } from '../services/plat';
import { Plat } from '../model/plat.model';
import { Restaurant } from '../model/restaurant.model';

describe('UpdatePlat', () => {
  let fixture: ComponentFixture<UpdatePlat>;
  let component: UpdatePlat;
  let service: jasmine.SpyObj<PlatService>;
  let router: jasmine.SpyObj<Router>;

  const restos = [
    { codeResto: 1, enseigneResto: 'Resto A' },
    { codeResto: 2, enseigneResto: 'Resto B' }
  ] as Restaurant[];

  beforeEach(async () => {
    const plat: Plat = { referencePlat: 5, intitulePlat: 'Ojja', tarifPlat: 9, restaurant: restos[0] };
    service = jasmine.createSpyObj('PlatService', ['listeRestaurants', 'consulterPlat', 'updatePlat']);
    service.listeRestaurants.and.returnValue(of({ _embedded: { restaurants: restos } }));
    service.consulterPlat.and.returnValue(of(plat));
    service.updatePlat.and.callFake((p: Plat) => of(p));
    router = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [UpdatePlat],
      providers: [
        { provide: PlatService, useValue: service },
        { provide: Router, useValue: router },
        { provide: ActivatedRoute, useValue: { snapshot: { params: { id: 5 } } } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UpdatePlat);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('charge le plat et son restaurant', () => {
    expect(component.currentPlat.referencePlat).toBe(5);
    expect(component.updatedRestoId).toBe(1);
  });

  it('envoie le plat modifié (même référence) puis revient à la liste', () => {
    component.currentPlat.tarifPlat = 12;
    component.updatedRestoId = 2;
    component.updatePlat();

    const envoye = service.updatePlat.calls.mostRecent().args[0];
    expect(envoye.referencePlat).toBe(5);
    expect(envoye.tarifPlat).toBe(12);
    expect(envoye.restaurant?.codeResto).toBe(2);
    expect(router.navigate).toHaveBeenCalledWith(['plats']);
  });
});
