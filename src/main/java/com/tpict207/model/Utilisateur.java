package com.tpict207.model;

public class Utilisateur {
    private int id;
    private String nomUtilisateur;
    private String motDePasse;
    private String role; // ENSEIGNANT, JURY

    public Utilisateur() {}
    public Utilisateur(int id, String nomUtilisateur, String role) {
        this.id = id;
        this.nomUtilisateur = nomUtilisateur;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNomUtilisateur() { return nomUtilisateur; }
    public void setNomUtilisateur(String nomUtilisateur) { this.nomUtilisateur = nomUtilisateur; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
