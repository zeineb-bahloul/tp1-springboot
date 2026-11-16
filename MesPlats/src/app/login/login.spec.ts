import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Login } from './login';
import { AuthService } from '../services/auth';

describe('Login', () => {
  let fixture: ComponentFixture<Login>;
  let component: Login;
  let auth: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    auth = jasmine.createSpyObj('AuthService', ['login', 'saveToken']);
    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [provideRouter([]), { provide: AuthService, useValue: auth }]
    }).compileComponents();
    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    fixture.detectChanges();
  });

  it('enregistre le token reçu dans le header Authorization', () => {
    auth.login.and.returnValue(of(new HttpResponse<any>({ headers: new HttpHeaders({ Authorization: 'le.jwt.ici' }) })));
    component.onLoggedin();
    expect(auth.saveToken).toHaveBeenCalledWith('le.jwt.ici');
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('affiche "login ou mot de passe erronés" si le login échoue', () => {
    auth.login.and.returnValue(throwError(() => new HttpErrorResponse({ status: 401 })));
    component.onLoggedin();
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('.alert-danger').textContent).toContain('erronés');
  });

  it('affiche "Utilisateur désactivé" si l\'email n\'est pas validé', () => {
    auth.login.and.returnValue(throwError(() => new HttpErrorResponse({
      status: 403, error: { errorCause: 'disabled', message: "L'utilisateur est désactivé !" }
    })));
    component.onLoggedin();
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('.alert-danger').textContent).toContain('désactivé');
  });

  it('propose un lien vers l\'inscription', () => {
    expect(fixture.nativeElement.querySelector('a[href="/register"]')).not.toBeNull();
  });
});
