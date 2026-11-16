import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { of } from 'rxjs';

import { Plats } from './plats';
import { PlatService } from '../services/plat';
import { Plat } from '../model/plat.model';

describe('Plats', () => {
  let component: Plats;
  let fixture: ComponentFixture<Plats>;

  const plats: Plat[] = [
    // comme PostgreSQL sans ORDER BY : le plat modifié (n°1) arrive en dernier
    { referencePlat: 2, intitulePlat: 'Lablabi', tarifPlat: 5 },
    { referencePlat: 3, intitulePlat: 'Ojja', tarifPlat: 9 },
    { referencePlat: 1, intitulePlat: 'Couscous modifié', tarifPlat: 20 }
  ];

  beforeEach(async () => {
    const service = jasmine.createSpyObj('PlatService', ['listePlats', 'supprimerPlat']);
    service.listePlats.and.returnValue(of(plats));

    await TestBed.configureTestingModule({
      imports: [Plats],
      providers: [provideRouter([]), provideHttpClient(), { provide: PlatService, useValue: service }]
    }).compileComponents();

    fixture = TestBed.createComponent(Plats);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('un plat modifié reste à sa place même si l\'API le renvoie en dernier', () => {
    const refs = Array.from(fixture.nativeElement.querySelectorAll('tbody tr') as NodeListOf<HTMLTableRowElement>)
      .filter(tr => tr.cells.length > 1)
      .map(tr => Number(tr.cells[1].textContent));
    expect(refs).toEqual([1, 2, 3]);
  });
});
