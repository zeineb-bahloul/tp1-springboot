import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { tokenInterceptor } from './token-interceptor';
import { AuthService } from './auth';
import { fauxJwt } from './jwt-test-utils';

describe('tokenInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let auth: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([tokenInterceptor])),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    });
    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    auth = TestBed.inject(AuthService);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.removeItem('jwt');
  });

  it('ajoute "Authorization: Bearer <jwt>" aux appels de l\'api', () => {
    const jwt = fauxJwt('admin', ['ADMIN']);
    auth.saveToken(jwt);
    http.get('http://localhost:8082/restaurant/api/all').subscribe();
    const req = httpMock.expectOne('http://localhost:8082/restaurant/api/all');
    expect(req.request.headers.get('Authorization')).toBe('Bearer ' + jwt);
    req.flush([]);
  });

  it('n\'ajoute rien pour /login, /register et /verifyEmail', () => {
    auth.saveToken(fauxJwt('admin', ['ADMIN']));
    for (const url of ['http://localhost:8081/users/login', 'http://localhost:8081/users/register',
                       'http://localhost:8081/users/verifyEmail/123456']) {
      http.post(url, {}).subscribe();
      const req = httpMock.expectOne(url);
      expect(req.request.headers.has('Authorization')).toBeFalse();
      req.flush({});
    }
  });

  it('n\'ajoute pas "Bearer undefined" quand on n\'est pas connecté', () => {
    http.get('http://localhost:8082/restaurant/api/all').subscribe();
    const req = httpMock.expectOne('http://localhost:8082/restaurant/api/all');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush([]);
  });
});
