package cse360helpsystem;

import java.sql.SQLException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
* <p>RoleChooser Class</p>
* 
* <p>Description:This class provides an interface for users who have multiple roles (e.g., Admin, Instructor, Student) 
* to choose which role they want to log into. It dynamically displays buttons based on the roles 
* assigned to the user and navigates to the corresponding page upon selection. </p>
* 
* <p>Authors: Erik Camacho, Thienban Nguyen, Sarvesh Shanmugam, Ivan Mancillas, Tanis Peterson</p>
*/
public class RoleChooser extends HBox {
	// Reference to the main application for navigation and database access
	private CSE360HelpSystem mainApp;
	
	 // UI Components
	private Label welcome = new Label("Please Choose the Role to Log In to");
	private Label warning = new Label("");
	private Button adminbutton = new Button ("Administrator");
	private Button instructbutton = new Button ("Instructor");
	private Button studbutton = new Button ("Student");
	
	 /**
     * Constructor for RoleChooser.
     * Initializes the UI components, sets up the layout, and assigns event handlers.
     * 
     * @param mainApp the main application instance for navigation and database access
     * @param username the username of the currently logged-in user
     */
	public RoleChooser(CSE360HelpSystem mainApp, String username){
		try {
			// Store reference to the main application
			this.mainApp = mainApp;
			
			// Create a BorderPane as the main layout container
			BorderPane mainPane = new BorderPane();
			
			// Configure the welcome label
			welcome.setTextFill(Color.BLACK);
	        welcome.setFont(Font.font(null, 14));
	        
	        // Configure the Administrator button
	        adminbutton.setTextFill(Color.BLACK);
	        adminbutton.setFont(Font.font(null, 14));
	        
	        // Configure the Instructor button
	        instructbutton.setTextFill(Color.BLACK);
	        instructbutton.setFont(Font.font(null, 14));

	        // Configure the Student button
	        studbutton.setTextFill(Color.BLACK);
	        studbutton.setFont(Font.font(null, 14));
	        
	        // Create a VBox to arrange labels and buttons vertically
	        VBox RoleChoose = new VBox();
	        RoleChoose.setAlignment(Pos.CENTER);
	        RoleChoose.setPadding(new Insets(30, 30, 30, 30));
	        RoleChoose.getChildren().addAll(welcome, warning);

	        // Set the VBox to the center of the BorderPane
	        mainPane.setCenter(RoleChoose);
	        
	        // Add the BorderPane to the HBox (root container of this page)
	        this.getChildren().addAll(mainPane);
	        this.setAlignment(Pos.CENTER);
	        
	        // Retrieve the User object from the database using the provided username
	        User user = mainApp.databaseHelper.getUserByUsername(username);
	        
	        // Dynamically add role buttons based on the user's assigned roles
	        if(user.isAdmin()) {
		        RoleChoose.getChildren().add(adminbutton);
	        }
	        if(user.isInstructor()) {
		        RoleChoose.getChildren().addAll(instructbutton);
	        }
	        if(user.isStudent()) {
		        RoleChoose.getChildren().addAll(studbutton);
	        }
	        adminbutton.setOnAction(e -> {
	            mainApp.showAdminPage(); // Switch back to the login page
	        });
	        
	        instructbutton.setOnAction(e -> {
	            mainApp.showInstructorPage(); // Switch back to the login page
	        });
	        
	        studbutton.setOnAction(e -> {
	            mainApp.showStudentPage(); // Switch back to the login page
	        });

		}
		catch (SQLException e) {
			// Handle any SQL exceptions that occur while retrieving user information
			System.err.println("Database error: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
    		System.out.println("path chosen"); // Debugging message
		}
	}
}