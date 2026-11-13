import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth.service';
import { CategorieService } from '../../core/categorie.service';
import { messageErreur } from '../../core/erreurs';
import { Categorie, Produit } from '../../core/models';
import { ProduitService } from '../../core/produit.service';

@Component({
  selector: 'app-produits',
  imports: [FormsModule, RouterLink],
  template: `
    <div class="conteneur">
      <div class="barre">
        <h1>Produits</h1>
        @if (peutModifier) {
          <a class="btn" routerLink="/produits/nouveau">Nouveau produit</a>
        }
      </div>

      <div class="filtres">
        <input type="search" placeholder="Rechercher par désignation ou référence"
               [ngModel]="recherche()" (ngModelChange)="changerRecherche($event)" />
        <select [ngModel]="categorieId()" (ngModelChange)="changerCategorie($event)">
          <option value="">Toutes les catégories</option>
          @for (c of categories(); track c.id) {
            <option [value]="c.id">{{ c.nom }}</option>
          }
        </select>
      </div>

      @if (erreur(); as message) {
        <p class="erreur" role="alert">{{ message }}</p>
      }

      @if (chargement()) {
        <p class="vide">Chargement…</p>
      } @else if (produits().length === 0) {
        <p class="vide">Aucun produit trouvé.</p>
      } @else {
        <div class="defilement">
          <table class="tableau">
            <thead>
              <tr>
                <th>Référence</th>
                <th>Désignation</th>
                <th>Catégorie</th>
                <th class="nombre">Prix</th>
                <th class="nombre">Stock</th>
                <th class="nombre">Seuil d'alerte</th>
                @if (peutModifier) {
                  <th></th>
                }
              </tr>
            </thead>
            <tbody>
              @for (p of produits(); track p.id) {
                <tr>
                  <td>{{ p.reference }}</td>
                  <td>{{ p.designation }}</td>
                  <td>{{ p.categorieNom }}</td>
                  <td class="nombre">{{ nombre(p.prix) }}</td>
                  <td class="nombre">
                    {{ nombre(p.quantiteStock) }}
                    @if (p.quantiteStock === 0) {
                      <span class="badge badge-rupture">Rupture</span>
                    } @else if (p.stockFaible) {
                      <span class="badge badge-alerte">Stock faible</span>
                    }
                  </td>
                  <td class="nombre">{{ nombre(p.seuilAlerte) }}</td>
                  @if (peutModifier) {
                    <td class="actions">
                      <a class="btn btn-secondaire btn-petit" [routerLink]="['/produits', p.id, 'modifier']">Modifier</a>
                      <button type="button" class="btn btn-danger btn-petit" (click)="supprimer(p)">Supprimer</button>
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
export class Produits {
  private readonly produitService = inject(ProduitService);
  private readonly categorieService = inject(CategorieService);
  private readonly auth = inject(AuthService);

  protected readonly peutModifier = this.auth.aUnDesRoles('ADMINISTRATEUR', 'GESTIONNAIRE_STOCK');

  protected readonly produits = signal<Produit[]>([]);
  protected readonly categories = signal<Categorie[]>([]);
  protected readonly recherche = signal('');
  protected readonly categorieId = signal('');
  protected readonly chargement = signal(true);
  protected readonly erreur = signal<string | null>(null);

  private minuteur: ReturnType<typeof setTimeout> | undefined;

  constructor() {
    this.categorieService.lister().subscribe({
      next: (liste) => this.categories.set(liste),
      error: (e: unknown) => this.erreur.set(messageErreur(e)),
    });
    this.charger();
  }

  protected changerRecherche(valeur: string): void {
    this.recherche.set(valeur);
    clearTimeout(this.minuteur);
    this.minuteur = setTimeout(() => this.charger(), 300); // on attend la fin de la frappe
  }

  protected changerCategorie(valeur: string): void {
    this.categorieId.set(valeur);
    this.charger();
  }

  protected supprimer(produit: Produit): void {
    if (!globalThis.confirm(`Supprimer le produit « ${produit.designation} » ?`)) {
      return;
    }
    this.erreur.set(null);
    this.produitService.supprimer(produit.id).subscribe({
      next: () => this.charger(),
      error: (e: unknown) => this.erreur.set(messageErreur(e)),
    });
  }

  protected nombre(valeur: number): string {
    return new Intl.NumberFormat('fr-FR').format(valeur);
  }

  private charger(): void {
    this.chargement.set(true);
    this.produitService.lister(this.categorieId(), this.recherche().trim()).subscribe({
      next: (liste) => {
        this.produits.set(liste);
        this.erreur.set(null);
        this.chargement.set(false);
      },
      error: (e: unknown) => {
        this.erreur.set(messageErreur(e));
        this.chargement.set(false);
      },
    });
  }
}
