CREATE DATABASE IF NOT EXISTS notes_db;
USE notes_db;

-- Table des utilisateurs
CREATE TABLE IF NOT EXISTS utilisateurs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom_utilisateur VARCHAR(50) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    role ENUM('ENSEIGNANT', 'JURY') NOT NULL
);

-- Table des étudiants
CREATE TABLE IF NOT EXISTS etudiants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    matricule VARCHAR(20) NOT NULL UNIQUE
);

-- Table des cours
CREATE TABLE IF NOT EXISTS cours (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    code VARCHAR(10) NOT NULL UNIQUE
);

-- Table des notes
CREATE TABLE IF NOT EXISTS notes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    etudiant_id INT NOT NULL,
    cours_id INT NOT NULL,
    valeur DECIMAL(4, 2) NOT NULL CHECK (valeur >= 0 AND valeur <= 20),
    enseignant_id INT NOT NULL,
    FOREIGN KEY (etudiant_id) REFERENCES etudiants(id),
    FOREIGN KEY (cours_id) REFERENCES cours(id),
    FOREIGN KEY (enseignant_id) REFERENCES utilisateurs(id)
);

-- Table de l'historique des modifications
CREATE TABLE IF NOT EXISTS historique_notes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    note_id INT NOT NULL,
    ancienne_note DECIMAL(4, 2) NOT NULL,
    nouvelle_note DECIMAL(4, 2) NOT NULL,
    motif_modification TEXT NOT NULL,
    modifie_par_id INT NOT NULL,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (note_id) REFERENCES notes(id),
    FOREIGN KEY (modifie_par_id) REFERENCES utilisateurs(id)
);

-- Données initiales
INSERT INTO utilisateurs (nom_utilisateur, mot_de_passe, role) VALUES ('enseignant1', 'pass123', 'ENSEIGNANT'), ('jury1', 'pass123', 'JURY');
INSERT INTO etudiants (nom, matricule) VALUES ('Alice Smith', 'ET001'), ('Bob Jones', 'ET002');
INSERT INTO cours (nom, code) VALUES ('Programmation Java', 'CS101'), ('Systèmes de Base de Données', 'CS102');
