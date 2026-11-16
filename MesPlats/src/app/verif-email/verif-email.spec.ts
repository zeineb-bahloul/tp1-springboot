import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { VerifEmail } from './verif-email';
import { AuthService } from '../services/auth';

describe('VerifEmail', () => {
  let fixture: ComponentFixture<VerifEmail>;
  let component: VerifEmail;
  let auth: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    auth = jasmine.createSpyObj('AuthService', ['validateEmail', 'login', 'saveToken'],
      { regitredUser: { username: 'sami', password: '123456' } });
    await TestBed.configureTestingModule({
      imports: [VerifEmail],
      providers: [provideRouter([]), { provide: AuthService, useValue: auth }]
    }).compileComponents();
    fixture = TestBed.createComponent(VerifEmail);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    spyOn(window, 'alert');
    fixture.detectChanges();
  });

  it('bon code : valide l\'email puis connecte l\'utilisateur', () => {
    auth.validateEmail.and.returnValue(of({} as any));
    auth.login.and.returnValue(of(new HttpResponse<any>({ headers: new HttpHeaders({ Authorization: 'jwt.sami' }) })));
    component.code = '123456';
    component.onValidateEmail();
    expect(auth.validateEmail).toHaveBeenCalledWith('123456');
    expect(auth.login).toHaveBeenCalledWith(jasmine.objectContaining({ username: 'sami' }));
    expect(auth.saveToken).toHaveBeenCalledWith('jwt.sami');
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('mauvais code : "Code invalide!"', () => {
    auth.validateEmail.and.returnValue(throwError(() => new HttpErrorResponse({
      status: 404, error: { errorCode: 'INVALID_TOKEN' } })));
    component.onValidateEmail();
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('.alert-danger').textContent).toContain('Code invalide!');
  });

  it('code expiré : "Code a expiré!"', () => {
    auth.validateEmail.and.returnValue(throwError(() => new HttpErrorResponse({
      status: 400, error: { errorCode: 'EXPIRED_TOKEN' } })));
    component.onValidateEmail();
    expect(component.err).toBe('Code a expiré!');
  });
});
