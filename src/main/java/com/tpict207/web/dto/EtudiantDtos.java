package com.tpict207.web.dto;

public final class EtudiantDtos {
    private EtudiantDtos() {}

    public record EtudiantDto(Long id, String nom, String matricule) {}

    public record CreateEtudiantRequest(String nom, String matricule) {}
}

