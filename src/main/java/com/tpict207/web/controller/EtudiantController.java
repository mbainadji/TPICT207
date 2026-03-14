package com.tpict207.web.controller;

import com.tpict207.model.Etudiant;
import com.tpict207.service.EtudiantService;
import com.tpict207.web.dto.EtudiantDtos;
import com.tpict207.web.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/etudiants")
@RequiredArgsConstructor
public class EtudiantController {
    private final EtudiantService etudiantService;

    @GetMapping
    public List<EtudiantDtos.EtudiantDto> list() {
        return etudiantService.getAllEtudiants().stream().map(DtoMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public EtudiantDtos.EtudiantDto get(@PathVariable Long id) {
        Etudiant e = etudiantService.getEtudiantById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Etudiant introuvable"));
        return DtoMapper.toDto(e);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EtudiantDtos.EtudiantDto create(@RequestBody EtudiantDtos.CreateEtudiantRequest req) {
        if (req == null || req.nom() == null || req.nom().isBlank() || req.matricule() == null || req.matricule().isBlank()) {
            throw new IllegalArgumentException("nom et matricule sont obligatoires");
        }
        Etudiant created = etudiantService.addEtudiant(req.nom().trim(), req.matricule().trim());
        return DtoMapper.toDto(created);
    }
}

