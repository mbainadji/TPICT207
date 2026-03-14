package com.tpict207.web.dto;

public final class UtilisateurDtos {
    private UtilisateurDtos() {}

    public record UtilisateurDto(Long id, String nomUtilisateur, String role) {}
}

