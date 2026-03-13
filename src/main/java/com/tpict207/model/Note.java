package com.tpict207.model;

public class Note {
    private int id;
    private int etudiantId;
    private int coursId;
    private double valeur;
    private int enseignantId;

    public Note() {}
    public Note(int id, int etudiantId, int coursId, double valeur, int enseignantId) {
        this.id = id;
        this.etudiantId = etudiantId;
        this.coursId = coursId;
        this.valeur = valeur;
        this.enseignantId = enseignantId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEtudiantId() { return etudiantId; }
    public void setEtudiantId(int etudiantId) { this.etudiantId = etudiantId; }
    public int getCoursId() { return coursId; }
    public void setCoursId(int coursId) { this.coursId = coursId; }
    public double getValeur() { return valeur; }
    public void setValeur(double valeur) { this.valeur = valeur; }
    public int getEnseignantId() { return enseignantId; }
    public void setEnseignantId(int enseignantId) { this.enseignantId = enseignantId; }
}
