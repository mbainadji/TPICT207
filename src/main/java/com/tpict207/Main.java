package com.tpict207;

import com.tpict207.dao.EtudiantDAO;
import com.tpict207.dao.UtilisateurDAO;
import com.tpict207.dao.HistoriqueNoteDAO;
import com.tpict207.model.Etudiant;
import com.tpict207.model.Utilisateur;
import com.tpict207.model.Note;
import com.tpict207.model.HistoriqueNote;
import com.tpict207.service.ServiceNote;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        EtudiantDAO etudiantDAO = new EtudiantDAO();
        ServiceNote serviceNote = new ServiceNote();
        HistoriqueNoteDAO historiqueNoteDAO = new HistoriqueNoteDAO();

        System.out.println("--- 1. Authentification ---");
        Utilisateur enseignant = utilisateurDAO.connexion("enseignant1", "pass123");
        if (enseignant != null) {
            System.out.println("Connecté en tant que : " + enseignant.getNomUtilisateur() + " (Rôle : " + enseignant.getRole() + ")");
        }

        System.out.println("\n--- 2. Liste des étudiants ---");
        List<Etudiant> etudiants = etudiantDAO.getTousLesEtudiants();
        for (Etudiant e : etudiants) {
            System.out.println("ID : " + e.getId() + " | Nom : " + e.getNom() + " | Matricule : " + e.getMatricule());
        }

        System.out.println("\n--- 3. Saisie d'une note (Enseignant) ---");
        if (enseignant != null && "ENSEIGNANT".equals(enseignant.getRole())) {
            boolean succes = serviceNote.ajouterNote(1, 1, 15.5, enseignant.getId());
            if (succes) {
                System.out.println("Note de 15.5 ajoutée pour l'étudiant 1, cours 1");
            }
        }

        System.out.println("\n--- 4. Consultation des notes ---");
        List<Note> notes = serviceNote.getToutesLesNotes();
        for (Note n : notes) {
            System.out.println("ID Note : " + n.getId() + " | Etudiant ID : " + n.getEtudiantId() + " | Valeur : " + n.getValeur());
        }

        System.out.println("\n--- 5. Modification d'une note (Jury) ---");
        Utilisateur jury = utilisateurDAO.connexion("jury1", "pass123");
        if (jury != null && "JURY".equals(jury.getRole())) {
            if (!notes.isEmpty()) {
                int noteIdAModifier = notes.get(0).getId();
                boolean succes = serviceNote.modifierNote(noteIdAModifier, 17.0, "Participation excellente", jury);
                if (succes) {
                    System.out.println("Note ID " + noteIdAModifier + " modifiée à 17.0 par " + jury.getNomUtilisateur());
                }
            }
        }

        System.out.println("\n--- 6. Historique des modifications ---");
        List<HistoriqueNote> historique = historiqueNoteDAO.getHistorique();
        for (HistoriqueNote hn : historique) {
            System.out.println("ID Modif : " + hn.getId() + " | Note ID : " + hn.getNoteId() 
                + " | " + hn.getAncienneNote() + " -> " + hn.getNouvelleNote() 
                + " | Motif : " + hn.getMotif() + " | Date : " + hn.getDateModification());
        }
    }
}
