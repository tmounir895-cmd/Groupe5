import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from './api';
import { Role, Utilisateur, UtilisateurCreation } from './models';

/** Gestion des comptes : réservée à l'administrateur (le backend le vérifie aussi). */
@Injectable({ providedIn: 'root' })
export class UtilisateurService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/utilisateurs`;

  lister(): Observable<Utilisateur[]> {
    return this.http.get<Utilisateur[]>(this.url);
  }

  creer(utilisateur: UtilisateurCreation): Observable<Utilisateur> {
    return this.http.post<Utilisateur>(this.url, utilisateur);
  }

  modifierRole(id: number, role: Role): Observable<Utilisateur> {
    return this.http.put<Utilisateur>(`${this.url}/${id}/role`, { role });
  }

  reinitialiserMotDePasse(id: number, motDePasse: string): Observable<void> {
    return this.http.put<void>(`${this.url}/${id}/mot-de-passe`, { motDePasse });
  }

  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
