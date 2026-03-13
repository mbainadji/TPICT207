package com.tpict207.dao;

import com.tpict207.model.Utilisateur;
import com.tpict207.util.ConfigurationBD;
import java.sql.*;

public class UtilisateurDAO {
    public Utilisateur connexion(String nomUtilisateur, String motDePasse) {
        String requete = "SELECT * FROM utilisateurs WHERE nom_utilisateur = ? AND mot_de_passe = ?";
        try (Connection conn = ConfigurationBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {
            pstmt.setString(1, nomUtilisateur);
            pstmt.setString(2, motDePasse);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setId(rs.getInt("id"));
                utilisateur.setNomUtilisateur(rs.getString("nom_utilisateur"));
                utilisateur.setRole(rs.getString("role"));
                return utilisateur;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
