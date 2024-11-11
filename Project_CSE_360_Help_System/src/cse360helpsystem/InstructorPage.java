package cse360helpsystem;

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
	private Button specialbutton = new Button ("Special Access View");
	private Button viewallstudentsbutton = new Button ("View All Students");
	private Button viewstudentsbutton = new Button ("View Students");
	private TextField viewstudentsText = new TextField ();
	private Button searchbutton = new Button ("Search View");
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
        
        // Configure the Special Access Groups button
        specialbutton.setTextFill(Color.BLACK);
        specialbutton.setFont(Font.font(null, 14));
        
        // Configure the View Students button
        viewstudentsbutton.setTextFill(Color.BLACK);
        viewstudentsbutton.setFont(Font.font(null, 14));
        
        // Configure the search button
        searchbutton.setTextFill(Color.BLACK);
        searchbutton.setFont(Font.font(null, 14));
        
        // Configure the View All Students button
        viewallstudentsbutton.setTextFill(Color.BLACK);
        viewallstudentsbutton.setFont(Font.font(null, 14));

        // Configure the logout button
        logoutbutton.setTextFill(Color.BLACK);
        logoutbutton.setFont(Font.font(null, 14));
        
        // Set prompt texts for text fields
        viewstudentsText.setPromptText("Enter Group to View");
        specialText.setPromptText("Enter Group to View");
        
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
        instructPane.add(specialText, 0, 6);
        instructPane.add(specialbutton, 1, 6);
        instructPane.add(logoutbutton, 0, 7);

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
        searchbutton.setOnAction(e -> {
        	mainApp.showSearchPage(current);
        });
	}
}