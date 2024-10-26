package cse360helpsystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
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
	private Button logoutbutton = new Button ("Log Out");
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
        
        // Create a VBox to hold the welcome message and logout button
        VBox studPane = new VBox();
        studPane.setSpacing(20);
        studPane.setPadding(new Insets(10, 10, 10, 10));
        studPane.getChildren().addAll(welcome, logoutbutton);

        // Place the VBox in the center of the BorderPane
        mainPane.setCenter(studPane);
        
        // Add the BorderPane to the root HBox container
        this.getChildren().addAll(mainPane);
        this.setAlignment(Pos.CENTER);
        
        logoutbutton.setOnAction(e -> {
            mainApp.showLoginPage(); // Switch back to the login page
        });

	}
}
