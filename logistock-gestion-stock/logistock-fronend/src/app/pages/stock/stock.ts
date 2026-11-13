import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AuthService } from '../../core/auth.service';
import { messageErreur } from '../../core/erreurs';
import { Mouvement, Produit } from '../../core/models';
import { ProduitService } from '../../core/produit.service';
import { StockService } from '../../core/stock.service';

/** Entrées de marchandises (gestionnaire de stock) et historique des mouvements. */
@Component({
  selector: 'app-stock',
  imports: [FormsModule],
  template: `
    <div class="conteneur">
      <div class="barre">
        <h1>Stock et mouvements</h1>
      </div>

      @if (peutEnregistrer) {
        <div class="carte formulaire">
          <strong>Enregistrer une entrée de marchandises</strong>

          <div class="champ">
            <label for="produit">Produit</label>
            <select id="produit" [ngModel]="produitEntree()" (ngModelChange)="produitEntree.set($event)">
              <option [ngValue]="null">Choisir un produit</option>
              @for (p of produits(); track p.id) {
                <option [ngValue]="p.id">{{ p.designation }} (stock : {{ p.quantiteStock }})</option>
              }
            </select>
          </div>

          <div class="champ">
            <label for="quantite">Quantité reçue</label>
            <input id="quantite" type="number" min="1" step="1"
                   [ngModel]="quantiteEntree()" (ngModelChange)="quantiteEntree.set($event)" />
          </div>

          @if (succes(); as message) {
            <p class="succes" role="status">{{ message }}</p>
          }
          @if (erreurEntree(); as message) {
            <p class="erreur" role="alert">{{ message }}</p>
          }

          <div class="boutons">
            <button type="button" class="btn" [disabled]="enCours()" (click)="enregistrerEntree()">
              {{ enCours() ? 'Enregistrement…' : 'Enregistrer l’entrée' }}
            </button>
          </div>
        </div>
      }

      <div class="filtres">
        <select [ngModel]="filtre()" (ngModelChange)="changerFiltre($event)">
          <option [ngValue]="null">Tous les produits</option>
          @for (p of produits(); track p.id) {
            <option [ngValue]="p.id">{{ p.designation }}</option>
          }
        </select>
      </div>

      @if (erreur(); as message) {
        <p class="erreur" role="alert">{{ message }}</p>
      }

      @if (mouvements().length === 0) {
        <p class="vide">Aucun mouvement.</p>
      } @else {
        <div class="defilement">
          <table class="tableau">
            <thead>
              <tr>
                <th>Date</th>
                <th>Type</th>
                <th>Produit</th>
                <th class="nombre">Quantité</th>
                <th class="nombre">Stock actuel</th>
              </tr>
            </thead>
            <tbody>
              @for (m of mouvements(); track m.id) {
                <tr>
                  <td>{{ date(m.date) }}</td>
                  <td>
                    @if (estEntree(m)) {
                      <span class="badge badge-entree">Entrée</span>
                    } @else {
                      <span class="badge badge-sortie">Sortie</span>
                    }
                  </td>
                  <td>{{ m.designation }}</td>
                  <td class="nombre">{{ m.quantite }}</td>
                  <td class="nombre">{{ m.stockActuel }}</td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      }
    </div>
  `,
  styles: `
    .succes { padding: .6rem .75rem; border-radius: 8px; background: #dcfce7; color: #166534; margin: 0; }
    .badge-entree { background: #dcfce7; color: #166534; margin-left: 0; }
    .badge-sortie { background: #e0e7ff; color: #3730a3; margin-left: 0; }
  `,
})
export class Stock {
  private readonly stockService = inject(StockService);
  private readonly produitService = inject(ProduitService);
  private readonly auth = inject(AuthService);

  protected readonly peutEnregistrer = this.auth.aUnDesRoles('ADMINISTRATEUR', 'GESTIONNAIRE_STOCK');

  protected readonly produits = signal<Produit[]>([]);
  protected readonly mouvements = signal<Mouvement[]>([]);
  protected readonly filtre = signal<number | null>(null);

  protected readonly produitEntree = signal<number | null>(null);
  protected readonly quantiteEntree = signal<number | null>(null);
  protected readonly enCours = signal(false);
  protected readonly succes = signal<string | null>(null);
  protected readonly erreurEntree = signal<string | null>(null);
  protected readonly erreur = signal<string | null>(null);

  constructor() {
    this.chargerProduits();
    this.chargerMouvements();
  }

  protected changerFiltre(produitId: number | null): void {
    this.filtre.set(produitId);
    this.chargerMouvements();
  }

  protected enregistrerEntree(): void {
    const produitId = this.produitEntree();
    const quantite = this.quantiteEntree();
    this.succes.set(null);
    if (produitId === null || quantite === null || quantite < 1) {
      this.erreurEntree.set('Choisissez un produit et une quantité supérieure à 0.');
      return;
    }
    this.erreurEntree.set(null);
    this.enCours.set(true);

    this.stockService.enregistrerEntree({ produitId, quantite }).subscribe({
      next: (m) => {
        this.enCours.set(false);
        this.succes.set(`Entrée enregistrée : +${m.quantite} « ${m.designation} » (stock actuel : ${m.stockActuel}).`);
        this.quantiteEntree.set(null);
        this.chargerProduits();
        this.chargerMouvements();
      },
      error: (e: unknown) => {
        this.enCours.set(false);
        this.erreurEntree.set(messageErreur(e));
      },
    });
  }

  /** Les anciens mouvements portent le type « entrée », les nouveaux « ENTREE ». */
  protected estEntree(m: Mouvement): boolean {
    return m.type.toLowerCase().startsWith('entr');
  }

  protected date(valeur: string): string {
    return new Date(valeur).toLocaleString('fr-FR', { dateStyle: 'short', timeStyle: 'short' });
  }

  private chargerProduits(): void {
    this.produitService.lister('', '').subscribe({
      next: (liste) => this.produits.set(liste),
      error: (e: unknown) => this.erreur.set(messageErreur(e)),
    });
  }

  private chargerMouvements(): void {
    this.stockService.mouvements(this.filtre()).subscribe({
      next: (liste) => {
        this.mouvements.set(liste);
        this.erreur.set(null);
      },
      error: (e: unknown) => this.erreur.set(messageErreur(e)),
    });
  }
}
