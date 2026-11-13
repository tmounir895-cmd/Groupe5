import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { AuthService } from '../core/auth.service';
import { Role } from '../core/models';

const LIBELLES: Record<Role, string> = {
  ADMINISTRATEUR: 'Administrateur',
  VENDEUR: 'Vendeur',
  GESTIONNAIRE_STOCK: 'Gestionnaire de stock',
  RESPONSABLE_STOCK: 'Responsable de stock',
};

/** Cadre commun des pages connectées : en-tête, menu et nom de l'utilisateur. */
@Component({
  selector: 'app-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <header class="entete">
      <strong class="marque">LogiStock SA</strong>
      <nav class="menu">
        @for (lien of liens(); track lien.chemin) {
          <a [routerLink]="lien.chemin" routerLinkActive="actif">{{ lien.libelle }}</a>
        }
      </nav>
      @if (auth.utilisateur(); as u) {
        <span class="profil">
          {{ u.prenom }} {{ u.nom }} · {{ libelle(u.role) }}
          <button type="button" (click)="auth.logout()">Se déconnecter</button>
        </span>
      }
    </header>
    <router-outlet />
  `,
  styles: `
    .entete { display: flex; flex-wrap: wrap; align-items: center; gap: 1rem; padding: .75rem 1.25rem;
              background: #0f172a; color: #fff; }
    .marque { margin-right: .5rem; }
    .menu { display: flex; gap: .25rem; flex: 1; }
    .menu a { color: #cbd5e1; text-decoration: none; padding: .35rem .7rem; border-radius: 6px; }
    .menu a:hover { background: #1e293b; }
    .menu a.actif { background: #2563eb; color: #fff; }
    .profil { display: flex; align-items: center; gap: .75rem; font-size: .9rem; }
    .profil button { padding: .35rem .7rem; border: 1px solid #94a3b8; border-radius: 6px;
                     background: transparent; color: #fff; cursor: pointer; }
  `,
})
export class Shell {
  protected readonly auth = inject(AuthService);

  /** Pages du menu et rôles qui y ont accès (d'autres s'ajouteront aux étapes suivantes). */
  private readonly tous: { chemin: string; libelle: string; roles?: Role[] }[] = [
    { chemin: '/accueil', libelle: 'Accueil' },
    { chemin: '/produits', libelle: 'Produits' },
    { chemin: '/categories', libelle: 'Catégories' },
    { chemin: '/stock', libelle: 'Stock', roles: ['ADMINISTRATEUR', 'GESTIONNAIRE_STOCK', 'RESPONSABLE_STOCK'] },
    { chemin: '/ventes', libelle: 'Ventes', roles: ['ADMINISTRATEUR', 'VENDEUR', 'RESPONSABLE_STOCK'] },
    { chemin: '/clients', libelle: 'Clients', roles: ['ADMINISTRATEUR', 'VENDEUR'] },
    { chemin: '/tableau-de-bord', libelle: 'Tableau de bord', roles: ['ADMINISTRATEUR', 'RESPONSABLE_STOCK'] },
    { chemin: '/utilisateurs', libelle: 'Utilisateurs', roles: ['ADMINISTRATEUR'] },
  ];

  protected readonly liens = computed(() =>
    this.tous.filter((l) => !l.roles || this.auth.aUnDesRoles(...l.roles)),
  );

  protected libelle(role: Role): string {
    return LIBELLES[role];
  }
}
