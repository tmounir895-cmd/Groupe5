export type Role = 'ADMINISTRATEUR' | 'VENDEUR' | 'GESTIONNAIRE_STOCK' | 'RESPONSABLE_STOCK';

export interface Utilisateur {
  id: number;
  nom: string;
  prenom: string | null;
  email: string;
  role: Role;
}

export interface LoginResponse {
  token: string;
  type: string;
  expiresInSeconds: number;
  utilisateur: Utilisateur;
}

export interface Categorie {
  id: string;
  nom: string;
  description: string | null;
}

export interface CategorieCreation {
  id: string;
  nom: string;
  description: string | null;
}

export interface CategorieModification {
  nom: string;
  description: string | null;
}

export interface Produit {
  id: number;
  reference: string;
  designation: string;
  prix: number;
  quantiteStock: number;
  seuilAlerte: number;
  stockFaible: boolean;
  categorieId: string | null;
  categorieNom: string | null;
}

/** À la création, on saisit le stock initial ; ensuite il ne change que par les entrées et les ventes. */
export interface ProduitCreation {
  reference: string;
  designation: string;
  prix: number;
  quantiteStock: number;
  seuilAlerte: number;
  categorieId: string;
}

export interface ProduitModification {
  reference: string;
  designation: string;
  prix: number;
  seuilAlerte: number;
  categorieId: string;
}

export interface LigneVente {
  produitId: number;
  reference: string;
  designation: string;
  quantite: number;
  prixUnitaire: number;
  sousTotal: number;
}

export interface Vente {
  id: number;
  date: string;
  vendeur: string | null;
  clientId: string | null;
  clientNom: string | null;
  factureNumero: string | null;
  lignes: LigneVente[];
  total: number;
  /** Produits vendus dont le stock a atteint le seuil d'alerte (renseigné à l'enregistrement). */
  alertesStockFaible: string[];
}

export interface VenteCreation {
  /** Facultatif : null = vente sans client enregistré. */
  clientId: string | null;
  lignes: { produitId: number; quantite: number }[];
}

export interface ChiffreAffaires {
  debut: string;
  fin: string;
  nombreVentes: number;
  total: number;
}

export interface Mouvement {
  id: number;
  type: string;
  quantite: number;
  date: string;
  produitId: number;
  designation: string;
  utilisateurId: number | null;
  stockActuel: number;
}

export interface EntreeStock {
  produitId: number;
  quantite: number;
}

export interface Dashboard {
  nombreProduits: number;
  nombreProduitsStockFaible: number;
  nombreRuptures: number;
  valeurStock: number;
  produitsStockFaible: Produit[];
  derniersMouvements: Mouvement[];
}

export interface UtilisateurCreation {
  nom: string;
  prenom: string | null;
  email: string;
  motDePasse: string;
  role: Role;
}

export interface Client {
  id: string;
  nom: string;
  telephone: string;
  email: string | null;
  adresse: string;
}

export interface ClientSaisie {
  nom: string;
  telephone: string;
  email: string | null;
  adresse: string;
}
