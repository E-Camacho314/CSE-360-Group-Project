package cse360helpsystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateAccount extends VBox {
    private CSE360HelpSystem mainApp;
    private static final DatabaseHelper databaseHelper = new DatabaseHelper();
    private String invitationCode;
    private Label titleLabel;
    private Label emailLabel;
    private Label passwordLabel;
    private Label confirmPassLabel;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPassField;
    private Button submitButton;
    private Button backButton;
    private Label messageLabel;

    public CreateAccount(CSE360HelpSystem mainApp, String invitationCode) {
        this.mainApp = mainApp;
        this.invitationCode = invitationCode;
        initializeUI();
    }

    private void initializeUI() {
        // Title
        titleLabel = new Label("Create Account");
        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setFont(Font.font(16));

        // Email
        emailLabel = new Label("Email:");
        emailLabel.setTextFill(Color.BLACK);
        emailLabel.setFont(Font.font(14));
        emailField = new TextField();
        emailField.setPromptText("Enter your email");

        // Password
        passwordLabel = new Label("Password:");
        passwordLabel.setTextFill(Color.BLACK);
        passwordLabel.setFont(Font.font(14));
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");

        // Confirm Password
        confirmPassLabel = new Label("Confirm Password:");
        confirmPassLabel.setTextFill(Color.BLACK);
        confirmPassLabel.setFont(Font.font(14));
        confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Re-enter your password");

        // Buttons
        submitButton = new Button("Submit");
        submitButton.setFont(Font.font(14));
        backButton = new Button("Back to Login");
        backButton.setFont(Font.font(14));

        // Message Label
        messageLabel = new Label();
        messageLabel.setTextFill(Color.BLACK);
        messageLabel.setFont(Font.font(14));
        
        // Layout using GridPane
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));
        
        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(confirmPassLabel, 0, 3);
        grid.add(confirmPassField, 1, 3);
        grid.add(submitButton, 1, 4);
        grid.add(messageLabel, 0, 6, 2, 1);
        
        try {
        	if(mainApp.databaseHelper.isDatabaseEmpty()) {
        		titleLabel.setText("First Admin Account Creation");
        		emailLabel.setText("Username:");
        		emailField.setPromptText("Enter your username");
        		
        	}
        	else {
                grid.add(backButton, 1, 5);
        	}
    	}
    	catch(SQLException e) {
			System.err.println("Database error: " + e.getMessage());
			e.printStackTrace();
    	}
    	finally {
    		System.out.println("First User Account Creation");
    	}

        // Align buttons to the right
        GridPane.setMargin(submitButton, new Insets(10, 0, 0, 0));
        GridPane.setMargin(backButton, new Insets(10, 0, 0, 0));

        // Add GridPane to VBox
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(grid);

        // Button Actions
        submitButton.setOnAction(e -> handleSubmit());
        backButton.setOnAction(e -> {
            mainApp.showLoginPage(); // Switch back to the login page
        });
    }

    private void handleSubmit() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPassField.getText();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match.");
            return;
        }

        try {
        	String role;
        	boolean success = false;
            if (mainApp.databaseHelper.isDatabaseEmpty()) {
                mainApp.databaseHelper.register(email, confirmPassword, 1, 0, 0);
                success = true;
            } else {
            	if (invitationCode == null || invitationCode.trim().isEmpty()) {
                    messageLabel.setText("Invitation code is required.");
                    return;
                }
                role = consumeInvitationCode(invitationCode.trim());
                if (role == null) {
                    messageLabel.setText("Invalid or already used invitation code.");
                    return;
                }
            }
            if (success) {
                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText("Account created successfully! Redirecting to login...");
                // Redirect back to login after a short delay
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        // Handle exception
                    }
                    javafx.application.Platform.runLater(() -> mainApp.showLoginPage());
                }).start();
            } else {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Email already exists.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("An error occurred during account creation.");
        }
    }
/* These methods are handled by databaseHelper class
    // Example method to register a user
    private boolean register(String email, String password, String role) throws SQLException {
        // Example database connection - update with your connection details
        String url = "jdbc:mysql://localhost:3306/yourDatabase"; // Replace with your DB URL
        String user = "yourUsername";
        String pass = "yourPassword";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String query = "INSERT INTO users (email, password, role) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                pstmt.setString(3, role);
                return pstmt.executeUpdate() > 0; // Returns true if insertion was successful
            }
        }
    }

    // method to check if the database is empty
    private boolean isDatabaseEmpty() throws SQLException {
        // Implement logic to check if the users table is empty
        return false; // Update with actual logic
    }
    */

    // method to consume invitation code
    private String consumeInvitationCode(String code) {
        // Implement logic to validate and consume the invitation code
        return "user"; // Update with actual logic
    }
}
