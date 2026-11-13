import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  template: `
    <main class="page">
      <form class="carte" (ngSubmit)="connecter()">
        <h1>LogiStock SA</h1>
        <p class="sous-titre">Gestion de stock et ventes</p>

        <label for="email">Email</label>
        <input id="email" name="email" type="email" autocomplete="username" required
               [(ngModel)]="email" />

        <label for="mdp">Mot de passe</label>
        <input id="mdp" name="motDePasse" type="password" autocomplete="current-password" required
               [(ngModel)]="motDePasse" />

        @if (erreur(); as message) {
          <p class="erreur" role="alert">{{ message }}</p>
        }

        <button type="submit" [disabled]="enCours()">
          {{ enCours() ? 'Connexion…' : 'Se connecter' }}
        </button>
      </form>
    </main>
  `,
  styles: `
    .page { min-height: 100vh; display: grid; place-items: center; padding: 1rem; background: #f1f5f9; }
    .carte { width: 100%; max-width: 380px; display: flex; flex-direction: column; gap: .5rem;
             background: #fff; padding: 2rem; border-radius: 12px; box-shadow: 0 4px 20px rgb(0 0 0 / .08); }
    h1 { margin: 0; font-size: 1.6rem; color: #0f172a; }
    .sous-titre { margin: 0 0 1rem; color: #64748b; }
    label { font-weight: 600; font-size: .9rem; color: #334155; margin-top: .5rem; }
    input { padding: .65rem .75rem; border: 1px solid #cbd5e1; border-radius: 8px; font-size: 1rem; }
    input:focus { outline: 2px solid #2563eb; border-color: transparent; }
    button { margin-top: 1rem; padding: .75rem; border: 0; border-radius: 8px; background: #2563eb;
             color: #fff; font-size: 1rem; font-weight: 600; cursor: pointer; }
    button:disabled { opacity: .6; cursor: default; }
    .erreur { margin: .5rem 0 0; padding: .6rem .75rem; border-radius: 8px; background: #fee2e2; color: #991b1b; }
  `,
})
export class Login {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly email = signal('');
  protected readonly motDePasse = signal('');
  protected readonly erreur = signal<string | null>(null);
  protected readonly enCours = signal(false);

  protected connecter(): void {
    if (this.enCours()) {
      return;
    }
    this.erreur.set(null);
    this.enCours.set(true);

    this.auth.login(this.email().trim(), this.motDePasse()).subscribe({
      next: () => void this.router.navigate(['/accueil']),
      error: (e: HttpErrorResponse) => {
        this.enCours.set(false);
        this.erreur.set(this.messageErreur(e));
      },
    });
  }

  private messageErreur(e: HttpErrorResponse): string {
    switch (e.status) {
      case 0:
        return 'Impossible de joindre le serveur. Vérifiez que le backend est démarré.';
      case 400:
        return 'Veuillez remplir tous les champs.';
      case 401:
        return 'Email ou mot de passe incorrect.';
      default:
        return 'Une erreur est survenue. Réessayez.';
    }
  }
}
