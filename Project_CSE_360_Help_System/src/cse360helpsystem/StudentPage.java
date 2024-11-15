package cse360helpsystem;

import javafx.scene.control.TextField;

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
* <p>StudentPage Class</p>
* 
* <p>Description:This class represents the student view in the CSE360HelpSystem. 
* It displays a welcome message and provides a logout button that navigates the user back to the login page. </p>
* 
* <p>Authors: Erik Camacho, Thienban Nguyen, Sarvesh Shanmugam, Ivan Mancillas, Tanis Peterson</p>
*/
public class StudentPage extends HBox {
	// UI Components
	private Label welcome = new Label("Student View");
	private Label warning = new Label("");
	private Button logoutbutton = new Button ("Log Out");
	private Button articlesbutton = new Button ("Articles View");
	private Button genericbutton = new Button ("Send Generic Message");
	private TextField specificText = new TextField ();
	private TextField specificneedText = new TextField ();
	private Button specificbutton = new Button ("Send Specific Message");
	private Button searchbutton = new Button ("Search View");
	private Button quitbutton = new Button ("Quit");
	private String current = "student";
	private CSE360HelpSystem mainApp;
	
	 /**
     * Constructor for StudentPage.
     * Initializes the layout and UI components, and assigns functionality to the logout button.
     * 
     * @param mainApp the main application instance to enable navigation
     */
	public StudentPage(CSE360HelpSystem mainApp){
		// Store the reference to the main application
		this.mainApp = mainApp;
		
		// Create a BorderPane layout to structure the student view
		BorderPane mainPane = new BorderPane();
		
		// Configure the welcome label
		welcome.setTextFill(Color.BLACK);
        welcome.setFont(Font.font(null, 16));

        // Configure the logout button
        logoutbutton.setTextFill(Color.BLACK);
        logoutbutton.setFont(Font.font(null, 14));
        
        // Configure the articles button
        articlesbutton.setTextFill(Color.BLACK);
        articlesbutton.setFont(Font.font(null, 14));
        
        // Configure the generic request button
        genericbutton.setTextFill(Color.BLACK);
        genericbutton.setFont(Font.font(null, 14));
        
        // Configure the specific request button
        specificbutton.setTextFill(Color.BLACK);
        specificbutton.setFont(Font.font(null, 14));
        
        // Configure the search button
        searchbutton.setTextFill(Color.BLACK);
        searchbutton.setFont(Font.font(null, 14));
        
        // Configure the quit button
        quitbutton.setTextFill(Color.BLACK);
        quitbutton.setFont(Font.font(null, 14));
        
        // Set prompt texts for text fields
        specificText.setPromptText("Enter What is not Found");
        specificneedText.setPromptText("Enter What is Needed");
        
        // Create a GridPane to hold the welcome message and logout button
        GridPane studPane = new GridPane();
        studPane.setAlignment(Pos.CENTER);
        studPane.setVgap(10);
        studPane.setHgap(10);
        studPane.setPadding(new Insets(20, 20, 20, 20));

        // Add components to the GridPane
        studPane.add(welcome, 0, 0, 2, 1);
        studPane.add(warning, 0, 1);
        studPane.add(searchbutton, 0, 2);
        studPane.add(genericbutton, 0, 3);
        studPane.add(specificText, 0, 4);
        studPane.add(specificneedText, 0, 5);
        studPane.add(specificbutton, 1, 5);
        studPane.add(logoutbutton, 0, 6);
        studPane.add(quitbutton, 1, 6);

        // Place the VBox in the center of the BorderPane
        mainPane.setCenter(studPane);
        
        // Add the BorderPane to the root HBox container
        this.getChildren().addAll(mainPane);
        this.setAlignment(Pos.CENTER);
        
        logoutbutton.setOnAction(e -> {
        	mainApp.databaseHelper.logoutUser();
            mainApp.showLoginPage(); // Switch back to the login page
        });
        searchbutton.setOnAction(e -> {
        	mainApp.showSearchPage(current);
        });

	}
}
