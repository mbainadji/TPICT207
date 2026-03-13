package com.tpict207.model;

import java.sql.Timestamp;

public class HistoriqueNote {
    private int id;
    private int noteId;
    private double ancienneNote;
    private double nouvelleNote;
    private String motif;
    private int modifieParId;
    private Timestamp dateModification;

    public HistoriqueNote() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getNoteId() { return noteId; }
    public void setNoteId(int noteId) { this.noteId = noteId; }
    public double getAncienneNote() { return ancienneNote; }
    public void setAncienneNote(double ancienneNote) { this.ancienneNote = ancienneNote; }
    public double getNouvelleNote() { return nouvelleNote; }
    public void setNouvelleNote(double nouvelleNote) { this.nouvelleNote = nouvelleNote; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public int getModifieParId() { return modifieParId; }
    public void setModifieParId(int modifieParId) { this.modifieParId = modifieParId; }
    public Timestamp getDateModification() { return dateModification; }
    public void setDateModification(Timestamp dateModification) { this.dateModification = dateModification; }
}
