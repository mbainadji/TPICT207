package com.tpict207.model;

public class Cours {
    private int id;
    private String nom;
    private String code;

    public Cours() {}
    public Cours(int id, String nom, String code) {
        this.id = id;
        this.nom = nom;
        this.code = code;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
