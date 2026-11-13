import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { CategorieService } from '../../core/categorie.service';
import { messageErreur } from '../../core/erreurs';
import { Categorie } from '../../core/models';
import { ProduitService } from '../../core/produit.service';

/** Création (/produits/nouveau) et modification (/produits/:id/modifier) d'un produit. */
@Component({
  selector: 'app-produit-form',
  imports: [FormsModule, RouterLink],
  template: `
    <div class="conteneur">
      <div class="barre">
        <h1>{{ enEdition ? 'Modifier le produit' : 'Nouveau produit' }}</h1>
      </div>

      <form class="carte formulaire" (ngSubmit)="enregistrer()">
        <div class="champ">
          <label for="reference">Référence</label>
          <input id="reference" name="reference" required maxlength="45" [(ngModel)]="reference" />
        </div>

        <div class="champ">
          <label for="designation">Désignation</label>
          <input id="designation" name="designation" required maxlength="40" [(ngModel)]="designation" />
        </div>

        <div class="champ">
          <label for="categorie">Catégorie</label>
          <select id="categorie" name="categorie" required [(ngModel)]="categorieId">
            <option value="">Choisir une catégorie</option>
            @for (c of categories(); track c.id) {
              <option [value]="c.id">{{ c.nom }}</option>
            }
          </select>
        </div>

        <div class="champ">
          <label for="prix">Prix</label>
          <input id="prix" name="prix" type="number" min="0" step="1" required [(ngModel)]="prix" />
        </div>

        @if (!enEdition) {
          <div class="champ">
            <label for="stock">Stock initial</label>
            <input id="stock" name="stock" type="number" min="0" step="1" required [(ngModel)]="quantiteStock" />
            <small>Ensuite, le stock évolue par les entrées de marchandises et les ventes.</small>
          </div>
        }

        <div class="champ">
          <label for="seuil">Seuil d'alerte</label>
          <input id="seuil" name="seuil" type="number" min="0" step="1" required [(ngModel)]="seuilAlerte" />
          <small>Une alerte s'affiche quand le stock atteint ce seuil.</small>
        </div>

        @if (erreur(); as message) {
          <p class="erreur" role="alert">{{ message }}</p>
        }

        <div class="boutons">
          <button type="submit" class="btn" [disabled]="enCours()">
            {{ enCours() ? 'Enregistrement…' : 'Enregistrer' }}
          </button>
          <a class="btn btn-secondaire" routerLink="/produits">Annuler</a>
        </div>
      </form>
    </div>
  `,
})
export class ProduitForm {
  private readonly produitService = inject(ProduitService);
  private readonly categorieService = inject(CategorieService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  private readonly idProduit = this.route.snapshot.paramMap.get('id');
  protected readonly enEdition = this.idProduit !== null;

  protected readonly categories = signal<Categorie[]>([]);
  protected readonly reference = signal('');
  protected readonly designation = signal('');
  protected readonly categorieId = signal('');
  protected readonly prix = signal<number | null>(null);
  protected readonly quantiteStock = signal<number | null>(null);
  protected readonly seuilAlerte = signal<number | null>(null);

  protected readonly erreur = signal<string | null>(null);
  protected readonly enCours = signal(false);

  constructor() {
    this.categorieService.lister().subscribe({
      next: (liste) => this.categories.set(liste),
      error: (e: unknown) => this.erreur.set(messageErreur(e)),
    });

    if (this.idProduit !== null) {
      this.produitService.obtenir(Number(this.idProduit)).subscribe({
        next: (p) => {
          this.reference.set(p.reference);
          this.designation.set(p.designation);
          this.categorieId.set(p.categorieId ?? '');
          this.prix.set(p.prix);
          this.seuilAlerte.set(p.seuilAlerte);
        },
        error: (e: unknown) => this.erreur.set(messageErreur(e)),
      });
    }
  }

  protected enregistrer(): void {
    const prix = this.prix();
    const seuil = this.seuilAlerte();
    const stock = this.quantiteStock();
    const categorieId = this.categorieId();

    if (prix === null || seuil === null || !categorieId || (!this.enEdition && stock === null)) {
      this.erreur.set('Veuillez remplir tous les champs.');
      return;
    }

    this.erreur.set(null);
    this.enCours.set(true);

    const base = {
      reference: this.reference().trim(),
      designation: this.designation().trim(),
      prix,
      seuilAlerte: seuil,
      categorieId,
    };
    const requete = this.idProduit !== null
      ? this.produitService.modifier(Number(this.idProduit), base)
      : this.produitService.creer({ ...base, quantiteStock: stock ?? 0 });

    requete.subscribe({
      next: () => void this.router.navigate(['/produits']),
      error: (e: unknown) => {
        this.enCours.set(false);
        this.erreur.set(messageErreur(e));
      },
    });
  }
}
