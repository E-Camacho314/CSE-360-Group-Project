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

/**
 * <p>NewPassword Class</p>
 * 
 * <p>Description: This class provides the UI for creating a new password if a user's account has been reset in the CSE360HelpSystem.
 * It validates user input and interacts with the database to create a new password.</p>
 * 
 * <p>Authors: Erik Camacho, Thienban Nguyen, Sarvesh Shanmugam, Ivan Mancillas, Tanis Peterson</p>
 */

public class NewPassword extends VBox {
	// Reference to the main application instance
    private CSE360HelpSystem mainApp;
    
    // Singleton for database operations
    private static final DatabaseHelper databaseHelper = new DatabaseHelper();
    
    // UI Components
    private String username;
    private Label titleLabel;
    private Label passwordLabel;
    private Label confirmPassLabel;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPassField;
    private Button submitButton;
    private Button backButton;
    private Label messageLabel;

    /**
     * Constructor for CreateAccount class.
     * Initializes the UI components and sets up the layout.
     *
     * @param mainApp        The main application instance.
     * @param invitationCode The invitation code for registration.
     */
    public NewPassword(CSE360HelpSystem mainApp, String username) {
        this.mainApp = mainApp;
        this.username = username;
        initializeUI();
    }
    
    // Method to initialize the user interface components and layout
    private void initializeUI() {
        // Title
        titleLabel = new Label("Create New Password");
        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setFont(Font.font(16));

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
        
        // Adding components to the grid
        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(confirmPassLabel, 0, 2);
        grid.add(confirmPassField, 1, 2);
        grid.add(submitButton, 1, 3);
        grid.add(backButton, 0, 3);
        grid.add(messageLabel, 0, 6, 2, 1);

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

    // Method to handle the submission of account creation form
    private void handleSubmit() {
    	// Retrieve input values from fields
        String password = passwordField.getText();
        String confirmPassword = confirmPassField.getText();

        // Check for empty fields
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("All fields are required.");
            return;
        }

        // Check if passwords match for verification
        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match.");
            return;
        }

        try {
        	boolean success = false; // Flag to indicate success of account creation
        	
        	// If the password is able to be reset, the user is unflagged
            if (mainApp.databaseHelper.resetPassword(username, password, 0)) {
                success = true;
            } 
            if (success) {
                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText("Password Reset! Redirecting to Roles...");
                // Redirect to role or role selection
                User user = mainApp.databaseHelper.getUserByUsername(username);
                if ((user.isAdmin() && user.isInstructor()) || (user.isAdmin() && user.isStudent()) || (user.isInstructor() && user.isStudent())) {
                    mainApp.showRoleChooser(username);
                }
                else if (user.isInstructor()) {
                    mainApp.showInstructorPage();
                }
                else if (user.isStudent()) {
                    mainApp.showStudentPage();
                }
                else if(user.isAdmin()){
                    mainApp.showAdminPage();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("An error occurred during password reset.");
        }
    }
}