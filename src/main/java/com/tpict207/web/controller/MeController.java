package com.tpict207.web.controller;

import com.tpict207.model.Utilisateur;
import com.tpict207.service.UtilisateurService;
import com.tpict207.web.dto.UtilisateurDtos;
import com.tpict207.web.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MeController {
    private final UtilisateurService utilisateurService;

    @GetMapping("/me")
    public UtilisateurDtos.UtilisateurDto me(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Non authentifie");
        }

        Utilisateur u = utilisateurService.getByNomUtilisateur(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Utilisateur introuvable"));

        return DtoMapper.toDtoSansMotDePasse(u);
    }
}

