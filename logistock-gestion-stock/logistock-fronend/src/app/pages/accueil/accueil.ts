import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth.service';
import { Role } from '../../core/models';

interface Module {
  titre: string;
  description: string;
  roles: Role[];
  chemin?: string; // absent = pas encore disponible
}

/** Modules de l'application et rôles qui y ont accès. */
const MODULES: Module[] = [
  { titre: 'Produits', description: 'Consulter le catalogue et la quantité en stock',
    roles: ['ADMINISTRATEUR', 'VENDEUR', 'GESTIONNAIRE_STOCK', 'RESPONSABLE_STOCK'], chemin: '/produits' },
  { titre: 'Catégories', description: 'Organiser les produits par catégorie',
    roles: ['ADMINISTRATEUR', 'VENDEUR', 'GESTIONNAIRE_STOCK', 'RESPONSABLE_STOCK'], chemin: '/categories' },
  { titre: 'Stock et mouvements', description: 'Enregistrer les entrées de marchandises et consulter les mouvements',
    roles: ['ADMINISTRATEUR', 'GESTIONNAIRE_STOCK', 'RESPONSABLE_STOCK'], chemin: '/stock' },
  { titre: 'Ventes', description: 'Enregistrer et consulter les ventes et factures',
    roles: ['ADMINISTRATEUR', 'VENDEUR', 'RESPONSABLE_STOCK'], chemin: '/ventes' },
  { titre: 'Clients', description: 'Enregistrer et retrouver les clients',
    roles: ['ADMINISTRATEUR', 'VENDEUR'], chemin: '/clients' },
  { titre: 'Tableau de bord', description: 'État du stock, alertes et mouvements',
    roles: ['ADMINISTRATEUR', 'RESPONSABLE_STOCK'], chemin: '/tableau-de-bord' },
  { titre: 'Utilisateurs', description: 'Créer les comptes et attribuer les rôles',
    roles: ['ADMINISTRATEUR'], chemin: '/utilisateurs' },
];

@Component({
  selector: 'app-accueil',
  imports: [RouterLink],
  template: `
    <main class="conteneur">
      <h1>Bienvenue</h1>
      <p class="aide">Voici les modules auxquels votre rôle donne accès.</p>
      <section class="grille">
        @for (m of modules(); track m.titre) {
          <article class="carte module">
            <h2>{{ m.titre }}</h2>
            <p>{{ m.description }}</p>
            @if (m.chemin) {
              <a class="btn btn-petit" [routerLink]="m.chemin">Ouvrir</a>
            } @else {
              <span class="bientot">Bientôt disponible</span>
            }
          </article>
        }
      </section>
    </main>
  `,
  styles: `
    h1 { margin: 0 0 .25rem; }
    .aide { color: #64748b; margin: 0 0 1.5rem; }
    .grille { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 1rem; }
    .module { margin: 0; }
    .module h2 { margin: 0 0 .25rem; font-size: 1.1rem; }
    .module p { margin: 0 0 .75rem; color: #475569; }
    .bientot { font-size: .8rem; color: #92400e; background: #fef3c7; padding: .15rem .5rem; border-radius: 999px; }
  `,
})
export class Accueil {
  private readonly auth = inject(AuthService);

  protected readonly modules = computed(() =>
    MODULES.filter((m) => this.auth.aUnDesRoles(...m.roles)),
  );
}
