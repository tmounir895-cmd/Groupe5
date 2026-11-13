import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from './api';
import { Client, ClientSaisie } from './models';

@Injectable({ providedIn: 'root' })
export class ClientService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/clients`;

  lister(recherche = ''): Observable<Client[]> {
    let params = new HttpParams();
    if (recherche) {
      params = params.set('q', recherche);
    }
    return this.http.get<Client[]>(this.url, { params });
  }

  creer(client: ClientSaisie): Observable<Client> {
    return this.http.post<Client>(this.url, client);
  }

  modifier(id: string, client: ClientSaisie): Observable<Client> {
    return this.http.put<Client>(`${this.url}/${encodeURIComponent(id)}`, client);
  }

  supprimer(id: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${encodeURIComponent(id)}`);
  }
}
