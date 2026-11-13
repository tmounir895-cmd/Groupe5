# LogiStock SA - Backend (Spring Boot)

Java 21 - Spring Boot 4.1 - Maven - MySQL - Spring Security (JWT à l'étape 2).

## Lancer
1. Exécuter `database/LOGISTOCKSA.sql` puis `database/01_migration_categorie_produit.sql` dans MySQL.
2. Renseigner `spring.datasource.password` dans `src/main/resources/application.properties`.
3. `mvn spring-boot:run` (ou lancer `LogistockApplication` depuis l'IDE). API sur http://localhost:8080.
4. Tests : `mvn test`.

## Authentification (JWT)
1. Exécuter aussi `database/02_migration_utilisateurs_roles.sql`.
2. Choisir le mot de passe du premier administrateur dans `app.bootstrap-admin.password`
   (compte `admin@logistock.sa` créé au démarrage s'il n'existe pas).
3. `POST /api/auth/login` avec `{"email": "...", "motDePasse": "..."}` renvoie un `token`.
4. Chaque requête suivante envoie l'en-tête `Authorization: Bearer <token>`.

Rôles : `ADMINISTRATEUR`, `VENDEUR`, `GESTIONNAIRE_STOCK`, `RESPONSABLE_STOCK`.

| Méthode | URL | Rôles |
|---|---|---|
| POST | `/api/auth/login` | public |
| GET | `/api/auth/me` | tout utilisateur connecté |
| GET / POST | `/api/utilisateurs` | ADMINISTRATEUR |
| GET / DELETE | `/api/utilisateurs/{id}` | ADMINISTRATEUR |
| PUT | `/api/utilisateurs/{id}/role` | ADMINISTRATEUR |
| PUT | `/api/utilisateurs/{id}/mot-de-passe` | ADMINISTRATEUR |

## API
| Méthode | URL | Rôles |
|---|---|---|
| GET | `/api/categories`, `/api/categories/{id}` | tous les connectés |
| POST / PUT / DELETE | `/api/categories[/{id}]` | ADMINISTRATEUR, GESTIONNAIRE_STOCK |
| GET | `/api/produits?categorieId=&q=`, `/api/produits/{id}`, `/api/produits/alertes-stock` | tous les connectés |
| POST / PUT / DELETE | `/api/produits[/{id}]` | ADMINISTRATEUR, GESTIONNAIRE_STOCK |

Règle : un produit est en **stock faible** quand `quantiteStock <= seuilAlerte`.

## Stock et ventes (étape 3)
Exécuter aussi `database/03_migration_ventes_stock.sql`.

| Méthode | URL | Rôles |
|---|---|---|
| POST | `/api/stock/entrees` | ADMINISTRATEUR, GESTIONNAIRE_STOCK |
| GET | `/api/stock/mouvements?produitId=` | ADMINISTRATEUR, GESTIONNAIRE_STOCK, RESPONSABLE_STOCK |
| POST | `/api/ventes` | ADMINISTRATEUR, VENDEUR |
| GET | `/api/ventes`, `/api/ventes/{id}` | ADMINISTRATEUR, RESPONSABLE_STOCK (toutes), VENDEUR (les siennes) |
| GET | `/api/factures/chiffre-affaires?debut=&fin=` | ADMINISTRATEUR, VENDEUR, RESPONSABLE_STOCK |

Règles : une vente baisse le stock ; elle est refusée (409) si le stock d'un produit est insuffisant,
et rien n'est enregistré. Chaque entrée ou sortie est tracée dans `mouvement-stock`.
La réponse d'une vente liste `alertesStockFaible` (produits vendus dont le stock a atteint le seuil).

## Tableau de bord (étape 4)
| Méthode | URL | Rôles |
|---|---|---|
| GET | `/api/dashboard` | ADMINISTRATEUR, RESPONSABLE_STOCK |

Renvoie le nombre de produits, le nombre de produits en stock faible et en rupture, la valeur du stock
(prix x quantité), la liste des produits en stock faible (les plus critiques d'abord) et les 10 derniers mouvements.
Les chiffres sont recalculés à chaque appel : l'état du stock est toujours à jour.

## Clients
Exécuter aussi `database/04_migration_clients.sql`.

| Méthode | URL | Rôles |
|---|---|---|
| GET | `/api/clients?q=`, `/api/clients/{id}` | ADMINISTRATEUR, VENDEUR |
| POST / PUT | `/api/clients[/{id}]` | ADMINISTRATEUR, VENDEUR |
| DELETE | `/api/clients/{id}` | ADMINISTRATEUR |

Le code client (C0001, C0002...) est généré. Une vente peut recevoir un `clientId` facultatif.

## Exemple
```
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" \
  -d '{"email":"admin@logistock.sa","motDePasse":"TON_MOT_DE_PASSE"}'

curl -H "Authorization: Bearer <token>" http://localhost:8080/api/produits/alertes-stock
```
