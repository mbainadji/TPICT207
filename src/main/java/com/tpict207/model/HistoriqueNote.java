package com.tpict207.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "historique_notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @Column(name = "ancienne_note", nullable = false)
    private Double ancienneNote;

    @Column(name = "nouvelle_note", nullable = false)
    private Double nouvelleNote;

    @Column(name = "motif_modification", nullable = false, columnDefinition = "TEXT")
    private String motif;

    @ManyToOne
    @JoinColumn(name = "modifie_par_id", nullable = false)
    private Utilisateur modifiePar;

    @CreationTimestamp
    @Column(name = "date_modification", updatable = false)
    private LocalDateTime dateModification;
}
