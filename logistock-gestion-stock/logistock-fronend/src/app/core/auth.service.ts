import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Injectable, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

import { API_URL } from './api';
import { LoginResponse, Role, Utilisateur } from './models';

interface Session {
  token: string;
  utilisateur: Utilisateur;
}

const CLE_SESSION = 'logistock.session';

/** Connexion, déconnexion et état de l'utilisateur connecté (le jeton est gardé dans le navigateur). */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  // Avec le rendu serveur (SSR), localStorage n'existe pas : on ne l'utilise que dans le navigateur.
  private readonly dansNavigateur = isPlatformBrowser(inject(PLATFORM_ID));

  private readonly session = signal<Session | null>(this.lireSession());

  readonly utilisateur = computed(() => this.session()?.utilisateur ?? null);
  readonly role = computed(() => this.session()?.utilisateur.role ?? null);
  readonly estConnecte = computed(() => this.session() !== null);

  get token(): string | null {
    return this.session()?.token ?? null;
  }

  login(email: string, motDePasse: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${API_URL}/auth/login`, { email, motDePasse }).pipe(
      tap((reponse) => {
        const session: Session = { token: reponse.token, utilisateur: reponse.utilisateur };
        this.session.set(session);
        if (this.dansNavigateur) {
          localStorage.setItem(CLE_SESSION, JSON.stringify(session));
        }
      }),
    );
  }

  logout(): void {
    this.session.set(null);
    if (this.dansNavigateur) {
      localStorage.removeItem(CLE_SESSION);
    }
    void this.router.navigate(['/login']);
  }

  aUnDesRoles(...roles: Role[]): boolean {
    const role = this.role();
    return role !== null && roles.includes(role);
  }

  private lireSession(): Session | null {
    if (!this.dansNavigateur) {
      return null;
    }
    try {
      const brut = localStorage.getItem(CLE_SESSION);
      if (!brut) {
        return null;
      }
      const session = JSON.parse(brut) as Session;
      if (this.estExpire(session.token)) {
        localStorage.removeItem(CLE_SESSION);
        return null;
      }
      return session;
    } catch {
      localStorage.removeItem(CLE_SESSION);
      return null;
    }
  }

  /** Lit la date d'expiration dans le jeton (partie centrale du JWT, encodée en base64). */
  private estExpire(token: string): boolean {
    try {
      const milieu = token.split('.')[1] ?? '';
      const json = atob(milieu.replace(/-/g, '+').replace(/_/g, '/'));
      const contenu = JSON.parse(json) as { exp?: number };
      return typeof contenu.exp === 'number' && contenu.exp * 1000 <= Date.now();
    } catch {
      return true;
    }
  }
}
