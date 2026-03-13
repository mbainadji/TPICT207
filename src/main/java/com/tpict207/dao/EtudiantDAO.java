package com.tpict207.dao;

import com.tpict207.model.Etudiant;
import com.tpict207.util.ConfigurationBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EtudiantDAO {
    public List<Etudiant> getTousLesEtudiants() {
        List<Etudiant> etudiants = new ArrayList<>();
        String requete = "SELECT * FROM etudiants";
        try (Connection conn = ConfigurationBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(requete)) {
            while (rs.next()) {
                etudiants.add(new Etudiant(rs.getInt("id"), rs.getString("nom"), rs.getString("matricule")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return etudiants;
    }
}
