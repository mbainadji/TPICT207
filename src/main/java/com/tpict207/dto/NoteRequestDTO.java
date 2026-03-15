package com.tpict207.dto;

import lombok.Data;

@Data
public class NoteRequestDTO {
    private Long etudiantId;
    private Long coursId;
    private Double valeur;
    private Long enseignantId;
}
