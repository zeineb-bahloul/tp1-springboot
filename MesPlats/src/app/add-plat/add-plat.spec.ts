import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AddPlat } from './add-plat';
import { PlatService } from '../services/plat';
import { Plat } from '../model/plat.model';
import { Restaurant } from '../model/restaurant.model';

describe('AddPlat', () => {
  let fixture: ComponentFixture<AddPlat>;
  let component: AddPlat;
  let service: jasmine.SpyObj<PlatService>;
  let router: jasmine.SpyObj<Router>;
  const restos = [{ codeResto: 1, enseigneResto: 'Resto A' }, { codeResto: 2, enseigneResto: 'Resto B' }] as Restaurant[];

  beforeEach(async () => {
    service = jasmine.createSpyObj('PlatService', ['listeRestaurants', 'ajouterPlat']);
    service.listeRestaurants.and.returnValue(of({ _embedded: { restaurants: restos } }));
    service.ajouterPlat.and.callFake((p: Plat) => of({ ...p, referencePlat: 99 }));
    router = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [AddPlat],
      providers: [{ provide: PlatService, useValue: service }, { provide: Router, useValue: router }]
    }).compileComponents();
    fixture = TestBed.createComponent(AddPlat);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('n\'affiche plus de champ Référence', () => {
    expect(fixture.nativeElement.querySelector('[name=referencePlat]')).toBeNull();
  });

  it('envoie le plat sans référence, avec son restaurant, puis revient à la liste', () => {
    component.newPlat.referencePlat = 5; // même si une référence traîne, elle n'est pas envoyée
    component.newPlat.intitulePlat = 'Brik';
    component.newPlat.tarifPlat = 4;
    component.newIdResto = 2;
    component.addPlat();

    const envoye = service.ajouterPlat.calls.mostRecent().args[0];
    expect(envoye.referencePlat).toBeUndefined();
    expect(envoye.intitulePlat).toBe('Brik');
    expect(envoye.restaurant?.codeResto).toBe(2);
    expect(router.navigate).toHaveBeenCalledWith(['plats']);
  });

  it('refuse un plat sans intitulé', () => {
    component.addPlat();
    expect(service.ajouterPlat).not.toHaveBeenCalled();
    expect(component.message).toContain('obligatoire');
  });

  it('affiche un message si le serveur renvoie une erreur', () => {
    service.ajouterPlat.and.returnValue(throwError(() => new Error('500')));
    component.newPlat.intitulePlat = 'Brik';
    component.addPlat();
    fixture.detectChanges();
    expect(router.navigate).not.toHaveBeenCalled();
    expect(fixture.nativeElement.querySelector('.alert-danger')).not.toBeNull();
  });
});
