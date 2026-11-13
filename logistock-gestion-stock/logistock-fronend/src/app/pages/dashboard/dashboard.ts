import { Component, DestroyRef, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth.service';
import { DashboardService } from '../../core/dashboard.service';
import { messageErreur } from '../../core/erreurs';
import { Dashboard, Mouvement } from '../../core/models';

const ACTUALISATION_MS = 30_000;

/** Tableau de bord du stock : chiffres clés, produits en stock faible et derniers mouvements. */
@Component({
  selector: 'app-dashboard',
  imports: [RouterLink],
  template: `
    <div class="conteneur">
      <div class="barre">
        <h1>Tableau de bord</h1>
        <button type="button" class="btn btn-secondaire" (click)="charger()">Actualiser</button>
      </div>

      @if (erreur(); as message) {
        <p class="erreur" role="alert">{{ message }}</p>
      }

      @if (donnees(); as d) {
        <section class="indicateurs">
          <article class="carte indicateur">
            <span class="etiquette">Produits</span>
            <span class="valeur">{{ nombre(d.nombreProduits) }}</span>
          </article>
          <article class="carte indicateur" [class.attention]="d.nombreProduitsStockFaible > 0">
            <span class="etiquette">Stock faible</span>
            <span class="valeur">{{ nombre(d.nombreProduitsStockFaible) }}</span>
          </article>
          <article class="carte indicateur" [class.danger]="d.nombreRuptures > 0">
            <span class="etiquette">Ruptures</span>
            <span class="valeur">{{ nombre(d.nombreRuptures) }}</span>
          </article>
          <article class="carte indicateur">
            <span class="etiquette">Valeur du stock</span>
            <span class="valeur">{{ nombre(d.valeurStock) }}</span>
          </article>
        </section>

        <h2>Produits en stock faible</h2>
        @if (d.produitsStockFaible.length === 0) {
          <p class="vide">Aucun produit en stock faible.</p>
        } @else {
          <div class="defilement">
            <table class="tableau">
              <thead>
                <tr>
                  <th>Produit</th>
                  <th>Catégorie</th>
                  <th class="nombre">Stock</th>
                  <th class="nombre">Seuil d'alerte</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                @for (p of d.produitsStockFaible; track p.id) {
                  <tr>
                    <td>{{ p.designation }} <small class="ref">{{ p.reference }}</small></td>
                    <td>{{ p.categorieNom }}</td>
                    <td class="nombre">{{ nombre(p.quantiteStock) }}</td>
                    <td class="nombre">{{ nombre(p.seuilAlerte) }}</td>
                    <td>
                      @if (p.quantiteStock === 0) {
                        <span class="badge badge-rupture">Rupture</span>
                      } @else {
                        <span class="badge badge-alerte">Stock faible</span>
                      }
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
          @if (peutReapprovisionner) {
            <p><a class="btn btn-petit" routerLink="/stock">Enregistrer une entrée de marchandises</a></p>
          }
        }

        <h2>Derniers mouvements de stock</h2>
        @if (d.derniersMouvements.length === 0) {
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
                </tr>
              </thead>
              <tbody>
                @for (m of d.derniersMouvements; track m.id) {
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
                  </tr>
                }
              </tbody>
            </table>
          </div>
        }
        <p class="maj">Mis à jour à {{ heure() }} · actualisation automatique toutes les 30 secondes</p>
      } @else if (!erreur()) {
        <p class="vide">Chargement…</p>
      }
    </div>
  `,
  styles: `
    h2 { font-size: 1.15rem; margin: 1.75rem 0 .75rem; }
    .indicateurs { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; }
    .indicateur { display: flex; flex-direction: column; gap: .25rem; margin: 0; border-left: 4px solid #2563eb; }
    .indicateur.attention { border-left-color: #f59e0b; background: #fffbeb; }
    .indicateur.danger { border-left-color: #dc2626; background: #fef2f2; }
    .etiquette { color: #64748b; font-size: .85rem; }
    .valeur { font-size: 1.75rem; font-weight: 700; }
    .ref { color: #64748b; margin-left: .25rem; }
    .badge-entree { background: #dcfce7; color: #166534; margin-left: 0; }
    .badge-sortie { background: #e0e7ff; color: #3730a3; margin-left: 0; }
    .maj { color: #64748b; font-size: .8rem; margin-top: 1.5rem; }
  `,
})
export class DashboardPage {
  private readonly service = inject(DashboardService);
  private readonly auth = inject(AuthService);

  protected readonly peutReapprovisionner = this.auth.aUnDesRoles('ADMINISTRATEUR', 'GESTIONNAIRE_STOCK');

  protected readonly donnees = signal<Dashboard | null>(null);
  protected readonly erreur = signal<string | null>(null);
  protected readonly heure = signal('');

  constructor() {
    this.charger();
    // L'état du stock se rafraîchit tout seul ; on arrête la minuterie en quittant la page.
    const minuterie = setInterval(() => this.charger(), ACTUALISATION_MS);
    inject(DestroyRef).onDestroy(() => clearInterval(minuterie));
  }

  protected charger(): void {
    this.service.tableauDeBord().subscribe({
      next: (d) => {
        this.donnees.set(d);
        this.erreur.set(null);
        this.heure.set(new Date().toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' }));
      },
      error: (e: unknown) => this.erreur.set(messageErreur(e)),
    });
  }

  protected nombre(valeur: number): string {
    return new Intl.NumberFormat('fr-FR').format(valeur);
  }

  /** Les anciens mouvements portent le type « entrée », les nouveaux « ENTREE ». */
  protected estEntree(m: Mouvement): boolean {
    return m.type.toLowerCase().startsWith('entr');
  }

  protected date(valeur: string): string {
    return new Date(valeur).toLocaleString('fr-FR', { dateStyle: 'short', timeStyle: 'short' });
  }
}
