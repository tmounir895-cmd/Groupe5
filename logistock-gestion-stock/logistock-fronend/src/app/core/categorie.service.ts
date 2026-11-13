import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from './api';
import { Categorie, CategorieCreation, CategorieModification } from './models';

@Injectable({ providedIn: 'root' })
export class CategorieService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/categories`;

  lister(): Observable<Categorie[]> {
    return this.http.get<Categorie[]>(this.url);
  }

  creer(categorie: CategorieCreation): Observable<Categorie> {
    return this.http.post<Categorie>(this.url, categorie);
  }

  modifier(id: string, categorie: CategorieModification): Observable<Categorie> {
    return this.http.put<Categorie>(`${this.url}/${encodeURIComponent(id)}`, categorie);
  }

  supprimer(id: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${encodeURIComponent(id)}`);
  }
}
