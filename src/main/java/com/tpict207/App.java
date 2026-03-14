package com.tpict207;

import com.tpict207.dao.EtudiantDAO;
import com.tpict207.dao.NoteDAO;
import com.tpict207.dao.UtilisateurDAO;
import com.tpict207.model.Etudiant;
import com.tpict207.model.Note;
import com.tpict207.model.Utilisateur;
import com.tpict207.service.ServiceNote;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class App extends Application {
    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private EtudiantDAO etudiantDAO = new EtudiantDAO();
    private ServiceNote serviceNote = new ServiceNote();
    private Utilisateur utilisateurConnecte;

    @Override
    public void start(Stage primaryStage) {
        showLoginScene(primaryStage);
    }

    private void showLoginScene(Stage stage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f4f4f4;");

        Label title = new Label("Grade Validation System - Login");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TextField loginField = new TextField();
        loginField.setPromptText("Nom d'utilisateur");
        loginField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.setMaxWidth(250);

        Button loginButton = new Button("Se connecter");
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        loginButton.setMinWidth(100);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #e74c3c;");

        loginButton.setOnAction(e -> {
            String login = loginField.getText();
            String password = passwordField.getText();
            utilisateurConnecte = utilisateurDAO.connexion(login, password);

            if (utilisateurConnecte != null) {
                showMainScene(stage);
            } else {
                errorLabel.setText("Identifiants incorrects.");
            }
        });

        root.getChildren().addAll(title, loginField, passwordField, loginButton, errorLabel);

        Scene scene = new Scene(root, 400, 350);
        stage.setTitle("Connexion");
        stage.setScene(scene);
        stage.show();
    }

    private void showMainScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Header
        HBox header = new HBox(20);
        header.setPadding(new Insets(10, 0, 20, 0));
        header.setAlignment(Pos.CENTER_LEFT);
        Label userLabel = new Label("Connecté : " + utilisateurConnecte.getNomUtilisateur() + " (" + utilisateurConnecte.getRole() + ")");
        userLabel.setStyle("-fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button logoutBtn = new Button("Déconnexion");
        logoutBtn.setOnAction(e -> showLoginScene(stage));
        header.getChildren().addAll(userLabel, spacer, logoutBtn);
        root.setTop(header);

        // Navigation (Left)
        VBox nav = new VBox(10);
        nav.setPadding(new Insets(0, 15, 0, 0));
        Button btnStudents = new Button("Liste des Étudiants");
        Button btnNotes = new Button("Liste des Notes");
        btnStudents.setMaxWidth(Double.MAX_VALUE);
        btnNotes.setMaxWidth(Double.MAX_VALUE);
        nav.getChildren().addAll(btnStudents, btnNotes);
        root.setLeft(nav);

        // Center Content
        VBox center = new VBox(10);
        center.setPadding(new Insets(0, 0, 0, 10));
        root.setCenter(center);

        btnStudents.setOnAction(e -> showStudents(center));
        btnNotes.setOnAction(e -> showNotes(center));

        // Initial View
        showStudents(center);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Système de Validation des Notes");
        stage.setScene(scene);
    }

    private void showStudents(VBox container) {
        container.getChildren().clear();
        Label title = new Label("Étudiants");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ListView<String> listView = new ListView<>();
        List<Etudiant> etudiants = etudiantDAO.getTousLesEtudiants();
        for (Etudiant e : etudiants) {
            listView.getItems().add(e.getNom() + " (Matricule: " + e.getMatricule() + ")");
        }

        container.getChildren().addAll(title, listView);
    }

    private void showNotes(VBox container) {
        container.getChildren().clear();
        Label title = new Label("Notes");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ListView<String> listView = new ListView<>();
        List<Note> notes = serviceNote.getToutesLesNotes();
        for (Note n : notes) {
            listView.getItems().add("Note ID: " + n.getId() + " | Étudiant ID: " + n.getEtudiantId() + " | Valeur: " + n.getValeur());
        }

        container.getChildren().addAll(title, listView);
    }

    public static void main(String[] args) {
        launch(args);
    }
}