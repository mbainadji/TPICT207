package com.tpict207.web.controller;

import com.tpict207.model.HistoriqueNote;
import com.tpict207.model.Note;
import com.tpict207.model.Utilisateur;
import com.tpict207.repository.HistoriqueNoteRepository;
import com.tpict207.service.NoteService;
import com.tpict207.service.UtilisateurService;
import com.tpict207.web.dto.NoteDtos;
import com.tpict207.web.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;
    private final UtilisateurService utilisateurService;
    private final HistoriqueNoteRepository historiqueNoteRepository;

    @GetMapping("/notes")
    public List<NoteDtos.NoteDto> listNotes() {
        return noteService.getToutesLesNotes().stream().map(DtoMapper::toDto).toList();
    }

    @PostMapping("/notes")
    @ResponseStatus(CREATED)
    public NoteDtos.NoteDto createNote(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody NoteDtos.CreateNoteRequest req
    ) {
        Utilisateur enseignant = requireUtilisateur(principal);
        if (req == null || req.etudiantId() == null || req.coursId() == null || req.valeur() == null) {
            throw new IllegalArgumentException("etudiantId, coursId et valeur sont obligatoires");
        }

        Note created = noteService.ajouterNote(req.etudiantId(), req.coursId(), req.valeur(), enseignant);
        return DtoMapper.toDto(created);
    }

    @PutMapping("/notes/{noteId}")
    public NoteDtos.NoteDto updateNote(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long noteId,
            @RequestBody NoteDtos.UpdateNoteRequest req
    ) {
        Utilisateur jury = requireUtilisateur(principal);
        if (req == null || req.nouvelleValeur() == null || req.motif() == null || req.motif().isBlank()) {
            throw new IllegalArgumentException("nouvelleValeur et motif sont obligatoires");
        }

        Note updated = noteService.modifierNote(noteId, req.nouvelleValeur(), req.motif().trim(), jury);
        return DtoMapper.toDto(updated);
    }

    @GetMapping("/notes/{noteId}/historique")
    public List<NoteDtos.HistoriqueNoteDto> historique(@PathVariable Long noteId) {
        List<HistoriqueNote> history = historiqueNoteRepository.findByNote_IdOrderByDateModificationDesc(noteId);
        return history.stream().map(DtoMapper::toDto).toList();
    }

    @GetMapping("/etudiants/{etudiantId}/moyenne")
    public ResponseEntity<Double> moyenne(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(noteService.calculerMoyenneEtudiant(etudiantId));
    }

    @GetMapping("/etudiants/{etudiantId}/releve.csv")
    public ResponseEntity<byte[]> releveCsv(@PathVariable Long etudiantId) {
        String csv = noteService.genererReleveNotesCSV(etudiantId);
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"releve-etudiant-" + etudiantId + ".csv\"")
                .contentType(new MediaType("text", "csv"))
                .body(bytes);
    }

    private Utilisateur requireUtilisateur(UserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Non authentifie");
        }

        return utilisateurService.getByNomUtilisateur(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Utilisateur introuvable"));
    }
}

