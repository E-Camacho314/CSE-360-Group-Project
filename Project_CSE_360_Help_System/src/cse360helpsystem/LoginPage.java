package cse360helpsystem;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
* <p>LoginPage Class</p>
* 
* <p>Description: This class represents the login interface for the CSE360HelpSystem application.
* It provides fields for users to enter their username and password, an optional invite code for
* account creation, and buttons to log in or create a new account. The class handles user authentication
* by interacting with the DatabaseHelper and navigates users to the appropriate dashboard based on their roles.</p>
* 
* <p>Authors: Erik Camacho, Thienban Nguyen, Sarvesh Shanmugam, Ivan Mancillas, Tanis Peterson</p>
*/

public class LoginPage extends HBox {
	// Reference to the main application for navigation and database access
	private CSE360HelpSystem mainApp;
	
	// UI Components
	private PasswordField passfield = new PasswordField();
	private TextField userfield = new TextField();
	private TextField inviteField = new TextField();
	private Button loginbutton = new Button ("Log In");
	private Button logoutbutton = new Button ("Log Out");
	private Button createAccount = new Button ("Create Account");
	private Label warning = new Label();
	private String username;
	private String passwords;
	private String invite;
	private Label welcome = new Label("Welcome to the ASU CSE 360 Help System!");
	private Label login = new Label("Username:");
	private Label password = new Label("Password:");
	private Button redirect = new Button("Have a One-Time Password?");
	private Label inviteLabel = new Label("Have an invite code?");
	
	 /**
     * Constructor for LoginPage.
     * Initializes the UI components, sets up the layout, and assigns event handlers.
     * 
     * @param mainApp the main application instance for navigation and database access
     */
	public LoginPage(CSE360HelpSystem mainApp){
		// Store reference to the main application
		this.mainApp = mainApp;
		
		// Create a BorderPane as the main layout container
		BorderPane mainPane = new BorderPane();
		
		// Configure the welcome label
		welcome.setTextFill(Color.BLACK);
        welcome.setFont(Font.font(null, 14));
        
        // Configure the username label
        login.setTextFill(Color.BLACK);
        login.setFont(Font.font(null, 14));
        
        // Set placeholder text for the username field
        userfield.setPromptText("Enter your username");

        // Configure the password label
        password.setTextFill(Color.BLACK);
        password.setFont(Font.font(null, 14));
        
        // Set placeholder text for the password field
        passfield.setPromptText("Enter your password");
        
        // Configure the login button
        loginbutton.setTextFill(Color.BLACK);
        loginbutton.setFont(Font.font(null, 14));

        // Configure the redirect button
        redirect.setTextFill(Color.BLACK);
        redirect.setFont(Font.font(null, 14));
        
        // Configure the invite code label
        inviteLabel.setTextFill(Color.BLACK);
        inviteLabel.setFont(Font.font(null, 14));
        
        // Set placeholder text for the invite code field
        inviteField.setPromptText("Enter your invite code");
        
        // Configure the create account button
        createAccount.setTextFill(Color.BLACK);
        createAccount.setFont(Font.font(null, 14));
        
        // Create a GridPane to organize labels and fields in a grid layout
        GridPane loginPane = new GridPane();
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setVgap(10);
        loginPane.setHgap(10);
        loginPane.setPadding(new Insets(20, 20, 20, 20));

        // Add components to the GridPane with specified column and row indices
        loginPane.add(welcome, 0, 0, 2, 1);
        loginPane.add(login, 0, 1);
        loginPane.add(userfield, 1, 1);
        loginPane.add(password, 0, 2);
        loginPane.add(passfield, 1, 2);
        loginPane.add(loginbutton, 0, 3);
        loginPane.add(warning, 0, 4);
        loginPane.add(inviteLabel, 0, 5);
        loginPane.add(inviteField, 1, 5);
        loginPane.add(createAccount, 0, 6);
        
        // Set the GridPane to the center of the BorderPane
        mainPane.setCenter(loginPane);
        
        // Add the BorderPane to the HBox (root container)
        this.getChildren().addAll(mainPane);
        this.setAlignment(Pos.CENTER);
        
        loginbutton.setOnAction(new ButtonHandler());
        createAccount.setOnAction(new ButtonHandler());
	}
	
	/**
     * Inner class to handle button click events.
     * Implements the EventHandler interface for ActionEvent.
     */
	private class ButtonHandler implements EventHandler<ActionEvent>{
		 /**
         * Handles button click events.
         * Differentiates actions based on the source of the event (login or create account).
         * Performs validation, interacts with the database, and navigates to appropriate pages.
         * 
         * @param e the ActionEvent triggered by a button click
         */
	    public void handle(ActionEvent e){
	        try {
	        	//check to see if the add button is clicked and the textfields are filled
					if (e.getSource() == loginbutton && userfield.getText().isEmpty() != true && passfield.getText().isEmpty() != true) {
						warning.setText("");
						username = userfield.getText();
			        	passwords = passfield.getText();
			        	//Check if the database is empty. If so, set up new user as Admin
			        	if (mainApp.databaseHelper.doesUserExist(username) && mainApp.databaseHelper.checkPassword(username, passwords)) {
                            User user = mainApp.databaseHelper.getUserByUsername(username);
                            
                            // Check if the user's account setup is incomplete or if the account is flagged
			        		if(user.getFirstname().equals("") || user.getMiddlename().equals("") || user.getLastname().equals("")) {
			        			mainApp.showFinishSetupPage(username);
			        		}
			        		else if (user.isFlagged() == true ) {
			        			mainApp.showNewPass(username);
			        		}
			        		else {
			        			// Navigate to the appropriate dashboard based on user roles
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
			        		}
			        		// Clear the username and password fields after successful login
		                	userfield.clear();
							passfield.clear();
						}
			        	else {
			        		// If user does not exist or password is incorrect, display an error message
			        		warning.setText("User does not Exist.");
							warning.setTextFill(Color.RED);
							warning.setFont(Font.font(null, 14));
		                	userfield.clear();
							passfield.clear();
			        	}
			        // Clear the fields regardless of the outcome
	            	userfield.clear();
					passfield.clear();
	          	    }

					//check if the addbutton is clicked and atleast one textfield is empty
	                  else if (e.getSource()  == loginbutton && (userfield.getText().isEmpty() == true || passfield.getText().isEmpty() == true)){
	                	   warning.setText("At least one field is empty.");
						   warning.setTextFill(Color.RED);
						   warning.setFont(Font.font(null, 14));
	                 }
					
					// Handle create account button click with a non-empty invite code field
	                if(e.getSource() == createAccount && inviteField.getText().isEmpty() != true) {
	                	invite = inviteField.getText();
	                	// Validate the invite code
	                	if(mainApp.databaseHelper.isInviteCodeValid(invite)) {
	                		inviteField.clear();
	                		mainApp.showCreateAccountPage(invite);
	                	}
	                	else {
	                		   // If the invite code is invalid, display an error message
		                	   warning.setText("Invalid Invite Code.");
							   warning.setTextFill(Color.RED);
							   warning.setFont(Font.font(null, 14));
	                	}
	                }
	                // Handle create account button click with an empty invite code field
	                else if(e.getSource() == createAccount && inviteField.getText().isEmpty() == true) {
	                	   warning.setText("Missing Invite Code.");
						   warning.setTextFill(Color.RED);
						   warning.setFont(Font.font(null, 14));
	                }
	           } //end of try*/

	        //exception if the courseNum is not an integer
	           catch(NumberFormatException ex){
	        	   warning.setText("Error!");
				   warning.setTextFill(Color.RED);
				   warning.setFont(Font.font(null, 14)); 
	            }
	        // Catch block for handling any other exceptions
	           catch(Exception exception)
	            {
	        	   warning.setText("Error");
				   warning.setTextFill(Color.RED);
				   warning.setFont(Font.font(null, 14)); 
	            }
	        
	        } //end of handle() method                 
	} //end of ButtonHandler class
	
}

