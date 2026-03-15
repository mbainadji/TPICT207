package com.tpict207.service;

import com.tpict207.model.Cours;
import com.tpict207.model.Etudiant;
import com.tpict207.model.Note;
import com.tpict207.model.Utilisateur;
import com.tpict207.model.HistoriqueNote;
import com.tpict207.repository.NoteRepository;
import com.tpict207.repository.HistoriqueNoteRepository;
import com.tpict207.repository.EtudiantRepository;
import com.tpict207.repository.CoursRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final HistoriqueNoteRepository historiqueNoteRepository;
    private final EtudiantRepository etudiantRepository;
    private final CoursRepository coursRepository;

    @Transactional
    public Note ajouterNote(Long etudiantId, Long coursId, Double valeur, Utilisateur enseignant) {
        if (valeur < 0 || valeur > 20) {
            throw new IllegalArgumentException("La note doit être comprise entre 0 et 20.");
        }
        if (enseignant.getRole() != Utilisateur.Role.ENSEIGNANT) {
            throw new RuntimeException("Seul un enseignant peut ajouter une note.");
        }

        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant introuvable"));
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new RuntimeException("Cours introuvable"));

        Note note = new Note(null, etudiant, cours, valeur, enseignant);
        return noteRepository.save(note);
    }

    public List<Note> getToutesLesNotes() {
        return noteRepository.findAll();
    }

    @Transactional
    public Note modifierNote(Long noteId, Double nouvelleValeur, String motif, Utilisateur jury) {
        if (jury.getRole() != Utilisateur.Role.JURY) {
            throw new RuntimeException("Seul le Jury peut modifier les notes.");
        }
        if (nouvelleValeur < 0 || nouvelleValeur > 20) {
            throw new IllegalArgumentException("La nouvelle note doit être comprise entre 0 et 20.");
        }

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note introuvable"));

        Double ancienneValeur = note.getValeur();
        note.setValeur(nouvelleValeur);
        Note updatedNote = noteRepository.save(note);

        HistoriqueNote historique = new HistoriqueNote(null, updatedNote, ancienneValeur, nouvelleValeur, motif, jury, null);
        historiqueNoteRepository.save(historique);

        return updatedNote;
    }

    public Double calculerMoyenneEtudiant(Long etudiantId) {
        List<Note> notes = noteRepository.findByEtudiant_Id(etudiantId);
        if (notes.isEmpty()) return 0.0;
        return notes.stream().mapToDouble(Note::getValeur).average().orElse(0.0);
    }

    public String genererReleveNotesCSV(Long etudiantId) {
        List<Note> notes = noteRepository.findByEtudiant_Id(etudiantId);
        StringBuilder csv = new StringBuilder("NoteID,CoursID,Valeur,EnseignantID\n");
        for (Note n : notes) {
            csv.append(n.getId()).append(",")
               .append(n.getCours().getId()).append(",")
               .append(n.getValeur()).append(",")
               .append(n.getEnseignant().getId()).append("\n");
        }
        csv.append("Moyenne,,")
           .append(calculerMoyenneEtudiant(etudiantId)).append(",\n");
        return csv.toString();
    }
}
