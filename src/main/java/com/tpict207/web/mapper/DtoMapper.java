package com.tpict207.web.mapper;

import com.tpict207.model.Cours;
import com.tpict207.model.Etudiant;
import com.tpict207.model.HistoriqueNote;
import com.tpict207.model.Note;
import com.tpict207.model.Utilisateur;
import com.tpict207.web.dto.CoursDtos;
import com.tpict207.web.dto.EtudiantDtos;
import com.tpict207.web.dto.NoteDtos;
import com.tpict207.web.dto.UtilisateurDtos;

public final class DtoMapper {
    private DtoMapper() {}

    public static EtudiantDtos.EtudiantDto toDto(Etudiant e) {
        return new EtudiantDtos.EtudiantDto(e.getId(), e.getNom(), e.getMatricule());
    }

    public static CoursDtos.CoursDto toDto(Cours c) {
        return new CoursDtos.CoursDto(c.getId(), c.getNom(), c.getCode());
    }

    public static UtilisateurDtos.UtilisateurDto toDtoSansMotDePasse(Utilisateur u) {
        return new UtilisateurDtos.UtilisateurDto(u.getId(), u.getNomUtilisateur(), u.getRole().name());
    }

    public static NoteDtos.NoteDto toDto(Note n) {
        return new NoteDtos.NoteDto(
                n.getId(),
                toDto(n.getEtudiant()),
                toDto(n.getCours()),
                n.getValeur(),
                toDtoSansMotDePasse(n.getEnseignant())
        );
    }

    public static NoteDtos.HistoriqueNoteDto toDto(HistoriqueNote h) {
        return new NoteDtos.HistoriqueNoteDto(
                h.getId(),
                h.getNote().getId(),
                h.getAncienneNote(),
                h.getNouvelleNote(),
                h.getMotif(),
                toDtoSansMotDePasse(h.getModifiePar()),
                h.getDateModification()
        );
    }
}

