import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from './api';
import { Dashboard } from './models';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly http = inject(HttpClient);

  tableauDeBord(): Observable<Dashboard> {
    return this.http.get<Dashboard>(`${API_URL}/dashboard`);
  }
}
