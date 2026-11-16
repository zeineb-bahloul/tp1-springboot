import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { Register } from './register';
import { AuthService } from '../services/auth';

describe('Register', () => {
  let fixture: ComponentFixture<Register>;
  let component: Register;
  let auth: jasmine.SpyObj<AuthService>;
  let toastr: jasmine.SpyObj<ToastrService>;
  let router: Router;

  beforeEach(async () => {
    auth = jasmine.createSpyObj('AuthService', ['registerUser', 'setRegistredUser']);
    toastr = jasmine.createSpyObj('ToastrService', ['success']);
    await TestBed.configureTestingModule({
      imports: [Register],
      providers: [provideRouter([]), { provide: AuthService, useValue: auth }, { provide: ToastrService, useValue: toastr }]
    }).compileComponents();
    fixture = TestBed.createComponent(Register);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    fixture.detectChanges();
  });

  function remplir(pwd = '123456', confirm = '123456', email = 'sami@test.tn') {
    component.myForm.setValue({ username: 'sami', email, password: pwd, confirmPassword: confirm });
    fixture.detectChanges();
  }

  function bouton(): HTMLButtonElement {
    return fixture.nativeElement.querySelector('button.btn-success');
  }

  it('le bouton est désactivé tant que le formulaire est invalide', () => {
    expect(bouton().disabled).toBeTrue();
    remplir('123', '123');            // mot de passe trop court
    expect(bouton().disabled).toBeTrue();
    remplir('123456', '123456', 'pas-un-email');
    expect(bouton().disabled).toBeTrue();
    remplir('123456', '654321');      // confirmation différente
    expect(bouton().disabled).toBeTrue();
    remplir();
    expect(bouton().disabled).toBeFalse();
  });

  it('inscription réussie : toastr puis page de validation', () => {
    auth.registerUser.and.returnValue(of(new HttpResponse<any>({ status: 200 })));
    remplir();
    component.onRegister();
    expect(auth.registerUser).toHaveBeenCalled();
    expect(auth.setRegistredUser).toHaveBeenCalledWith(component.user);
    expect(toastr.success).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/verifEmail']);
  });

  it('email déjà existant : message d\'erreur du serveur', () => {
    auth.registerUser.and.returnValue(throwError(() => new HttpErrorResponse({
      status: 400, error: { message: 'email déjà existant!', errorCode: 'USER_EMAIL_ALREADY_EXISTS' }
    })));
    remplir();
    component.onRegister();
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('.alert-danger').textContent).toContain('email déjà existant');
    expect(component.loading).toBeFalse();
  });
});
