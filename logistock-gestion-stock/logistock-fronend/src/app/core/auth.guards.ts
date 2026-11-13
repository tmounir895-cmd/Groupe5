import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from './auth.service';
import { Role } from './models';

/** Bloque l'accès aux pages si personne n'est connecté. */
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.estConnecte() ? true : router.createUrlTree(['/login']);
};

/** Autorise une page seulement à certains rôles (à utiliser avec canActivate: [roleGuard('VENDEUR', ...)]). */
export const roleGuard = (...roles: Role[]): CanActivateFn => () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (!auth.estConnecte()) {
    return router.createUrlTree(['/login']);
  }
  return auth.aUnDesRoles(...roles) ? true : router.createUrlTree(['/accueil']);
};
