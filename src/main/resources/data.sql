-- Seed data for local (H2) runs.
-- Passwords use Spring Security's DelegatingPasswordEncoder format.
-- For real deployments, switch to bcrypt and rotate credentials.

MERGE INTO utilisateurs (nom_utilisateur, mot_de_passe, role)
KEY(nom_utilisateur)
VALUES ('enseignant1', '{noop}pass123', 'ENSEIGNANT');

MERGE INTO utilisateurs (nom_utilisateur, mot_de_passe, role)
KEY(nom_utilisateur)
VALUES ('jury1', '{noop}pass123', 'JURY');

MERGE INTO etudiants (nom, matricule)
KEY(matricule)
VALUES ('Alice Smith', 'ET001');

MERGE INTO etudiants (nom, matricule)
KEY(matricule)
VALUES ('Bob Jones', 'ET002');

MERGE INTO cours (nom, code)
KEY(code)
VALUES ('Programmation Java', 'CS101');

MERGE INTO cours (nom, code)
KEY(code)
VALUES ('Systemes de Base de Donnees', 'CS102');
