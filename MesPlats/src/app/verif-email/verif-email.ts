import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from '../model/user.model';
import { AuthService } from '../services/auth';

@Component({
  selector: 'app-verif-email',
  imports: [FormsModule],
  templateUrl: './verif-email.html',
  styles: ``
})
export class VerifEmail implements OnInit {
  code: string = '';
  user: User = new User();
  err = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.user = this.authService.regitredUser;
  }

  onValidateEmail() {
    this.authService.validateEmail(this.code).subscribe({
      next: (res) => {
        alert('Login successful');
        // email validé : on connecte directement l'utilisateur
        this.authService.login(this.user).subscribe({
          next: (data) => {
            const jwToken = data.headers.get('Authorization')!;
            this.authService.saveToken(jwToken);
            this.router.navigate(['/']);
          },
          error: (err: any) => {
            console.log(err);
          }
        });
      },
      error: (err: any) => {
        if (err.error?.errorCode == 'INVALID_TOKEN')
          this.err = 'Code invalide!';
        else if (err.error?.errorCode == 'EXPIRED_TOKEN')
          this.err = 'Code a expiré!';
        else
          this.err = err.error?.message ?? 'Erreur de validation';
        console.log(err.error?.errorCode);
      }
    });
  }
}
