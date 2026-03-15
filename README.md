# Grade Validation (Web + API)

Ce projet est un système de gestion et de validation des notes pour les étudiants, développé en **Java 17** avec **Spring Boot** (API REST + mini interface web) et **Maven**.

## Fonctionnalités principales

- **Authentification** : Accès sécurisé avec deux rôles (**ENSEIGNANT**, **JURY**).
- **Gestion des Étudiants** : Visualisation et ajout d'étudiants.
- **Gestion des Cours** : Visualisation des cours disponibles.
- **Gestion des Notes** :
    - Ajout de notes (entre 0 et 20) par les enseignants.
    - Consultation de toutes les notes.
    - **Modification sécurisée** : Seul le rôle **JURY** peut modifier une note existante.
- **Historique des Modifications** : Chaque modification de note par le jury est tracée avec l'ancienne valeur, la nouvelle valeur, le motif et la date.

## Architecture Technique

- **Langage** : Java 17.
- **API** : Spring Boot + Spring Data JPA + Spring Security (roles **ENSEIGNANT**, **JURY**).
- **UI** : page statique servie par Spring (`/`) qui consomme l'API (`/api/**`).
- **Base de données** :
    - Par defaut: **H2 en memoire** (demarrage immediat en local).
    - Optionnel: **MySQL** (script disponible: `src/main/resources/schema-mysql.sql`).

## Installation et Utilisation

1. **Prérequis** : Java 17 et Maven installés.
2. **Compilation** (offline possible si les dependances sont deja en cache) :
   ```bash
   mvn -DskipTests compile
   ```
3. **Execution** :
   ```bash
   mvn spring-boot:run
   ```
4. **Acces** :
    - UI (dashboard): `http://localhost:8081/`

## Ou sont stockees les donnees ?

En local, les donnees sont persistees dans une base H2 sur fichier (donc elles restent apres redemarrage) :
- `~/.grade-validation/notes_db.mv.db`

## Stocker les donnees dans MySQL

Pour que toutes les donnees soient stockees dans **MySQL** (au lieu de H2), lance l'application avec le profil `mysql` :
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=mysql
```

Par defaut, le profil MySQL utilise:
- host: `localhost:3306`
- database: `notes_db`
- user/pass: `root` / `kamala237`

Tu peux modifier ces valeurs dans `src/main/resources/application-mysql.properties`.

## Identifiants de test (H2)

En local (H2), voici les comptes par defaut :
- **Enseignant** : `enseignant1` / `pass123`
- **Jury** : `jury1` / `pass123`

---

## Propositions d'amélioration

Voici quelques fonctionnalités suggérées pour enrichir le projet :

1. **UI plus complete** : formulaires avances, filtres, export PDF.
2. **Calcul des Moyennes** : Ajouter un module pour calculer automatiquement les moyennes par semestre et par étudiant.
3. **Exportation de Rapports** : Générer des relevés de notes au format PDF ou Excel.
4. **Gestion des Absences** : Intégrer un système de suivi des présences lié aux performances académiques.
5. **Notifications** : Envoyer des alertes (Email/SMS) aux étudiants lors de la publication ou de la modification d'une note.
