package com.example.lab5java;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;

public class Game extends Application {
    private Connection connection;

    public void start(Stage primaryStage) {
        connectToDatabase();

        Label lblPlayerId = new Label("Player ID:");
        TextField txtPlayerId = new TextField();
        Label lblName = new Label("Name:");
        TextField txtName = new TextField();
        Label lblEmail = new Label("Email:");
        TextField txtEmail = new TextField();

        Button btnInsertPlayer = new Button("Insert Player");
        Button btnUpdatePlayer = new Button("Update Player");
        Button btnDisplayReport = new Button("Display Report");

        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);

        GridPane inputPane = new GridPane();
        inputPane.setHgap(10);
        inputPane.setVgap(10);
        inputPane.setPadding(new Insets(10));
        inputPane.add(lblPlayerId, 0, 0);
        inputPane.add(txtPlayerId, 1, 0);
        inputPane.add(lblName, 0, 1);
        inputPane.add(txtName, 1, 1);
        inputPane.add(lblEmail, 0, 2);
        inputPane.add(txtEmail, 1, 2);

        HBox buttonPane = new HBox(10, btnInsertPlayer, btnUpdatePlayer, btnDisplayReport);
        buttonPane.setPadding(new Insets(10));

        VBox mainLayout = new VBox(10, inputPane, buttonPane, reportArea);
        mainLayout.setPadding(new Insets(15));

        btnInsertPlayer.setOnAction(e -> {
            try {
                int playerId = Integer.parseInt(txtPlayerId.getText());
                String name = txtName.getText();
                String email = txtEmail.getText();
                insertPlayer(playerId, name, email);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Player inserted");
            }
            catch (Exception ex)
            {
                System.out.println("Erorr cant insert player");
            }
        });

        btnUpdatePlayer.setOnAction(e -> {
            try {
                int playerId = Integer.parseInt(txtPlayerId.getText());
                String name = txtName.getText();
                String email = txtEmail.getText();
                updatePlayer(playerId, name, email);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Player updated");
            }
            catch (Exception ex)
            {
                System.out.println("Error cant update player");
            }
        });

        btnDisplayReport.setOnAction(e -> {
            try {
                String report = getPlayerGameReport();
                reportArea.setText(report);
            } catch (Exception ex) {
                System.out.println("Wrror cannot get report");
            }
        });

        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setTitle("Game Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void connectToDatabase() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@199.212.26.208:1521:SQLD",
                    "COMP228_F24_soh_44",
                    "password"
            );
        }
        catch (Exception e)
        {
            System.out.println("Error cannot connect to database");
        }
    }

    private void insertPlayer(int playerId, String name, String email) throws SQLException {
        String query = "INSERT INTO Fatimah_Hatia_player (player_id, name, email) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, playerId);
            stmt.setString(2, name);
            stmt.setString(3, email);
            stmt.executeUpdate();
        }
    }

    private void updatePlayer(int playerId, String name, String email) throws SQLException {
        String query = "UPDATE Fatimah_Hatia_player SET name = ?, email = ? WHERE player_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setInt(3, playerId);
            stmt.executeUpdate();
        }
    }

    private String getPlayerGameReport() throws SQLException {
        String query = """
                SELECT p.player_id, p.name, p.email, g.title
                FROM Fatimah_Hatia_player p
                LEFT JOIN Fatimah_Hatia_player_and_game pg ON p.player_id = pg.player_id
                LEFT JOIN Fatimah_Hatia_game g ON pg.game_id = g.game_id
                """;
        StringBuilder report = new StringBuilder();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            report.append("Player ID\tName\tEmail\tGame Title\n");
            report.append("*****************************************************\n");
            while (rs.next()) {
                report.append(rs.getInt("player_id")).append("\t")
                        .append(rs.getString("name")).append("\t")
                        .append(rs.getString("email")).append("\t")
                        .append(rs.getString("title") == null ? "N/A" : rs.getString("title")).append("\n");
            }
        }
        return report.toString();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}