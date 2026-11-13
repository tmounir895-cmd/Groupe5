import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AuthService } from '../../core/auth.service';
import { messageErreur } from '../../core/erreurs';
import { Role, Utilisateur } from '../../core/models';
import { UtilisateurService } from '../../core/utilisateur.service';

const ROLES: { valeur: Role; libelle: string }[] = [
  { valeur: 'ADMINISTRATEUR', libelle: 'Administrateur' },
  { valeur: 'VENDEUR', libelle: 'Vendeur' },
  { valeur: 'GESTIONNAIRE_STOCK', libelle: 'Gestionnaire de stock' },
  { valeur: 'RESPONSABLE_STOCK', libelle: 'Responsable de stock' },
];

/** Création des comptes et attribution des rôles (administrateur). */
@Component({
  selector: 'app-utilisateurs',
  imports: [FormsModule],
  template: `
    <div class="conteneur">
      <div class="barre">
        <h1>Utilisateurs</h1>
      </div>

      <form class="carte formulaire" (ngSubmit)="creer()">
        <strong>Nouveau compte</strong>

        <div class="champ">
          <label for="nom">Nom</label>
          <input id="nom" name="nom" required maxlength="45" [(ngModel)]="nom" />
        </div>
        <div class="champ">
          <label for="prenom">Prénom</label>
          <input id="prenom" name="prenom" maxlength="45" [(ngModel)]="prenom" />
        </div>
        <div class="champ">
          <label for="email">Email</label>
          <input id="email" name="email" type="email" required maxlength="100" autocomplete="off" [(ngModel)]="email" />
        </div>
        <div class="champ">
          <label for="mdp">Mot de passe</label>
          <input id="mdp" name="mdp" type="password" required minlength="8" maxlength="72"
                 autocomplete="new-password" [(ngModel)]="motDePasse" />
          <small>8 caractères minimum.</small>
        </div>
        <div class="champ">
          <label for="role">Rôle</label>
          <select id="role" name="role" required [(ngModel)]="role">
            <option value="">Choisir un rôle</option>
            @for (r of roles; track r.valeur) {
              <option [value]="r.valeur">{{ r.libelle }}</option>
            }
          </select>
        </div>

        @if (erreurFormulaire(); as message) {
          <p class="erreur" role="alert">{{ message }}</p>
        }
        <div class="boutons">
          <button type="submit" class="btn" [disabled]="enCours()">
            {{ enCours() ? 'Création…' : 'Créer le compte' }}
          </button>
        </div>
      </form>

      @if (succes(); as message) {
        <p class="succes" role="status">{{ message }}</p>
      }
      @if (erreur(); as message) {
        <p class="erreur" role="alert">{{ message }}</p>
      }

      <div class="defilement">
        <table class="tableau">
          <thead>
            <tr>
              <th>Nom</th>
              <th>Email</th>
              <th>Rôle</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            @for (u of utilisateurs(); track u.id) {
              <tr>
                <td>{{ u.prenom }} {{ u.nom }}</td>
                <td>{{ u.email }}</td>
                <td>
                  <select aria-label="Rôle de {{ u.nom }}" [disabled]="estMoi(u)" (change)="changerRole(u, $event)">
                    @for (r of roles; track r.valeur) {
                      <option [value]="r.valeur" [selected]="r.valeur === u.role">{{ r.libelle }}</option>
                    }
                  </select>
                </td>
                <td class="actions">
                  <button type="button" class="btn btn-secondaire btn-petit" (click)="ouvrirMotDePasse(u.id)">Mot de passe</button>
                  @if (!estMoi(u)) {
                    <button type="button" class="btn btn-danger btn-petit" (click)="supprimer(u)">Supprimer</button>
                  }
                </td>
              </tr>
              @if (idMotDePasse() === u.id) {
                <tr class="detail">
                  <td colspan="4">
                    <div class="mdp">
                      <input type="password" placeholder="Nouveau mot de passe (8 caractères min.)"
                             autocomplete="new-password" [ngModel]="nouveauMotDePasse()"
                             (ngModelChange)="nouveauMotDePasse.set($event)" />
                      <button type="button" class="btn btn-petit" (click)="reinitialiser(u)">Valider</button>
                      <button type="button" class="btn btn-secondaire btn-petit" (click)="idMotDePasse.set(null)">Annuler</button>
                    </div>
                  </td>
                </tr>
              }
            }
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: `
    .succes { padding: .6rem .75rem; border-radius: 8px; background: #dcfce7; color: #166534; margin: 0 0 1rem; }
    .detail td { background: #f8fafc; }
    .mdp { display: flex; flex-wrap: wrap; gap: .5rem; }
    .mdp input { flex: 1 1 260px; }
  `,
})
export class Utilisateurs {
  private readonly service = inject(UtilisateurService);
  private readonly auth = inject(AuthService);

  protected readonly roles = ROLES;
  protected readonly utilisateurs = signal<Utilisateur[]>([]);

  protected readonly nom = signal('');
  protected readonly prenom = signal('');
  protected readonly email = signal('');
  protected readonly motDePasse = signal('');
  protected readonly role = signal('');

  protected readonly enCours = signal(false);
  protected readonly erreurFormulaire = signal<string | null>(null);
  protected readonly erreur = signal<string | null>(null);
  protected readonly succes = signal<string | null>(null);

  protected readonly idMotDePasse = signal<number | null>(null);
  protected readonly nouveauMotDePasse = signal('');

  constructor() {
    this.charger();
  }

  protected estMoi(u: Utilisateur): boolean {
    return u.id === this.auth.utilisateur()?.id;
  }

  protected creer(): void {
    const role = this.role() as Role | '';
    if (!role) {
      this.erreurFormulaire.set('Choisissez un rôle.');
      return;
    }
    this.erreurFormulaire.set(null);
    this.succes.set(null);
    this.enCours.set(true);

    this.service
      .creer({
        nom: this.nom().trim(),
        prenom: this.prenom().trim() || null,
        email: this.email().trim(),
        motDePasse: this.motDePasse(),
        role,
      })
      .subscribe({
        next: (u) => {
          this.enCours.set(false);
          this.succes.set(`Compte créé pour ${u.email}.`);
          this.nom.set('');
          this.prenom.set('');
          this.email.set('');
          this.motDePasse.set('');
          this.role.set('');
          this.charger();
        },
        error: (e: unknown) => {
          this.enCours.set(false);
          this.erreurFormulaire.set(messageErreur(e));
        },
      });
  }

  protected changerRole(u: Utilisateur, evenement: Event): void {
    const liste = evenement.target as HTMLSelectElement;
    this.erreur.set(null);
    this.succes.set(null);
    this.service.modifierRole(u.id, liste.value as Role).subscribe({
      next: (maj) => {
        this.utilisateurs.update((l) => l.map((x) => (x.id === maj.id ? maj : x)));
        this.succes.set(`Le rôle de ${maj.email} est maintenant « ${this.libelle(maj.role)} ». Il s'appliquera à sa prochaine connexion.`);
      },
      error: (e: unknown) => {
        liste.value = u.role; // on remet l'ancien rôle à l'écran
        this.erreur.set(messageErreur(e));
      },
    });
  }

  protected ouvrirMotDePasse(id: number): void {
    this.nouveauMotDePasse.set('');
    this.idMotDePasse.set(this.idMotDePasse() === id ? null : id);
  }

  protected reinitialiser(u: Utilisateur): void {
    this.erreur.set(null);
    this.succes.set(null);
    this.service.reinitialiserMotDePasse(u.id, this.nouveauMotDePasse()).subscribe({
      next: () => {
        this.idMotDePasse.set(null);
        this.succes.set(`Nouveau mot de passe enregistré pour ${u.email}.`);
      },
      error: (e: unknown) => this.erreur.set(messageErreur(e)),
    });
  }

  protected supprimer(u: Utilisateur): void {
    if (!globalThis.confirm(`Supprimer le compte de ${u.email} ?`)) {
      return;
    }
    this.erreur.set(null);
    this.succes.set(null);
    this.service.supprimer(u.id).subscribe({
      next: () => this.charger(),
      error: (e: unknown) => this.erreur.set(messageErreur(e)),
    });
  }

  private libelle(role: Role): string {
    return ROLES.find((r) => r.valeur === role)?.libelle ?? role;
  }

  private charger(): void {
    this.service.lister().subscribe({
      next: (liste) => this.utilisateurs.set(liste),
      error: (e: unknown) => this.erreur.set(messageErreur(e)),
    });
  }
}
