package cse360helpsystem;

import java.sql.SQLException;

import javafx.geometry.Insets;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * <p>InstructorPage Class</p>
 * 
 * <p>Description: This class represents the Instructor's interface within the CSE360HelpSystem application.
 * It provides a simple layout with a welcome message and a logout button, allowing instructors
 * to navigate back to the login page when needed.</p>
 * 
 * <p>Authors: Erik Camacho, Thienban Nguyen, Sarvesh Shanmugam, Ivan Mancillas, Tanis Peterson</p>
 */

public class InstructorPage extends HBox {
	// UI Components
	private Label welcome = new Label("Instructor View");
	private Label warning = new Label("");
	private Button logoutbutton = new Button ("Log Out");
	private Button articlesbutton = new Button ("Articles View");
	private TextField specialText = new TextField ();
	private TextField createText = new TextField ();
	private Button specialbutton = new Button ("Special Access View");
	private Button createbutton = new Button ("Create Special Access Group");
	private Button viewallstudentsbutton = new Button ("View All Students");
	private Button viewstudentsbutton = new Button ("View Student");
	private Button deletestudentbutton = new Button ("Delete Student");
	private TextField deletestudentText = new TextField ();
	private TextField viewstudentsText = new TextField ();
	private Button searchbutton = new Button ("Search View");
	private String current = "instructor";
	private CSE360HelpSystem mainApp;
	
	// String to find the currently logged in user
	private String username;
	
	// String to hold the group that the user chooses
	private String group;
	
	/**
     * Constructor for InstructorPage.
     * Initializes the UI components and sets up the layout.
     * 
     * @param mainApp the main application instance to facilitate page navigation
     */
	public InstructorPage(CSE360HelpSystem mainApp){
		
		// Store reference to the main application
		this.mainApp = mainApp;
		
		// Create a BorderPane to organize the layout
		BorderPane mainPane = new BorderPane();
		
		// Configure the welcome label
		welcome.setTextFill(Color.BLACK);
        welcome.setFont(Font.font(null, 16));
        
        // Configure the articles button
        articlesbutton.setTextFill(Color.BLACK);
        articlesbutton.setFont(Font.font(null, 14));
        
        // Configure the Special Access Groups button
        specialbutton.setTextFill(Color.BLACK);
        specialbutton.setFont(Font.font(null, 14));
        
        // Configure the View Student button
        viewstudentsbutton.setTextFill(Color.BLACK);
        viewstudentsbutton.setFont(Font.font(null, 14));
        
        // Configure the Delete Student button
        deletestudentbutton.setTextFill(Color.BLACK);
        deletestudentbutton.setFont(Font.font(null, 14));
        
        // Configure the search button
        searchbutton.setTextFill(Color.BLACK);
        searchbutton.setFont(Font.font(null, 14));
        
        // Configure the View All Students button
        viewallstudentsbutton.setTextFill(Color.BLACK);
        viewallstudentsbutton.setFont(Font.font(null, 14));
        
        // Configure the Create Special Access Group button
        createbutton.setTextFill(Color.BLACK);
        createbutton.setFont(Font.font(null, 14));

        // Configure the logout button
        logoutbutton.setTextFill(Color.BLACK);
        logoutbutton.setFont(Font.font(null, 14));
        
        // Set prompt texts for text fields
        deletestudentText.setPromptText("Enter Student to Delete");
        viewstudentsText.setPromptText("Enter Student to View");
        specialText.setPromptText("Enter Group to View");
        createText.setPromptText("Enter Group Name");
        
        // Create a GridPane to hold the welcome message and logout button
        GridPane instructPane = new GridPane();
        instructPane.setAlignment(Pos.CENTER);
        instructPane.setVgap(10);
        instructPane.setHgap(10);
        instructPane.setPadding(new Insets(20, 20, 20, 20));

        // Add components to the GridPane
        instructPane.add(welcome, 0, 0, 2, 1);
        instructPane.add(warning, 0, 1);
        instructPane.add(articlesbutton, 0, 2);
        instructPane.add(searchbutton, 0, 3);
        instructPane.add(viewstudentsText, 0, 4);
        instructPane.add(viewstudentsbutton, 1, 4);
        instructPane.add(viewallstudentsbutton, 0, 5);
        instructPane.add(deletestudentText, 0, 6);
        instructPane.add(deletestudentbutton, 1, 6);
        instructPane.add(specialText, 0, 7);
        instructPane.add(specialbutton, 1, 7);
        instructPane.add(createText, 0, 8);
        instructPane.add(createbutton, 1, 8);
        instructPane.add(logoutbutton, 0, 9);

        // Place the VBox in the center of the BorderPane
        mainPane.setCenter(instructPane);
        
        // Add the BorderPane to the HBox (the root container of this page)
        this.getChildren().addAll(mainPane);
        this.setAlignment(Pos.CENTER);
        
        articlesbutton.setOnAction(e -> {
            mainApp.showArticlesPage(current); // Switch articles page
        });
        
        logoutbutton.setOnAction(e -> {
        	mainApp.databaseHelper.logoutUser();
            mainApp.showLoginPage(); // Switch back to the login page
        });
        
        specialbutton.setOnAction(e -> {
        	getSpecialAccess();
        });
        
        createbutton.setOnAction(e -> {
        	createSpecialAccess();
        });
        
        searchbutton.setOnAction(e -> {
        	mainApp.showSearchPage(current);
        });
        
        deletestudentbutton.setOnAction(e -> {
        	deleteStudent();
        });
	}
	
	private void getSpecialAccess() {
		try {
			String group;
			// Store the username of the current user
			username = mainApp.databaseHelper.findLoggedInUser();
			if(specialText.getText().isEmpty()) {
				warning.setText("Enter a Group Name");
				warning.setTextFill(Color.RED);
	            return;
			}
			else {
				group = specialText.getText();
				if(mainApp.databaseHelper.doesGroupExist(group)) {
					mainApp.databaseHelper.printSpecialAccessTable();
					if(mainApp.databaseHelper.isUserInGroup(group, username)) {
						if(mainApp.databaseHelper.isUserAdmin(group, username)) {
							specialText.clear();
							warning.setText("");
							mainApp.showSpecialAccessPage(current, group, true);
						}
						else {
							specialText.clear();
							warning.setText("");
							mainApp.showSpecialAccessPage(current, group, false);
						}
					}
					else {
						warning.setText("You Do Not Have Access");
						warning.setTextFill(Color.RED);
			            return;
					}
				}
				else {
					warning.setText("Enter a Valid Group");
					warning.setTextFill(Color.RED);
		            return;
				}
			}
		}
		catch(SQLException e) {
			warning.setText("ERROR: Exception Hit");
			warning.setTextFill(Color.RED);
            return;
		}
	}
	
	private void createSpecialAccess() {
		String group;
		// Store the username of the current user
		username = mainApp.databaseHelper.findLoggedInUser();
		try {
			if(createText.getText().isEmpty()) {
				warning.setText("Enter a Group Name");
				warning.setTextFill(Color.RED);
	            return;
			}
			else {
				group = createText.getText();
				if(!mainApp.databaseHelper.doesGroupExist(group)) {
					mainApp.databaseHelper.createGroup(group, username);
				}
				else {
					warning.setText("Group Already Exists");
					warning.setTextFill(Color.RED);
		            return;
				}

			}
			mainApp.showSpecialAccessPage(current, group, true);
			warning.setText("");
			createText.clear();
		}
		catch(SQLException e) {
			warning.setText("ERROR: Exception Hit");
			warning.setTextFill(Color.RED);
            return;
		}
	}
	
	// Method to handle deleting a student account from the database
	private void deleteStudent() {
		try {
	        // Retrieve the entered student ID or username
	        String studentIdentifier = deletestudentText.getText().trim();

	        // Check if the input is empty
	        if (studentIdentifier.isEmpty()) {
	            warning.setText("Enter a Student ID or Username to Delete");
	            warning.setTextFill(Color.RED);
	            return;
	        }
 
	        // Check if the student exists in the database
	        if (!mainApp.databaseHelper.doesStudentExist(studentIdentifier)) {
	            warning.setText("Student Not Found");
	            warning.setTextFill(Color.RED);
	            return;
	        }

	        // Attempt to delete the student
	        boolean success = mainApp.databaseHelper.deleteStudent(studentIdentifier);

	        if (success) {
	            warning.setText("Student Successfully Deleted");
	            warning.setTextFill(Color.GREEN);
	            deletestudentText.clear();
	        } else {
	            warning.setText("Failed to Delete Student");
	            warning.setTextFill(Color.RED);
	        }
	    } catch (SQLException e) {
	        warning.setText("Database Error: Unable to Delete Student");
	        warning.setTextFill(Color.RED);
	        e.printStackTrace();
	    }
	}	
}