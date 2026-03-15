-- MySQL schema (production / Azure MySQL).
-- Note: database creation is intentionally omitted; manage it via your DB provisioning.

-- Table des utilisateurs
CREATE TABLE IF NOT EXISTS utilisateurs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom_utilisateur VARCHAR(50) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    role ENUM('ENSEIGNANT', 'JURY', 'ADMIN') NOT NULL
);

-- Table des étudiants
CREATE TABLE IF NOT EXISTS etudiants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    matricule VARCHAR(20) NOT NULL UNIQUE
);

-- Table des cours
CREATE TABLE IF NOT EXISTS cours (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    code VARCHAR(10) NOT NULL UNIQUE
);

-- Table des notes
CREATE TABLE IF NOT EXISTS notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    etudiant_id BIGINT NOT NULL,
    cours_id BIGINT NOT NULL,
    valeur DECIMAL(4, 2) NOT NULL,
    enseignant_id BIGINT NOT NULL,
    CONSTRAINT chk_notes_valeur CHECK (valeur >= 0 AND valeur <= 20),
    CONSTRAINT fk_notes_etudiant FOREIGN KEY (etudiant_id) REFERENCES etudiants(id),
    CONSTRAINT fk_notes_cours FOREIGN KEY (cours_id) REFERENCES cours(id),
    CONSTRAINT fk_notes_enseignant FOREIGN KEY (enseignant_id) REFERENCES utilisateurs(id)
);

-- Table de l'historique des modifications
CREATE TABLE IF NOT EXISTS historique_notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    note_id BIGINT NOT NULL,
    ancienne_note DECIMAL(4, 2) NOT NULL,
    nouvelle_note DECIMAL(4, 2) NOT NULL,
    motif_modification TEXT NOT NULL,
    modifie_par_id BIGINT NOT NULL,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hist_note FOREIGN KEY (note_id) REFERENCES notes(id),
    CONSTRAINT fk_hist_user FOREIGN KEY (modifie_par_id) REFERENCES utilisateurs(id)
);
