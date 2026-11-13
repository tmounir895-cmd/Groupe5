import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AuthService } from '../../core/auth.service';
import { ClientService } from '../../core/client.service';
import { messageErreur } from '../../core/erreurs';
import { Client } from '../../core/models';

/** Clients : consulter, créer, modifier (administrateur et vendeur) ; supprimer (administrateur). */
@Component({
  selector: 'app-clients',
  imports: [FormsModule],
  template: `
    <div class="conteneur">
      <div class="barre">
        <h1>Clients</h1>
      </div>

      <form class="carte formulaire" (ngSubmit)="enregistrer()">
        <strong>{{ edition() ? 'Modifier le client ' + edition() : 'Nouveau client' }}</strong>

        <div class="champ">
          <label for="nom">Nom</label>
          <input id="nom" name="nom" required maxlength="45" [(ngModel)]="nom" />
        </div>
        <div class="champ">
          <label for="telephone">Téléphone</label>
          <input id="telephone" name="telephone" type="tel" required maxlength="20" [(ngModel)]="telephone" />
        </div>
        <div class="champ">
          <label for="email">Email (facultatif)</label>
          <input id="email" name="email" type="email" maxlength="100" [(ngModel)]="email" />
        </div>
        <div class="champ">
          <label for="adresse">Adresse</label>
          <input id="adresse" name="adresse" required maxlength="255" [(ngModel)]="adresse" />
        </div>

        @if (erreurFormulaire(); as message) {
          <p class="erreur" role="alert">{{ message }}</p>
        }
        <div class="boutons">
          <button type="submit" class="btn" [disabled]="enCours()">
            {{ edition() ? 'Enregistrer' : 'Ajouter' }}
          </button>
          @if (edition()) {
            <button type="button" class="btn btn-secondaire" (click)="annuler()">Annuler</button>
          }
        </div>
      </form>

      <div class="filtres">
        <input type="search" placeholder="Rechercher par nom ou téléphone"
               [ngModel]="recherche()" (ngModelChange)="changerRecherche($event)" />
      </div>

      @if (erreur(); as message) {
        <p class="erreur" role="alert">{{ message }}</p>
      }

      @if (clients().length === 0) {
        <p class="vide">Aucun client.</p>
      } @else {
        <div class="defilement">
          <table class="tableau">
            <thead>
              <tr>
                <th>Code</th>
                <th>Nom</th>
                <th>Téléphone</th>
                <th>Email</th>
                <th>Adresse</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              @for (c of clients(); track c.id) {
                <tr>
                  <td>{{ c.id }}</td>
                  <td>{{ c.nom }}</td>
                  <td>{{ c.telephone }}</td>
                  <td>{{ c.email }}</td>
                  <td>{{ c.adresse }}</td>
                  <td class="actions">
                    <button type="button" class="btn btn-secondaire btn-petit" (click)="modifier(c)">Modifier</button>
                    @if (peutSupprimer) {
                      <button type="button" class="btn btn-danger btn-petit" (click)="supprimer(c)">Supprimer</button>
                    }
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      }
    </div>
  `,
})
export class Clients {
  private readonly service = inject(ClientService);
  private readonly auth = inject(AuthService);

  protected readonly peutSupprimer = this.auth.aUnDesRoles('ADMINISTRATEUR');

  protected readonly clients = signal<Client[]>([]);
  protected readonly recherche = signal('');
  protected readonly erreur = signal<string | null>(null);

  protected readonly nom = signal('');
  protected readonly telephone = signal('');
  protected readonly email = signal('');
  protected readonly adresse = signal('');
  /** Code du client en cours de modification (null = création). */
  protected readonly edition = signal<string | null>(null);
  protected readonly enCours = signal(false);
  protected readonly erreurFormulaire = signal<string | null>(null);

  private minuteur: ReturnType<typeof setTimeout> | undefined;

  constructor() {
    this.charger();
  }

  protected changerRecherche(valeur: string): void {
    this.recherche.set(valeur);
    clearTimeout(this.minuteur);
    this.minuteur = setTimeout(() => this.charger(), 300);
  }

  protected modifier(c: Client): void {
    this.edition.set(c.id);
    this.nom.set(c.nom);
    this.telephone.set(c.telephone);
    this.email.set(c.email ?? '');
    this.adresse.set(c.adresse);
    this.erreurFormulaire.set(null);
  }

  protected annuler(): void {
    this.edition.set(null);
    this.nom.set('');
    this.telephone.set('');
    this.email.set('');
    this.adresse.set('');
    this.erreurFormulaire.set(null);
  }

  protected enregistrer(): void {
    const saisie = {
      nom: this.nom().trim(),
      telephone: this.telephone().trim(),
      email: this.email().trim() || null,
      adresse: this.adresse().trim(),
    };
    if (!saisie.nom || !saisie.telephone || !saisie.adresse) {
      this.erreurFormulaire.set('Le nom, le téléphone et l\u2019adresse sont obligatoires.');
      return;
    }
    this.erreurFormulaire.set(null);
    this.enCours.set(true);

    const id = this.edition();
    const requete = id !== null ? this.service.modifier(id, saisie) : this.service.creer(saisie);
    requete.subscribe({
      next: () => {
        this.enCours.set(false);
        this.annuler();
        this.charger();
      },
      error: (e: unknown) => {
        this.enCours.set(false);
        this.erreurFormulaire.set(messageErreur(e));
      },
    });
  }

  protected supprimer(c: Client): void {
    if (!globalThis.confirm(`Supprimer le client « ${c.nom} » ?`)) {
      return;
    }
    this.erreur.set(null);
    this.service.supprimer(c.id).subscribe({
      next: () => this.charger(),
      error: (e: unknown) => this.erreur.set(messageErreur(e)),
    });
  }

  private charger(): void {
    this.service.lister(this.recherche().trim()).subscribe({
      next: (liste) => {
        this.clients.set(liste);
        this.erreur.set(null);
      },
      error: (e: unknown) => this.erreur.set(messageErreur(e)),
    });
  }
}
