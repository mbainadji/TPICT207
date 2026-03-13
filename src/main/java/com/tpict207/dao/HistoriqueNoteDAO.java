package com.tpict207.dao;

import com.tpict207.model.HistoriqueNote;
import com.tpict207.util.ConfigurationBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoriqueNoteDAO {
    public boolean enregistrerChangement(int noteId, double ancienneNote, double nouvelleNote, String motif, int modifieParId) {
        String requete = "INSERT INTO historique_notes (note_id, ancienne_note, nouvelle_note, motif_modification, modifie_par_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConfigurationBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {
            pstmt.setInt(1, noteId);
            pstmt.setDouble(2, ancienneNote);
            pstmt.setDouble(3, nouvelleNote);
            pstmt.setString(4, motif);
            pstmt.setInt(5, modifieParId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<HistoriqueNote> getHistorique() {
        List<HistoriqueNote> historique = new ArrayList<>();
        String requete = "SELECT * FROM historique_notes ORDER BY date_modification DESC";
        try (Connection conn = ConfigurationBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(requete)) {
            while (rs.next()) {
                HistoriqueNote changement = new HistoriqueNote();
                changement.setId(rs.getInt("id"));
                changement.setNoteId(rs.getInt("note_id"));
                changement.setAncienneNote(rs.getDouble("ancienne_note"));
                changement.setNouvelleNote(rs.getDouble("nouvelle_note"));
                changement.setMotif(rs.getString("motif_modification"));
                changement.setModifieParId(rs.getInt("modifie_par_id"));
                changement.setDateModification(rs.getTimestamp("date_modification"));
                historique.add(changement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historique;
    }
}
