package com.tpict207.model;

public class Etudiant {
    private int id;
    private String nom;
    private String matricule;

    public Etudiant() {}
    public Etudiant(int id, String nom, String matricule) {
        this.id = id;
        this.nom = nom;
        this.matricule = matricule;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
}
