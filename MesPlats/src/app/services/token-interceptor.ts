import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth';

// Atelier 17 : ajoute "Authorization: Bearer <jwt>" à toutes les requêtes
// sauf login / register / verifyEmail (on n'a pas encore de token)
const exclude_array: string[] = ['/login', '/register', '/verifyEmail'];

function toExclude(url: string) {
  const length = exclude_array.length;
  for (let i = 0; i < length; i++) {
    if (url.search(exclude_array[i]) != -1)
      return true;
  }
  return false;
}

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  if (!toExclude(req.url)) {
    const jwt = authService.getToken();
    if (jwt) {
      const reqWithToken = req.clone({
        setHeaders: { Authorization: 'Bearer ' + jwt }
      });
      return next(reqWithToken);
    }
  }
  return next(req);
};
