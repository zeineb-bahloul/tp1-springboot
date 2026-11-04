import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { Plats } from './plats';

describe('Plats', () => {
  let component: Plats;
  let fixture: ComponentFixture<Plats>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Plats],
      providers: [provideHttpClient(), provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Plats);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
