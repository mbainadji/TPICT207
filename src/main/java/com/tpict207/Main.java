package com.tpict207;

import com.tpict207.dao.CoursDAO;
import com.tpict207.dao.EtudiantDAO;
import com.tpict207.dao.UtilisateurDAO;
import com.tpict207.dao.HistoriqueNoteDAO;
import com.tpict207.model.Cours;
import com.tpict207.model.Etudiant;
import com.tpict207.model.Utilisateur;
import com.tpict207.model.Note;
import com.tpict207.model.HistoriqueNote;
import com.tpict207.service.ServiceNote;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        EtudiantDAO etudiantDAO = new EtudiantDAO();
        CoursDAO coursDAO = new CoursDAO();
        ServiceNote serviceNote = new ServiceNote();
        HistoriqueNoteDAO historiqueNoteDAO = new HistoriqueNoteDAO();

        Scanner scanner = new Scanner(System.in);

        System.out.println("--- Authentification ---");
        System.out.print("Nom d'utilisateur: ");
        String login = scanner.nextLine().trim();
        System.out.print("Mot de passe: ");
        String password = scanner.nextLine().trim();

        Utilisateur utilisateur = utilisateurDAO.connexion(login, password);
        if (utilisateur == null) {
            System.err.println("Échec de l'authentification. Vérifiez vos identifiants.");
            scanner.close();
            return;
        }

        System.out.println("Connecté en tant que : " + utilisateur.getNomUtilisateur() + " (Rôle : " + utilisateur.getRole() + ")");

        while (true) {
            System.out.println("\n--- Menu ---");
            System.out.println("1) Voir la liste des étudiants");
            System.out.println("2) Ajouter un étudiant");
            System.out.println("3) Voir la liste des cours");
            System.out.println("4) Ajouter une note");
            System.out.println("5) Voir toutes les notes");
            System.out.println("6) Modifier une note (Jury uniquement)");
            System.out.println("7) Voir l'historique des modifications");
            System.out.println("8) Voir la moyenne d'un étudiant");
            System.out.println("9) Exporter le relevé de notes (CSV)");
            System.out.println("0) Quitter");
            System.out.print("Choix: ");

            String choix = scanner.nextLine().trim();
            switch (choix) {
                case "1":
                    List<Etudiant> etudiants = etudiantDAO.getTousLesEtudiants();
                    System.out.println("--- Étudiants ---");
                    for (Etudiant e : etudiants) {
                        System.out.println("ID : " + e.getId() + " | Nom : " + e.getNom() + " | Matricule : " + e.getMatricule());
                    }
                    break;
                case "2":
                    System.out.print("Nom de l'étudiant: ");
                    String nomEtudiant = scanner.nextLine().trim();
                    System.out.print("Matricule: ");
                    String matricule = scanner.nextLine().trim();
                    boolean ajoute = etudiantDAO.ajouterEtudiant(nomEtudiant, matricule);
                    System.out.println(ajoute ? "Étudiant ajouté." : "Échec de l'ajout de l'étudiant.");
                    break;
                case "3":
                    List<Cours> cours = coursDAO.getTousLesCours();
                    System.out.println("--- Cours ---");
                    for (Cours c : cours) {
                        System.out.println("ID : " + c.getId() + " | Nom : " + c.getNom() + " | Code : " + c.getCode());
                    }
                    break;
                case "4":
                    System.out.print("ID étudiant : ");
                    int etudiantId = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("ID cours : ");
                    int coursId = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Valeur de la note (0-20) : ");
                    double valeur = Double.parseDouble(scanner.nextLine().trim());
                    boolean succes = serviceNote.ajouterNote(etudiantId, coursId, valeur, utilisateur.getId());
                    System.out.println(succes ? "Note ajoutée." : "Échec de l'ajout de la note.");
                    break;
                case "5":
                    List<Note> notes = serviceNote.getToutesLesNotes();
                    System.out.println("--- Notes ---");
                    for (Note n : notes) {
                        System.out.println("ID Note : " + n.getId() + " | Etudiant ID : " + n.getEtudiantId() + " | Valeur : " + n.getValeur());
                    }
                    break;
                case "6":
                    if (!"JURY".equals(utilisateur.getRole())) {
                        System.err.println("Seul le Jury peut modifier les notes.");
                        break;
                    }
                    System.out.print("ID de la note à modifier : ");
                    int noteId = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Nouvelle valeur (0-20) : ");
                    double nouvelleValeur = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Motif de la modification : ");
                    String motif = scanner.nextLine().trim();
                    boolean modifie = serviceNote.modifierNote(noteId, nouvelleValeur, motif, utilisateur);
                    System.out.println(modifie ? "Note modifiée." : "Échec de la modification.");
                    break;
                case "7":
                    List<HistoriqueNote> historique = historiqueNoteDAO.getHistorique();
                    System.out.println("--- Historique des modifications ---");
                    for (HistoriqueNote hn : historique) {
                        System.out.println("ID Modif : " + hn.getId() + " | Note ID : " + hn.getNoteId()
                                + " | " + hn.getAncienneNote() + " -> " + hn.getNouvelleNote()
                                + " | Motif : " + hn.getMotif() + " | Date : " + hn.getDateModification());
                    }
                    break;
                case "8":
                    System.out.print("ID de l'étudiant : ");
                    int eId = Integer.parseInt(scanner.nextLine().trim());
                    double moyenne = serviceNote.calculerMoyenneEtudiant(eId);
                    System.out.println("Moyenne de l'étudiant : " + moyenne);
                    break;
                case "9":
                    System.out.print("ID de l'étudiant pour l'export : ");
                    int expId = Integer.parseInt(scanner.nextLine().trim());
                    String csv = serviceNote.genererReleveNotesCSV(expId);
                    System.out.println("--- Relevé de notes (CSV) ---");
                    System.out.println(csv);
                    break;
                case "0":
                    System.out.println("Au revoir.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }
}
