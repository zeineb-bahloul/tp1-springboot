import { Component, OnInit, signal } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from './services/auth';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('MesPlats');

  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  // Atelier 16bis : afficher la page login seulement si l'utilisateur n'est pas connecté
  ngOnInit(): void {
    this.authService.loadToken();
    // les pages d'inscription restent accessibles sans être connecté
    const page = window.location.pathname;
    if (page.startsWith('/register') || page.startsWith('/verifEmail'))
      return;
    if (this.authService.getToken() == null || this.authService.isTokenExpired())
      this.router.navigate(['/login']);
  }

  onLogout(): void {
    this.authService.logout();
  }
}
