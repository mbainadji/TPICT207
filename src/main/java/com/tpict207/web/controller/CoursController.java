package com.tpict207.web.controller;

import com.tpict207.model.Cours;
import com.tpict207.service.CoursService;
import com.tpict207.web.dto.CoursDtos;
import com.tpict207.web.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/cours")
@RequiredArgsConstructor
public class CoursController {
    private final CoursService coursService;

    @GetMapping
    public List<CoursDtos.CoursDto> list() {
        return coursService.getAllCours().stream().map(DtoMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public CoursDtos.CoursDto get(@PathVariable Long id) {
        Cours c = coursService.getCoursById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cours introuvable"));
        return DtoMapper.toDto(c);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CoursDtos.CoursDto create(@RequestBody CoursDtos.CreateCoursRequest req) {
        if (req == null || req.nom() == null || req.nom().isBlank() || req.code() == null || req.code().isBlank()) {
            throw new IllegalArgumentException("nom et code sont obligatoires");
        }
        Cours created = coursService.addCours(req.nom().trim(), req.code().trim());
        return DtoMapper.toDto(created);
    }
}

