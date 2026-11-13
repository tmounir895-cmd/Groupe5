import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { ClientService } from '../../core/client.service';
import { messageErreur } from '../../core/erreurs';
import { Client, Produit, Vente } from '../../core/models';
import { ProduitService } from '../../core/produit.service';
import { VenteService } from '../../core/vente.service';

interface LigneSaisie {
  produitId: number | null;
  quantite: number | null;
}

/** Enregistrement d'une vente : plusieurs lignes (produit + quantité), total calculé, facture créée. */
@Component({
  selector: 'app-vente-form',
  imports: [FormsModule, RouterLink],
  template: `
    <div class="conteneur">
      <div class="barre">
        <h1>Nouvelle vente</h1>
      </div>

      @if (resultat(); as v) {
        <div class="carte">
          <p class="succes" role="status">Vente enregistrée. Facture <strong>{{ v.factureNumero }}</strong></p>
          @if (v.clientNom) {
            <p>Client : {{ v.clientNom }}</p>
          }
          @for (l of v.lignes; track l.produitId) {
            <div>{{ l.designation }} : {{ l.quantite }} × {{ nombre(l.prixUnitaire) }} = {{ nombre(l.sousTotal) }}</div>
          }
          <p class="total">Total : {{ nombre(v.total) }}</p>

          @if (v.alertesStockFaible.length > 0) {
            <div class="alerte" role="alert">
              <strong>Stock faible après cette vente :</strong>
              @for (a of v.alertesStockFaible; track a) {
                <div>{{ a }}</div>
              }
            </div>
          }

          <div class="boutons">
            <button type="button" class="btn" (click)="nouvelleVente()">Nouvelle vente</button>
            <a class="btn btn-secondaire" routerLink="/ventes">Voir les ventes</a>
          </div>
        </div>
      } @else {
        <div class="carte">
          <div class="champ client">
            <label for="client">Client (facultatif)</label>
            <select id="client" [(ngModel)]="clientId">
              <option [ngValue]="null">Sans client</option>
              @for (c of clients(); track c.id) {
                <option [ngValue]="c.id">{{ c.nom }} — {{ c.telephone }}</option>
              }
            </select>
          </div>

          @for (ligne of lignes(); track ligne; let i = $index) {
            <div class="ligne">
              <select [(ngModel)]="ligne.produitId">
                <option [ngValue]="null">Choisir un produit</option>
                @for (p of produits(); track p.id) {
                  <option [ngValue]="p.id" [disabled]="p.quantiteStock === 0">
                    {{ p.designation }} — {{ nombre(p.prix) }} (stock : {{ p.quantiteStock }})
                  </option>
                }
              </select>
              <input type="number" min="1" step="1" aria-label="Quantité" [(ngModel)]="ligne.quantite" />
              <span class="sous-total">{{ nombre(sousTotal(ligne)) }}</span>
              <button type="button" class="btn btn-secondaire btn-petit" aria-label="Retirer la ligne"
                      [disabled]="lignes().length === 1" (click)="retirer(i)">✕</button>
              @if (depasse(ligne); as stock) {
                <small class="depasse">Stock disponible : {{ stock }}</small>
              }
            </div>
          }

          <button type="button" class="btn btn-secondaire btn-petit" (click)="ajouter()">+ Ajouter un produit</button>

          <p class="total">Total : {{ nombre(total()) }}</p>

          @if (erreur(); as message) {
            <p class="erreur" role="alert">{{ message }}</p>
          }

          <div class="boutons">
            <button type="button" class="btn" [disabled]="enCours()" (click)="enregistrer()">
              {{ enCours() ? 'Enregistrement…' : 'Enregistrer la vente' }}
            </button>
            <a class="btn btn-secondaire" routerLink="/ventes">Annuler</a>
          </div>
        </div>
      }
    </div>
  `,
  styles: `
    .client { margin-bottom: 1rem; max-width: 420px; }
    .ligne { display: grid; grid-template-columns: 1fr 100px 110px auto; gap: .5rem; align-items: center; margin-bottom: .6rem; }
    .ligne small { grid-column: 1 / -1; }
    .sous-total { text-align: right; font-weight: 600; }
    .depasse { color: #b91c1c; }
    .total { font-size: 1.25rem; font-weight: 700; margin: 1rem 0; }
    .succes { padding: .6rem .75rem; border-radius: 8px; background: #dcfce7; color: #166534; }
    .alerte { padding: .6rem .75rem; border-radius: 8px; background: #fef3c7; color: #92400e; margin-bottom: 1rem; }
  `,
})
export class VenteForm {
  private readonly venteService = inject(VenteService);
  private readonly produitService = inject(ProduitService);
  private readonly clientService = inject(ClientService);

  protected readonly produits = signal<Produit[]>([]);
  protected readonly clients = signal<Client[]>([]);
  protected readonly clientId = signal<string | null>(null);
  protected readonly lignes = signal<LigneSaisie[]>([{ produitId: null, quantite: 1 }]);
  protected readonly resultat = signal<Vente | null>(null);
  protected readonly erreur = signal<string | null>(null);
  protected readonly enCours = signal(false);

  constructor() {
    this.chargerProduits();
    this.clientService.lister().subscribe({
      next: (liste) => this.clients.set(liste),
      error: (e: unknown) => this.erreur.set(messageErreur(e)),
    });
  }

  protected ajouter(): void {
    this.lignes.update((l) => [...l, { produitId: null, quantite: 1 }]);
  }

  protected retirer(index: number): void {
    this.lignes.update((l) => l.filter((_, i) => i !== index));
  }

  protected sousTotal(ligne: LigneSaisie): number {
    const produit = this.produits().find((p) => p.id === ligne.produitId);
    return produit && ligne.quantite ? produit.prix * ligne.quantite : 0;
  }

  protected total(): number {
    return this.lignes().reduce((somme, l) => somme + this.sousTotal(l), 0);
  }

  /** Renvoie le stock disponible si la quantité demandée le dépasse (sinon 0). Le serveur reste juge. */
  protected depasse(ligne: LigneSaisie): number {
    const produit = this.produits().find((p) => p.id === ligne.produitId);
    return produit && ligne.quantite !== null && ligne.quantite > produit.quantiteStock
      ? produit.quantiteStock
      : 0;
  }

  protected enregistrer(): void {
    const lignes = this.lignes();
    const valides = lignes.filter((l) => l.produitId !== null && l.quantite !== null && l.quantite > 0);
    if (valides.length === 0 || valides.length !== lignes.length) {
      this.erreur.set('Chaque ligne doit avoir un produit et une quantité supérieure à 0.');
      return;
    }

    this.erreur.set(null);
    this.enCours.set(true);
    this.venteService
      .creer({ clientId: this.clientId(), lignes: valides.map((l) => ({ produitId: l.produitId as number, quantite: l.quantite as number })) })
      .subscribe({
        next: (vente) => {
          this.enCours.set(false);
          this.resultat.set(vente);
        },
        error: (e: unknown) => {
          this.enCours.set(false);
          this.erreur.set(messageErreur(e));
          this.chargerProduits(); // le stock a peut-être changé entre-temps
        },
      });
  }

  protected nouvelleVente(): void {
    this.resultat.set(null);
    this.erreur.set(null);
    this.lignes.set([{ produitId: null, quantite: 1 }]);
    this.clientId.set(null);
    this.chargerProduits();
  }

  protected nombre(valeur: number): string {
    return new Intl.NumberFormat('fr-FR').format(valeur);
  }

  private chargerProduits(): void {
    this.produitService.lister('', '').subscribe({
      next: (liste) => this.produits.set(liste),
      error: (e: unknown) => this.erreur.set(messageErreur(e)),
    });
  }
}
