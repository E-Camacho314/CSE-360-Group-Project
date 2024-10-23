package cse360helpsystem;

import java.sql.SQLException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ArticlesPage extends VBox {
	// Reference to the main application to facilitate navigation between pages
    private CSE360HelpSystem mainApp;
    
    // UI Components
	private Label welcome = new Label("Articles View");
	private Label warning = new Label("");
	private Label code = new Label("");
	private Label codetype = new Label("");
	private Label articleinfo = new Label("Manipulate Articles:");
	private Button createbutton = new Button ("Create Article");
	private Button viewbutton = new Button ("View Article");
	private Button backupbutton = new Button ("Backup Articles");
	private Button restorebutton = new Button ("Restore Articles");
	private Button deletebutton = new Button ("Delete Articles");
	private Button listbutton = new Button ("List Articles");
	private Button updatebutton = new Button ("Update Article");
	private Button returnbutton = new Button ("Return to Main");
	private TextField deleteField = new TextField();
	private TextField updateField = new TextField();
	private TextField backupField = new TextField();
	private TextField restoreField = new TextField();
	
	// String to know which page to return to
	private String prev;

    /**
     * Constructor for FinishSetupPage.
     * Initializes the UI components and sets up the layout.
     * 
     * @param mainApp the main application instance for navigation
     * @param username the username of the user completing the setup
     */
    public ArticlesPage(CSE360HelpSystem mainApp, String prev) {
        this.mainApp = mainApp;
        this.prev = prev;
        initializeUI();
    }

    /**
     * Initializes and configures all UI components and layouts.
     * Sets up labels, text fields, buttons, and their event handlers.
     */
    private void initializeUI() {
    	// Create a BorderPane to organize the layout
		BorderPane mainPane = new BorderPane();
		
		// Configure the welcome label
		welcome.setTextFill(Color.BLACK);
        welcome.setFont(Font.font(null, 16));
        
        // Configure the userinfo label
        articleinfo.setTextFill(Color.BLACK);
        articleinfo.setFont(Font.font(null, 14));

        // Configure buttons and text fields
        createbutton.setTextFill(Color.BLACK);
        createbutton.setFont(Font.font(null, 14));        
        backupbutton.setTextFill(Color.BLACK);
        backupbutton.setFont(Font.font(null, 14));    
        restorebutton.setTextFill(Color.BLACK);
        restorebutton.setFont(Font.font(null, 14)); 
        deletebutton.setTextFill(Color.RED);
        deletebutton.setFont(Font.font(null, 14));        
        listbutton.setTextFill(Color.BLACK);
        listbutton.setFont(Font.font(null, 14));
        returnbutton.setTextFill(Color.BLACK);
        returnbutton.setFont(Font.font(null, 14));
        updatebutton.setTextFill(Color.BLACK);
        updatebutton.setFont(Font.font(null, 14));
        viewbutton.setTextFill(Color.BLACK);
        viewbutton.setFont(Font.font(null, 14));
        
        // Set prompt texts for text fields
        deleteField.setPromptText("Article to delete");
        updateField.setPromptText("Article to update");   
        backupField.setPromptText("File to backup to"); 
        restoreField.setPromptText("File to restore from"); 
        
        // Create a GridPane to arrange the UI components
        GridPane articlesPane = new GridPane();
        articlesPane.setAlignment(Pos.CENTER);
        articlesPane.setVgap(10);
        articlesPane.setHgap(10);
        articlesPane.setPadding(new Insets(20, 20, 20, 20));

        // Add components to the GridPane
        articlesPane.add(welcome, 0, 0, 2, 1);
        articlesPane.add(warning, 0, 1);
        articlesPane.add(codetype, 0, 2);
        articlesPane.add(code, 1, 2);
        articlesPane.add(createbutton, 0, 3);
        articlesPane.add(updatebutton, 0, 4);
        articlesPane.add(deleteField, 0, 5);
        articlesPane.add(deletebutton, 1, 5);
        articlesPane.add(listbutton, 0, 6);
        articlesPane.add(viewbutton, 0, 7);
        articlesPane.add(backupField, 0, 8);
        articlesPane.add(backupbutton, 1, 8);
        articlesPane.add(restoreField, 0, 9);
        articlesPane.add(restorebutton, 1, 9);
        articlesPane.add(returnbutton, 0, 12);

        // Set the VBox to the center of the BorderPane
        mainPane.setCenter(articlesPane);
        
        // Add the BorderPane to the HBox (the root container of this page)
        this.getChildren().addAll(mainPane);
        this.setAlignment(Pos.CENTER);

        // Button Actions
        returnbutton.setOnAction(e -> returnToPage(prev));
    }
    
    private void returnToPage(String prev) {
    	if(prev.equals("admin")) {
    		mainApp.showAdminPage();
    	}
    	if(prev.equals("instructor")) {
    		mainApp.showInstructorPage();
    	}
    }

    /**
     * Handles the submission of the form.
     * Validates input fields, updates the user's information in the database,
     * and navigates to the appropriate dashboard upon success.
     */
    private void handleSubmit() {
        /*String email = emailField.getText().trim();
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
        }*/
    }
}
