# Grade Validation Backend

Ce projet est un système de gestion et de validation des notes pour les étudiants, développé en **Java 17** avec **Maven**.

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
- **Base de données** : 
    - Supporte **MySQL** (avec intégration Azure Identity pour le cloud).
    - Mode de repli automatique sur une base de données **H2 en mémoire** pour les tests locaux si MySQL n'est pas disponible.
- **Patron de conception** : Architecture DAO (Data Access Object) pour séparer la logique métier de l'accès aux données.

## Installation et Utilisation

1. **Prérequis** : Java 17 et Maven installés.
2. **Compilation** :
   ```bash
   mvn clean compile
   ```
3. **Exécution** :
   ```bash
   mvn exec:java
   ```

## Identifiants de test (H2)

Si vous utilisez la base de données de repli (H2), voici les comptes par défaut :
- **Enseignant** : `enseignant1` / `pass123`
- **Jury** : `jury1` / `pass123`

---

## Propositions d'amélioration

Voici quelques fonctionnalités suggérées pour enrichir le projet :

1. **Interface Web ou Desktop** : Remplacer l'interface en ligne de commande par une interface graphique (JavaFX ou Spring Boot + React).
2. **Calcul des Moyennes** : Ajouter un module pour calculer automatiquement les moyennes par semestre et par étudiant.
3. **Exportation de Rapports** : Générer des relevés de notes au format PDF ou Excel.
4. **Gestion des Absences** : Intégrer un système de suivi des présences lié aux performances académiques.
5. **Notifications** : Envoyer des alertes (Email/SMS) aux étudiants lors de la publication ou de la modification d'une note.
