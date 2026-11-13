import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth.service';
import { messageErreur } from '../../core/erreurs';
import { ChiffreAffaires, Vente } from '../../core/models';
import { VenteService } from '../../core/vente.service';

function enTexte(d: Date): string {
  const mois = String(d.getMonth() + 1).padStart(2, '0');
  const jour = String(d.getDate()).padStart(2, '0');
  return `${d.getFullYear()}-${mois}-${jour}`;
}

@Component({
  selector: 'app-ventes',
  imports: [FormsModule, RouterLink],
  template: `
    <div class="conteneur">
      <div class="barre">
        <h1>Ventes</h1>
        @if (peutVendre) {
          <a class="btn" routerLink="/ventes/nouvelle">Nouvelle vente</a>
        }
      </div>

      <div class="carte">
        <strong>Chiffre d'affaires</strong>
        <div class="periode">
          <label>Du <input type="date" [ngModel]="debut()" (ngModelChange)="debut.set($event)" /></label>
          <label>Au <input type="date" [ngModel]="fin()" (ngModelChange)="fin.set($event)" /></label>
          <button type="button" class="btn btn-secondaire" (click)="calculer()">Calculer</button>
        </div>
        @if (chiffre(); as c) {
          <p class="resultat">
            <span class="montant">{{ nombre(c.total) }}</span>
            · {{ c.nombreVentes }} vente(s) du {{ c.debut }} au {{ c.fin }}
          </p>
        }
        @if (erreurChiffre(); as message) {
          <p class="erreur" role="alert">{{ message }}</p>
        }
      </div>

      @if (erreur(); as message) {
        <p class="erreur" role="alert">{{ message }}</p>
      }

      @if (ventes().length === 0) {
        <p class="vide">Aucune vente.</p>
      } @else {
        <div class="defilement">
          <table class="tableau">
            <thead>
              <tr>
                <th>N°</th>
                <th>Date</th>
                <th>Vendeur</th>
                <th>Client</th>
                <th>Facture</th>
                <th class="nombre">Total</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              @for (v of ventes(); track v.id) {
                <tr>
                  <td>{{ v.id }}</td>
                  <td>{{ date(v.date) }}</td>
                  <td>{{ v.vendeur }}</td>
                  <td>{{ v.clientNom }}</td>
                  <td>{{ v.factureNumero }}</td>
                  <td class="nombre">{{ nombre(v.total) }}</td>
                  <td class="actions">
                    <button type="button" class="btn btn-secondaire btn-petit" (click)="basculer(v.id)">
                      {{ ouverte() === v.id ? 'Masquer' : 'Détails' }}
                    </button>
                  </td>
                </tr>
                @if (ouverte() === v.id) {
                  <tr class="detail">
                    <td colspan="7">
                      @for (l of v.lignes; track l.produitId) {
                        <div>
                          {{ l.designation }} : {{ l.quantite }} × {{ nombre(l.prixUnitaire) }}
                          = <strong>{{ nombre(l.sousTotal) }}</strong>
                        </div>
                      }
                    </td>
                  </tr>
                }
              }
            </tbody>
          </table>
        </div>
      }
    </div>
  `,
  styles: `
    .periode { display: flex; flex-wrap: wrap; align-items: end; gap: .75rem; margin-top: .75rem; }
    .periode label { display: flex; flex-direction: column; gap: .25rem; font-size: .9rem; color: #334155; }
    .resultat { margin: .75rem 0 0; color: #475569; }
    .montant { font-size: 1.5rem; font-weight: 700; color: #0f172a; }
    .detail td { background: #f8fafc; color: #334155; font-size: .9rem; }
  `,
})
export class Ventes {
  private readonly venteService = inject(VenteService);
  private readonly auth = inject(AuthService);

  protected readonly peutVendre = this.auth.aUnDesRoles('ADMINISTRATEUR', 'VENDEUR');

  protected readonly ventes = signal<Vente[]>([]);
  protected readonly ouverte = signal<number | null>(null);
  protected readonly erreur = signal<string | null>(null);

  // Période du chiffre d'affaires : du 1er du mois à aujourd'hui.
  protected readonly debut = signal(enTexte(new Date(new Date().getFullYear(), new Date().getMonth(), 1)));
  protected readonly fin = signal(enTexte(new Date()));
  protected readonly chiffre = signal<ChiffreAffaires | null>(null);
  protected readonly erreurChiffre = signal<string | null>(null);

  constructor() {
    this.venteService.lister().subscribe({
      next: (liste) => this.ventes.set(liste),
      error: (e: unknown) => this.erreur.set(messageErreur(e)),
    });
    this.calculer();
  }

  protected calculer(): void {
    this.erreurChiffre.set(null);
    this.venteService.chiffreAffaires(this.debut(), this.fin()).subscribe({
      next: (c) => this.chiffre.set(c),
      error: (e: unknown) => {
        this.chiffre.set(null);
        this.erreurChiffre.set(messageErreur(e));
      },
    });
  }

  protected basculer(id: number): void {
    this.ouverte.set(this.ouverte() === id ? null : id);
  }

  protected nombre(valeur: number): string {
    return new Intl.NumberFormat('fr-FR').format(valeur);
  }

  protected date(valeur: string): string {
    return new Date(valeur).toLocaleString('fr-FR', { dateStyle: 'short', timeStyle: 'short' });
  }
}
