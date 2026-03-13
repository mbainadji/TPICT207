package com.tpict207.dao;

import com.tpict207.model.Cours;
import com.tpict207.util.ConfigurationBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursDAO {
    public List<Cours> getTousLesCours() {
        List<Cours> listeCours = new ArrayList<>();
        String requete = "SELECT * FROM cours";
        try (Connection conn = ConfigurationBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(requete)) {
            while (rs.next()) {
                listeCours.add(new Cours(rs.getInt("id"), rs.getString("nom"), rs.getString("code")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listeCours;
    }
}
