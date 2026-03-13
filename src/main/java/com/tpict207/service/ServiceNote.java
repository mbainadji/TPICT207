package com.tpict207.service;

import com.tpict207.dao.NoteDAO;
import com.tpict207.dao.HistoriqueNoteDAO;
import com.tpict207.model.Note;
import com.tpict207.model.Utilisateur;
import java.util.List;

public class ServiceNote {
    private NoteDAO noteDAO = new NoteDAO();
    private HistoriqueNoteDAO historiqueNoteDAO = new HistoriqueNoteDAO();

    public boolean ajouterNote(int etudiantId, int coursId, double valeur, int enseignantId) {
        if (valeur < 0 || valeur > 20) {
            System.err.println("La note doit être comprise entre 0 et 20.");
            return false;
        }
        Note note = new Note(0, etudiantId, coursId, valeur, enseignantId);
        return noteDAO.enregistrerNote(note);
    }

    public List<Note> getToutesLesNotes() {
        return noteDAO.getToutesLesNotes();
    }

    public boolean modifierNote(int noteId, double nouvelleValeur, String motif, Utilisateur jury) {
        if (!"JURY".equals(jury.getRole())) {
            System.err.println("Seul le Jury peut modifier les notes.");
            return false;
        }
        if (nouvelleValeur < 0 || nouvelleValeur > 20) {
            System.err.println("La nouvelle note doit être comprise entre 0 et 20.");
            return false;
        }

        Note ancienneNote = noteDAO.getNoteParId(noteId);
        if (ancienneNote == null) {
            System.err.println("Note introuvable.");
            return false;
        }

        boolean miseAJour = noteDAO.modifierNote(noteId, nouvelleValeur);
        if (miseAJour) {
            return historiqueNoteDAO.enregistrerChangement(noteId, ancienneNote.getValeur(), nouvelleValeur, motif, jury.getId());
        }
        return false;
    }
}
