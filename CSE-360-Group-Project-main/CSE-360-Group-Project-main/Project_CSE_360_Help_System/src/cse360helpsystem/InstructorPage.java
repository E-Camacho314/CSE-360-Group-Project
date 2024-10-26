package cse360helpsystem;

import javafx.geometry.Insets;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
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
	private Button logoutbutton = new Button ("Log Out");
	private Button articlesbutton = new Button ("Articles");
	private String current = "instructor";
	private CSE360HelpSystem mainApp;
	
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

        // Configure the logout button
        logoutbutton.setTextFill(Color.BLACK);
        logoutbutton.setFont(Font.font(null, 14));
        
        // Create a VBox to hold the welcome label and logout button vertically
        VBox instructPane = new VBox();
        instructPane.setSpacing(20);
        instructPane.setPadding(new Insets(10, 10, 10, 10));
        instructPane.getChildren().addAll(welcome, articlesbutton, logoutbutton);

        // Set the VBox to the center of the BorderPane
        mainPane.setCenter(instructPane);
        
        // Add the BorderPane to the HBox (the root container of this page)
        this.getChildren().addAll(mainPane);
        this.setAlignment(Pos.CENTER);
        
        articlesbutton.setOnAction(e -> {
            mainApp.showArticlesPage(current); // Switch articles page
        });
        
        logoutbutton.setOnAction(e -> {
            mainApp.showLoginPage(); // Switch back to the login page
        });

	}
}