import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';
import { AuthService } from './auth';
import { fauxJwt } from './jwt-test-utils';

describe('AuthService (JWT)', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.removeItem('jwt');
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.removeItem('jwt');
  });

  it('login() fait un POST sur http://localhost:8081/users/login', () => {
    service.login({ username: 'admin', password: '123' } as any).subscribe(res => {
      expect(res.headers.get('Authorization')).toBe('xxx.yyy.zzz');
    });
    const req = httpMock.expectOne('http://localhost:8081/users/login');
    expect(req.request.method).toBe('POST');
    req.flush(null, { headers: { Authorization: 'xxx.yyy.zzz' } });
  });

  it('saveToken() enregistre le jwt et décode le nom et les rôles', () => {
    service.saveToken(fauxJwt('admin', ['ADMIN', 'USER']));
    expect(localStorage.getItem('jwt')).toBeTruthy();
    expect(service.loggedUser).toBe('admin');
    expect(service.isloggedIn).toBeTrue();
    expect(service.isAdmin()).toBeTrue();
    expect(service.isTokenExpired()).toBeFalse();
  });

  it('un USER n\'est pas admin', () => {
    service.saveToken(fauxJwt('zeineb', ['USER']));
    expect(service.isAdmin()).toBeFalse();
  });

  it('loadToken() relit le jwt du localStorage', () => {
    localStorage.setItem('jwt', fauxJwt('zeineb', ['USER']));
    service.loadToken();
    expect(service.loggedUser).toBe('zeineb');
    expect(service.roles).toEqual(['USER']);
  });

  it('isTokenExpired() : vrai sans token ou avec un token expiré', () => {
    expect(service.isTokenExpired()).toBeTrue();
    service.saveToken(fauxJwt('admin', ['ADMIN'], -60));
    expect(service.isTokenExpired()).toBeTrue();
  });

  it('logout() efface tout et revient au login', () => {
    const router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    service.saveToken(fauxJwt('admin', ['ADMIN']));
    service.logout();
    expect(localStorage.getItem('jwt')).toBeNull();
    expect(service.isloggedIn).toBeFalse();
    expect(service.isAdmin()).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('registerUser() et validateEmail() appellent le microservice users', () => {
    service.registerUser({ username: 'sami', email: 's@t.tn', password: '123456' } as any).subscribe();
    const reg = httpMock.expectOne('http://localhost:8081/users/register');
    expect(reg.request.method).toBe('POST');
    reg.flush({});
    service.validateEmail('123456').subscribe();
    httpMock.expectOne('http://localhost:8081/users/verifyEmail/123456').flush({});
  });
});
