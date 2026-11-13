import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from './api';
import { Produit, ProduitCreation, ProduitModification } from './models';

@Injectable({ providedIn: 'root' })
export class ProduitService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/produits`;

  lister(categorieId: string, recherche: string): Observable<Produit[]> {
    let params = new HttpParams();
    if (categorieId) {
      params = params.set('categorieId', categorieId);
    }
    if (recherche) {
      params = params.set('q', recherche);
    }
    return this.http.get<Produit[]>(this.url, { params });
  }

  obtenir(id: number): Observable<Produit> {
    return this.http.get<Produit>(`${this.url}/${id}`);
  }

  creer(produit: ProduitCreation): Observable<Produit> {
    return this.http.post<Produit>(this.url, produit);
  }

  modifier(id: number, produit: ProduitModification): Observable<Produit> {
    return this.http.put<Produit>(`${this.url}/${id}`, produit);
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
