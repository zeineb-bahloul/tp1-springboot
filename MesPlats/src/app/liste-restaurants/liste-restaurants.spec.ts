import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ListeRestaurants } from './liste-restaurants';
import { PlatService } from '../services/plat';
import { Restaurant } from '../model/restaurant.model';

function resto(code: number, nom: string): Restaurant {
  return { codeResto: code, enseigneResto: nom, villeResto: '', noteMoyenne: 0, dateOuverture: new Date() };
}

describe('ListeRestaurants', () => {
  let fixture: ComponentFixture<ListeRestaurants>;
  let component: ListeRestaurants;
  let service: jasmine.SpyObj<PlatService>;
  // simule la base : l'API renvoie toujours la liste triée par code
  let base: Restaurant[];

  beforeEach(async () => {
    base = [resto(1, 'Resto A'), resto(2, 'Resto B'), resto(3, 'Resto C')];
    service = jasmine.createSpyObj('PlatService', ['listeRestaurants', 'updateRestaurant', 'ajouterRestaurant']);
    service.listeRestaurants.and.callFake(() =>
      of({ _embedded: { restaurants: base.map(r => ({ ...r })) } }));
    service.updateRestaurant.and.callFake((r: Restaurant) => {
      base = base.map(b => (b.codeResto === r.codeResto ? { ...r } : b));
      return of(r);
    });

    service.ajouterRestaurant.and.callFake((r: Restaurant) => {
      const ajoute = { ...r, codeResto: base.length + 1 };
      base = [...base, ajoute];
      return of(ajoute);
    });

    await TestBed.configureTestingModule({
      imports: [ListeRestaurants],
      providers: [{ provide: PlatService, useValue: service }]
    }).compileComponents();

    fixture = TestBed.createComponent(ListeRestaurants);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  function enseignesAffichees(): string[] {
    const lignes = fixture.nativeElement.querySelectorAll('tbody tr') as NodeListOf<HTMLTableRowElement>;
    return Array.from(lignes).map(tr => tr.cells[1].textContent!.trim());
  }

  it('affiche les restaurants dans l\'ordre des codes', () => {
    expect(enseignesAffichees()).toEqual(['Resto A', 'Resto B', 'Resto C']);
  });

  it('un restaurant modifié reste à sa place', () => {
    component.updateResto({ ...component.restaurants[0] });
    expect(component.ajout).toBeFalse();

    component.updatedResto.enseigneResto = 'Resto A modifié';
    component.restaurantUpdated(component.updatedResto);
    fixture.detectChanges();

    expect(service.updateRestaurant).toHaveBeenCalled();
    expect(service.ajouterRestaurant).not.toHaveBeenCalled();
    expect(enseignesAffichees()).toEqual(['Resto A modifié', 'Resto B', 'Resto C']);
    expect(component.ajout).toBeTrue();
  });

  it('ajoute un nouveau restaurant à la fin de la liste', () => {
    expect(component.ajout).toBeTrue();
    component.updatedResto.enseigneResto = 'Resto D';
    component.restaurantUpdated(component.updatedResto);
    fixture.detectChanges();

    expect(service.ajouterRestaurant).toHaveBeenCalled();
    expect(service.updateRestaurant).not.toHaveBeenCalled();
    expect(enseignesAffichees()).toEqual(['Resto A', 'Resto B', 'Resto C', 'Resto D']);
    expect(component.updatedResto.enseigneResto).toBe('');
  });
});
