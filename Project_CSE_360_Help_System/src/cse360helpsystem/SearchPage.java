package cse360helpsystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SearchPage extends VBox {

	// Reference to the main application to facilitate navigation between pages
    private CSE360HelpSystem mainApp;
    
    // Role of the currently logged-in user
    private String prev;
    
    // UI Components
    private Label titleLabel;
    private Label diffLabel;
    private Label passLabel;
    private Label groupLabel;
    private Label phraseLabel;
    private TextField groupField;
    private TextField phraseField;
    private CheckBox beginner = new CheckBox ("Beginner");
	private CheckBox intermediate = new CheckBox ("Intermediate");
	private CheckBox advanced = new CheckBox ("Advanced");
	private CheckBox expert = new CheckBox ("Expert");
    private Button diffButton;
    private Button groupButton;
    private Button phraseButton;
    private Button backButton;
    private Label messageLabel;

    /**
     * Constructor for SearchPage.
     * Initializes the UI components and sets up the layout.
     * 
     * @param mainApp the main application instance for navigation
     * @param prev the role of the current user
     */
    public SearchPage(CSE360HelpSystem mainApp, String prev) {
        this.mainApp = mainApp;
        this.prev = prev;
        initializeUI();
    }

    /**
     * Initializes and configures all UI components and layouts.
     * Sets up labels, text fields, buttons, and their event handlers.
     */
    private void initializeUI() {
        // Title
        titleLabel = new Label("Search Articles");
        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setFont(Font.font(16));

        // By Difficulty
        diffLabel = new Label("Search By Difficulty:");
        diffLabel.setTextFill(Color.BLACK);
        diffLabel.setFont(Font.font(14));

        // By Group 
        groupLabel = new Label("Search by Group:");
        groupLabel.setTextFill(Color.BLACK);
        groupLabel.setFont(Font.font(14));
        groupField = new TextField();
        groupField.setPromptText("Enter the Group");

        // By Words, Names, or Phrases 
        phraseLabel = new Label("Search by Words, Names, or Phrases:");
        phraseLabel.setTextFill(Color.BLACK);
        phraseLabel.setFont(Font.font(14));
        phraseField = new TextField();
        phraseField.setPromptText("Enter the Word or Phrase");

        // Buttons
        diffButton = new Button("Search");
        diffButton.setFont(Font.font(14));
        groupButton = new Button("Search");
        groupButton.setFont(Font.font(14));
        phraseButton = new Button("Search");
        phraseButton.setFont(Font.font(14));
        backButton = new Button("Back to Main");
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
        grid.add(diffLabel, 0, 1);
        grid.add(beginner, 0, 2);
        grid.add(intermediate, 1, 2);
        grid.add(advanced, 0, 3);
        grid.add(expert, 1, 3);
        grid.add(diffButton, 2, 3);
        grid.add(groupLabel, 0, 4);
        grid.add(groupField, 0, 5);
        grid.add(groupButton, 1, 5);
        grid.add(phraseLabel, 0, 6);
        grid.add(phraseField, 0, 7);
        grid.add(phraseButton, 1, 7);
        grid.add(backButton, 0, 8);
        grid.add(messageLabel, 0, 9, 2, 1);

        // Align buttons to the right
        GridPane.setMargin(backButton, new Insets(10, 0, 0, 0));

        // Add GridPane to VBox
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(grid);

        // Button Actions
        backButton.setOnAction(e -> returnToPage(prev));
    }
    
    // Method to handle switching to previous page
    private void returnToPage(String prev) {
    	if(prev.equals("admin")) {
    		mainApp.showAdminPage();
    	}
    	if(prev.equals("instructor")) {
    		mainApp.showInstructorPage();
    	}
    	if(prev.equals("student")) {
    		mainApp.showStudentPage();
    	}
    }
}
