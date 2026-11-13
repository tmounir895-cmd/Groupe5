import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from './api';
import { ChiffreAffaires, Vente, VenteCreation } from './models';

@Injectable({ providedIn: 'root' })
export class VenteService {
  private readonly http = inject(HttpClient);

  /** Un vendeur reçoit ses propres ventes ; l'administrateur et le responsable de stock reçoivent toutes les ventes. */
  lister(): Observable<Vente[]> {
    return this.http.get<Vente[]>(`${API_URL}/ventes`);
  }

  creer(vente: VenteCreation): Observable<Vente> {
    return this.http.post<Vente>(`${API_URL}/ventes`, vente);
  }

  /** Dates au format AAAA-MM-JJ, incluses. */
  chiffreAffaires(debut: string, fin: string): Observable<ChiffreAffaires> {
    const params = new HttpParams().set('debut', debut).set('fin', fin);
    return this.http.get<ChiffreAffaires>(`${API_URL}/factures/chiffre-affaires`, { params });
  }
}
