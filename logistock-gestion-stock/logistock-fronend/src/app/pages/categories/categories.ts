import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AuthService } from '../../core/auth.service';
import { CategorieService } from '../../core/categorie.service';
import { messageErreur } from '../../core/erreurs';
import { Categorie } from '../../core/models';

@Component({
  selector: 'app-categories',
  imports: [FormsModule],
  template: `
    <div class="conteneur">
      <div class="barre">
        <h1>Catégories</h1>
      </div>

      @if (peutModifier) {
        <form class="carte formulaire" (ngSubmit)="enregistrer()">
          <strong>{{ edition() ? 'Modifier la catégorie ' + edition() : 'Nouvelle catégorie' }}</strong>

          <div class="champ">
            <label for="code">Code (5 caractères max)</label>
            <input id="code" name="code" required maxlength="5" [disabled]="edition() !== null" [(ngModel)]="code" />
          </div>
          <div class="champ">
            <label for="nom">Nom</label>
            <input id="nom" name="nom" required maxlength="45" [(ngModel)]="nom" />
          </div>
          <div class="champ">
            <label for="description">Description</label>
            <input id="description" name="description" maxlength="255" [(ngModel)]="description" />
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
      }

      @if (erreur(); as message) {
        <p class="erreur" role="alert">{{ message }}</p>
      }

      @if (categories().length === 0) {
        <p class="vide">Aucune catégorie.</p>
      } @else {
        <div class="defilement">
          <table class="tableau">
            <thead>
              <tr>
                <th>Code</th>
                <th>Nom</th>
                <th>Description</th>
                @if (peutModifier) {
                  <th></th>
                }
              </tr>
            </thead>
            <tbody>
              @for (c of categories(); track c.id) {
                <tr>
                  <td>{{ c.id }}</td>
                  <td>{{ c.nom }}</td>
                  <td>{{ c.description }}</td>
                  @if (peutModifier) {
                    <td class="actions">
                      <button type="button" class="btn btn-secondaire btn-petit" (click)="modifier(c)">Modifier</button>
                      <button type="button" class="btn btn-danger btn-petit" (click)="supprimer(c)">Supprimer</button>
                    </td>
                  }
                </tr>
              }
            </tbody>
          </table>
        </div>
      }
    </div>
  `,
})
export class Categories {
  private readonly categorieService = inject(CategorieService);
  private readonly auth = inject(AuthService);

  protected readonly peutModifier = this.auth.aUnDesRoles('ADMINISTRATEUR', 'GESTIONNAIRE_STOCK');

  protected readonly categories = signal<Categorie[]>([]);
  protected readonly code = signal('');
  protected readonly nom = signal('');
  protected readonly description = signal('');
  /** Code de la catégorie en cours de modification (null = création). */
  protected readonly edition = signal<string | null>(null);
  protected readonly enCours = signal(false);
  protected readonly erreur = signal<string | null>(null);
  protected readonly erreurFormulaire = signal<string | null>(null);

  constructor() {
    this.charger();
  }

  protected modifier(categorie: Categorie): void {
    this.edition.set(categorie.id);
    this.code.set(categorie.id);
    this.nom.set(categorie.nom);
    this.description.set(categorie.description ?? '');
    this.erreurFormulaire.set(null);
  }

  protected annuler(): void {
    this.edition.set(null);
    this.code.set('');
    this.nom.set('');
    this.description.set('');
    this.erreurFormulaire.set(null);
  }

  protected enregistrer(): void {
    const nom = this.nom().trim();
    const code = this.code().trim();
    if (!nom || (this.edition() === null && !code)) {
      this.erreurFormulaire.set('Le code et le nom sont obligatoires.');
      return;
    }
    const description = this.description().trim() || null;

    this.erreurFormulaire.set(null);
    this.enCours.set(true);

    const idEdite = this.edition();
    const requete = idEdite !== null
      ? this.categorieService.modifier(idEdite, { nom, description })
      : this.categorieService.creer({ id: code, nom, description });

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

  protected supprimer(categorie: Categorie): void {
    if (!globalThis.confirm(`Supprimer la catégorie « ${categorie.nom} » ?`)) {
      return;
    }
    this.erreur.set(null);
    this.categorieService.supprimer(categorie.id).subscribe({
      next: () => this.charger(),
      error: (e: unknown) => this.erreur.set(messageErreur(e)),
    });
  }

  private charger(): void {
    this.categorieService.lister().subscribe({
      next: (liste) => this.categories.set(liste),
      error: (e: unknown) => this.erreur.set(messageErreur(e)),
    });
  }
}
