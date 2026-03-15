-- MySQL seed data (idempotent).
-- Passwords use Spring Security's DelegatingPasswordEncoder format.
-- For real deployments, replace {noop} with bcrypt and rotate credentials.

INSERT INTO utilisateurs (nom_utilisateur, mot_de_passe, role)
VALUES ('enseignant1', '{noop}pass123', 'ENSEIGNANT')
ON DUPLICATE KEY UPDATE mot_de_passe = VALUES(mot_de_passe), role = VALUES(role);

INSERT INTO utilisateurs (nom_utilisateur, mot_de_passe, role)
VALUES ('jury1', '{noop}pass123', 'JURY')
ON DUPLICATE KEY UPDATE mot_de_passe = VALUES(mot_de_passe), role = VALUES(role);

INSERT INTO etudiants (nom, matricule)
VALUES ('Alice Smith', 'ET001')
ON DUPLICATE KEY UPDATE nom = VALUES(nom);

INSERT INTO etudiants (nom, matricule)
VALUES ('Bob Jones', 'ET002')
ON DUPLICATE KEY UPDATE nom = VALUES(nom);

INSERT INTO cours (nom, code)
VALUES ('Programmation Java', 'CS101')
ON DUPLICATE KEY UPDATE nom = VALUES(nom);

INSERT INTO cours (nom, code)
VALUES ('Systemes de Base de Donnees', 'CS102')
ON DUPLICATE KEY UPDATE nom = VALUES(nom);

