package com.tpict207.web.dto;

import java.time.LocalDateTime;

import static com.tpict207.web.dto.CoursDtos.CoursDto;
import static com.tpict207.web.dto.EtudiantDtos.EtudiantDto;
import static com.tpict207.web.dto.UtilisateurDtos.UtilisateurDto;

public final class NoteDtos {
    private NoteDtos() {}

    public record NoteDto(Long id, EtudiantDto etudiant, CoursDto cours, Double valeur, UtilisateurDto enseignant) {}

    public record CreateNoteRequest(Long etudiantId, Long coursId, Double valeur) {}

    public record UpdateNoteRequest(Double nouvelleValeur, String motif) {}

    public record HistoriqueNoteDto(
            Long id,
            Long noteId,
            Double ancienneNote,
            Double nouvelleNote,
            String motif,
            UtilisateurDto modifiePar,
            LocalDateTime dateModification
    ) {}
}

