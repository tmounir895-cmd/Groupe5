import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';

import { API_URL } from './api';
import { AuthService } from './auth.service';

/** Ajoute "Authorization: Bearer <jeton>" à chaque appel de l'API, et déconnecte si le jeton est refusé (401). */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const verslApi = req.url.startsWith(API_URL);
  const token = auth.token;

  const requete =
    verslApi && token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(requete).pipe(
    catchError((erreur: unknown) => {
      const refuse = erreur instanceof HttpErrorResponse && erreur.status === 401;
      if (refuse && verslApi && !req.url.endsWith('/auth/login')) {
        auth.logout();
      }
      return throwError(() => erreur);
    }),
  );
};
