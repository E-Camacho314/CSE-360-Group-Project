package cse360helpsystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.sql.SQLException;

/**
 * <p>FinishSetupPage Class</p>
 * 
 * <p>Description: This class represents the final setup page where users complete their account details
 * after initial registration. It collects email and usernames and updates
 * the database accordingly. Upon successful completion, it redirects users to the appropriate
 * dashboard based on their roles.</p>
 * 
 * <p>Authors: Erik Camacho, Thienban Nguyen, Sarvesh Shanmugam, Ivan Mancillas, Tanis Peterson</p>
 */

public class FinishSetupPage extends VBox {
	// Reference to the main application to facilitate navigation between pages
    private CSE360HelpSystem mainApp;
    
    // Username of the currently logged-in user
    private String username;
    
    // UI Components
    private Label titleLabel;
    private Label emailLabel;
    private Label passLabel;
    private Label firstNameLabel;
    private Label middleNameLabel;
    private Label lastNameLabel;
    private Label preferredNameLabel;
    private TextField emailField;
    private TextField firstNameField;
    private TextField middleNameField;
    private TextField lastNameField;
    private TextField preferredNameField;
    private Button submitButton;
    private Button backButton;
    private Label messageLabel;

    /**
     * Constructor for FinishSetupPage.
     * Initializes the UI components and sets up the layout.
     * 
     * @param mainApp the main application instance for navigation
     * @param username the username of the user completing the setup
     */
    public FinishSetupPage(CSE360HelpSystem mainApp, String username) {
        this.mainApp = mainApp;
        this.username = username;
        initializeUI();
    }

    /**
     * Initializes and configures all UI components and layouts.
     * Sets up labels, text fields, buttons, and their event handlers.
     */
    private void initializeUI() {
        // Title
        titleLabel = new Label("Finish Setting Up Your Account");
        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setFont(Font.font(16));

        // Email
        emailLabel = new Label("Email:");
        emailLabel.setTextFill(Color.BLACK);
        emailLabel.setFont(Font.font(14));
        emailField = new TextField();
        emailField.setPromptText("Enter your email");

        // First Name
        firstNameLabel = new Label("First Name:");
        firstNameLabel.setTextFill(Color.BLACK);
        firstNameLabel.setFont(Font.font(14));
        firstNameField = new TextField();
        firstNameField.setPromptText("Enter your first name");

        // Middle Name
        middleNameLabel = new Label("Middle Name:");
        middleNameLabel.setTextFill(Color.BLACK);
        middleNameLabel.setFont(Font.font(14));
        middleNameField = new TextField();
        middleNameField.setPromptText("Enter your middle name");

        // Last Name
        lastNameLabel = new Label("Last Name:");
        lastNameLabel.setTextFill(Color.BLACK);
        lastNameLabel.setFont(Font.font(14));
        lastNameField = new TextField();
        lastNameField.setPromptText("Enter your last name");

        // Preferred First Name (optional)
        preferredNameLabel = new Label("Preferred First Name (optional):");
        preferredNameLabel.setTextFill(Color.BLACK);
        preferredNameLabel.setFont(Font.font(14));
        preferredNameField = new TextField();
        preferredNameField.setPromptText("Enter your preferred first name");

        // Buttons
        submitButton = new Button("Submit");
        submitButton.setFont(Font.font(14));
        backButton = new Button("Back to Login");
        backButton.setFont(Font.font(14));

        // Message Label
        messageLabel = new Label();
        messageLabel.setTextFill(Color.BLACK);
        messageLabel.setFont(Font.font(14));

        // Create a GridPane for organizing labels and text fields in a grid layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        // Adding components to the grid
        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(firstNameLabel, 0, 2);
        grid.add(firstNameField, 1, 2);
        grid.add(middleNameLabel, 0, 3);
        grid.add(middleNameField, 1, 3);
        grid.add(lastNameLabel, 0, 4);
        grid.add(lastNameField, 1, 4);
        grid.add(preferredNameLabel, 0, 5);
        grid.add(preferredNameField, 1, 5);
        grid.add(submitButton, 1, 6);
        grid.add(backButton, 1, 7);
        grid.add(messageLabel, 0, 8, 2, 1);

        // Align buttons to the right
        GridPane.setMargin(submitButton, new Insets(10, 0, 0, 0));
        GridPane.setMargin(backButton, new Insets(10, 0, 0, 0));

        // Add GridPane to VBox
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(grid);

        // Button Actions
        submitButton.setOnAction(e -> handleSubmit());
        backButton.setOnAction(e -> mainApp.showLoginPage());
    }

    /**
     * Handles the submission of the form.
     * Validates input fields, updates the user's information in the database,
     * and navigates to the appropriate dashboard upon success.
     */
    private void handleSubmit() {
        String email = emailField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String preferredName = preferredNameField.getText().trim();

        if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            messageLabel.setText("Email, First Name, and Last Name are required.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        try {
            // Update user details in the database
        	mainApp.databaseHelper.displayUsers();
            boolean success = mainApp.databaseHelper.registerWithEmailAndNames(username, email, firstName, middleName, lastName, preferredName);
            if (success) {
                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText("Account setup completed successfully!");

                // Redirect to the appropriate page after a short delay
                new Thread(() -> {
                    try {
                        Thread.sleep(2000); // Wait for 2 seconds
                    } catch (InterruptedException ex) {
                        // Handle exception
                    }
                    javafx.application.Platform.runLater(() -> {
                        try {
							// Re-login to fetch updated user roles
                            User user = mainApp.databaseHelper.getUserByUsername(username);
                            if (user != null) {
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
                            else {
                                mainApp.showLoginPage();
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            mainApp.showLoginPage();
                        }
                    });
                }).start();
            }
            else {
            	// If updating user details failed, show an error message
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Failed to update account.");
            }
        }
        catch (SQLException ex) {
        	// Handle any SQL exceptions that occur during the update
            ex.printStackTrace();
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("An error occurred during setup.");
        }
    }
}
