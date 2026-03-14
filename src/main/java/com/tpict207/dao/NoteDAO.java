package com.tpict207.dao;

import com.tpict207.model.Note;
import com.tpict207.util.ConfigurationBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDAO {
    public boolean enregistrerNote(Note note) {
        String requete = "INSERT INTO notes (etudiant_id, cours_id, valeur, enseignant_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConfigurationBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {
            pstmt.setInt(1, note.getEtudiantId());
            pstmt.setInt(2, note.getCoursId());
            pstmt.setDouble(3, note.getValeur());
            pstmt.setInt(4, note.getEnseignantId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Note> getToutesLesNotes() {
        List<Note> notes = new ArrayList<>();
        String requete = "SELECT * FROM notes";
        try (Connection conn = ConfigurationBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(requete)) {
            while (rs.next()) {
                notes.add(new Note(rs.getInt("id"), rs.getInt("etudiant_id"), rs.getInt("cours_id"), rs.getDouble("valeur"), rs.getInt("enseignant_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }

    public Note getNoteParId(int id) {
        String requete = "SELECT * FROM notes WHERE id = ?";
        try (Connection conn = ConfigurationBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Note(rs.getInt("id"), rs.getInt("etudiant_id"), rs.getInt("cours_id"), rs.getDouble("valeur"), rs.getInt("enseignant_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean modifierNote(int noteId, double nouvelleValeur) {
        String requete = "UPDATE notes SET valeur = ? WHERE id = ?";
        try (Connection conn = ConfigurationBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {
            pstmt.setDouble(1, nouvelleValeur);
            pstmt.setInt(2, noteId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getMoyenneEtudiant(int etudiantId) {
        String requete = "SELECT AVG(valeur) as moyenne FROM notes WHERE etudiant_id = ?";
        try (Connection conn = ConfigurationBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {
            pstmt.setInt(1, etudiantId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("moyenne");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public List<Note> getNotesParEtudiant(int etudiantId) {
        List<Note> notes = new ArrayList<>();
        String requete = "SELECT * FROM notes WHERE etudiant_id = ?";
        try (Connection conn = ConfigurationBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(requete)) {
            pstmt.setInt(1, etudiantId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                notes.add(new Note(rs.getInt("id"), rs.getInt("etudiant_id"), rs.getInt("cours_id"), rs.getDouble("valeur"), rs.getInt("enseignant_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }
}
