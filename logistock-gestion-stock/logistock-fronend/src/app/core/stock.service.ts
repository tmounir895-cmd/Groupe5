import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from './api';
import { EntreeStock, Mouvement } from './models';

@Injectable({ providedIn: 'root' })
export class StockService {
  private readonly http = inject(HttpClient);

  enregistrerEntree(entree: EntreeStock): Observable<Mouvement> {
    return this.http.post<Mouvement>(`${API_URL}/stock/entrees`, entree);
  }

  /** Les 200 mouvements les plus récents ; produitId nul = tous les produits. */
  mouvements(produitId: number | null): Observable<Mouvement[]> {
    let params = new HttpParams();
    if (produitId !== null) {
      params = params.set('produitId', produitId);
    }
    return this.http.get<Mouvement[]>(`${API_URL}/stock/mouvements`, { params });
  }
}
