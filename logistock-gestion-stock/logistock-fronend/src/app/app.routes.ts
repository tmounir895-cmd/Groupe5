import { Routes } from '@angular/router';

import { authGuard, roleGuard } from './core/auth.guards';

const ECRITURE_STOCK = roleGuard('ADMINISTRATEUR', 'GESTIONNAIRE_STOCK');
const VOIR_STOCK = roleGuard('ADMINISTRATEUR', 'GESTIONNAIRE_STOCK', 'RESPONSABLE_STOCK');
const VOIR_VENTES = roleGuard('ADMINISTRATEUR', 'VENDEUR', 'RESPONSABLE_STOCK');
const VENDRE = roleGuard('ADMINISTRATEUR', 'VENDEUR');
const TABLEAU_DE_BORD = roleGuard('ADMINISTRATEUR', 'RESPONSABLE_STOCK');
const ADMIN = roleGuard('ADMINISTRATEUR');
const CLIENTS = roleGuard('ADMINISTRATEUR', 'VENDEUR');

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./pages/login/login').then((m) => m.Login) },
  {
    // Toutes les pages connectées partagent le cadre (en-tête + menu) de Shell.
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./layout/shell').then((m) => m.Shell),
    children: [
      { path: 'accueil', loadComponent: () => import('./pages/accueil/accueil').then((m) => m.Accueil) },
      { path: 'produits', loadComponent: () => import('./pages/produits/produits').then((m) => m.Produits) },
      {
        path: 'produits/nouveau',
        canActivate: [ECRITURE_STOCK],
        loadComponent: () => import('./pages/produits/produit-form').then((m) => m.ProduitForm),
      },
      {
        path: 'produits/:id/modifier',
        canActivate: [ECRITURE_STOCK],
        loadComponent: () => import('./pages/produits/produit-form').then((m) => m.ProduitForm),
      },
      { path: 'categories', loadComponent: () => import('./pages/categories/categories').then((m) => m.Categories) },
      { path: 'stock', canActivate: [VOIR_STOCK], loadComponent: () => import('./pages/stock/stock').then((m) => m.Stock) },
      { path: 'ventes', canActivate: [VOIR_VENTES], loadComponent: () => import('./pages/ventes/ventes').then((m) => m.Ventes) },
      {
        path: 'ventes/nouvelle',
        canActivate: [VENDRE],
        loadComponent: () => import('./pages/ventes/vente-form').then((m) => m.VenteForm),
      },
      {
        path: 'tableau-de-bord',
        canActivate: [TABLEAU_DE_BORD],
        loadComponent: () => import('./pages/dashboard/dashboard').then((m) => m.DashboardPage),
      },
      {
        path: 'utilisateurs',
        canActivate: [ADMIN],
        loadComponent: () => import('./pages/utilisateurs/utilisateurs').then((m) => m.Utilisateurs),
      },
      { path: 'clients', canActivate: [CLIENTS], loadComponent: () => import('./pages/clients/clients').then((m) => m.Clients) },
      { path: '', pathMatch: 'full', redirectTo: 'accueil' },
    ],
  },
  { path: '**', redirectTo: 'accueil' },
];
