import { HttpErrorResponse } from '@angular/common/http';

/** Transforme une erreur de l'API (format ProblemDetail du backend) en message lisible. */
export function messageErreur(erreur: unknown): string {
  if (erreur instanceof HttpErrorResponse) {
    if (erreur.status === 0) {
      return 'Impossible de joindre le serveur. Vérifiez que le backend est démarré.';
    }
    if (erreur.status === 403) {
      return "Vous n'avez pas le droit d'effectuer cette action.";
    }
    const corps = erreur.error as { detail?: string; erreurs?: Record<string, string> } | null;
    if (corps?.erreurs) {
      return Object.values(corps.erreurs).join(' · ');
    }
    if (corps?.detail) {
      return corps.detail;
    }
  }
  return 'Une erreur est survenue. Réessayez.';
}
