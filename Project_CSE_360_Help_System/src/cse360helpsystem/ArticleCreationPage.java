package cse360helpsystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * <p>ArticleCreationPage Class</p>
 * 
 * <p>Description: The ArticleCreationPage class represents a page in the CSE360HelpSystem that handles the creation of an article.</p>
 * 
 * <p>Authors: Erik Camacho, Thienban Nguyen, Sarvesh Shanmugam, Ivan Mancillas, Tanis Peterson</p>
 */

public class ArticleCreationPage extends VBox {
	// Reference to the main application instance
    private CSE360HelpSystem mainApp;
    
    // Class variables
    private String prev;
    private long id;
    
    // UI Components for input fields
    private Label titleLabel;
    private Label title2Label;
    private Label headersLabel;
    private Label groupsLabel;
    private Label accessLabel;
    private Label levelLabel;
    private Label abstractLabel;
    private Label bodyLabel;
    private Label keywordsLabel;
    private Label referencesLabel;
    
    // UI Components for data entry
    private TextField titleField;
    private TextField headersField;
    private TextField groupsField;
    private TextField abstractField;
    private TextField bodyField;
    private TextField keywordsField;
    private TextField referencesField;
    
    // Buttons for updating individual fields and submitting forms
    private Button titleButton = new Button ("Update Title");
    private Button headersButton = new Button ("Update Headers");
    private Button groupsButton = new Button ("Update Groups");
    private Button accessButton = new Button ("Update Access");
    private Button abstractButton = new Button ("Update Abstract");
    private Button bodyButton = new Button ("Update Body");
    private Button levelButton = new Button ("Update Level");
    private Button keywordsButton = new Button ("Update Keywords");
    private Button referencesButton = new Button ("Update References");
    private Button createButton;
    private Button backButton;
    
    // CheckBoxes for role-based access control
	private CheckBox admin = new CheckBox ("Admin");
	private CheckBox instructor = new CheckBox ("Instructor");
	private CheckBox student = new CheckBox ("Student");
	private CheckBox beginner = new CheckBox ("Beginner");
	private CheckBox intermediate = new CheckBox ("Intermediate");
	private CheckBox advanced = new CheckBox ("Advanced");
	private CheckBox expert = new CheckBox ("Expert");
	private CheckBox all = new CheckBox ("All");
    private Label messageLabel;

    /**
     * Constructor for ArticleCreationPage class.
     * Initializes the UI components and sets up the layout.
     *
     * @param mainApp        The main application instance.
     * @param prev 			 current role of the logged-in user to keep track.
     * @param id             id of the article to be updated.
     * 
     */
    public ArticleCreationPage(CSE360HelpSystem mainApp, String prev, long id) {
        this.mainApp = mainApp;
        this.prev = prev;
        this.id = id;
        initializeUI();
    }
    
    // Method to initialize the user interface components and layout
    private void initializeUI() {
        
    	//check if a valid id is given
    	if(id == 0) {
        	// Title if Creating a new Article
            titleLabel = new Label("Create Article");
            
            // Create Article Button
            createButton = new Button("Create Article");
            createButton.setFont(Font.font(14));
        }
        else {
        	// Title if Updating an Existing Article
            titleLabel = new Label("Update Article");
        }
        
    	// Setting font and color for title label
        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setFont(Font.font(16));

        // Title
        title2Label = new Label("Title:");
        title2Label.setTextFill(Color.BLACK);
        title2Label.setFont(Font.font(14));
        titleField = new TextField();
        titleField.setPromptText("Enter Title of Article");

        // Headers
        headersLabel = new Label("Headers:");
        headersLabel.setTextFill(Color.BLACK);
        headersLabel.setFont(Font.font(14));
        headersField = new TextField();
        headersField.setPromptText("Enter Headers (If multiple, separate with a comma)");

        // Groups
        groupsLabel = new Label("Groups:");
        groupsLabel.setTextFill(Color.BLACK);
        groupsLabel.setFont(Font.font(14));
        groupsField = new TextField();
        groupsField.setPromptText("Enter Groups (If multiple, separate with a comma)");
        
        // Access
        accessLabel = new Label("Accessibility:");
        accessLabel.setTextFill(Color.BLACK);
        accessLabel.setFont(Font.font(14));
        
        // Level
        levelLabel = new Label("Difficulty Level:");
        levelLabel.setTextFill(Color.BLACK);
        levelLabel.setFont(Font.font(14));
        
        // Abstract
        abstractLabel = new Label("Abstract:");
        abstractLabel.setTextFill(Color.BLACK);
        abstractLabel.setFont(Font.font(14));
        abstractField = new TextField();
        abstractField.setPromptText("Enter Abstract of Article");
        
        // Body
        bodyLabel = new Label("Body:");
        bodyLabel.setTextFill(Color.BLACK);
        bodyLabel.setFont(Font.font(14));
        bodyField = new TextField();
        bodyField.setPromptText("Enter Body of Article");
        
        // Keywords
        keywordsLabel = new Label("Keywords:");
        keywordsLabel.setTextFill(Color.BLACK);
        keywordsLabel.setFont(Font.font(14));
        keywordsField = new TextField();
        keywordsField.setPromptText("Enter Keywords (If multiple, separate with a comma)");
        
        // References
        referencesLabel = new Label("References:");
        referencesLabel.setTextFill(Color.BLACK);
        referencesLabel.setFont(Font.font(14));
        referencesField = new TextField();
        referencesField.setPromptText("Enter References (If multiple, separate with a comma)");

        // Buttons
        backButton = new Button("Back to Articles");
        backButton.setFont(Font.font(14));

        // Message Label
        messageLabel = new Label();
        messageLabel.setTextFill(Color.BLACK);
        messageLabel.setFont(Font.font(14));
        
        // Layout using GridPane
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));
        
        // Adding components to the grid
        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(title2Label, 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(headersLabel, 0, 2);
        grid.add(headersField, 1, 2);
        grid.add(groupsLabel, 0, 3);
        grid.add(groupsField, 1, 3);
        grid.add(accessLabel, 0, 4);
        grid.add(admin, 1, 4);
        grid.add(instructor, 0, 5);
        grid.add(student, 1, 5);
        grid.add(abstractLabel, 0, 6);
        grid.add(abstractField, 1, 6);
        grid.add(levelLabel, 0, 7);
        grid.add(beginner, 1, 7);
        grid.add(intermediate, 2, 7);
        grid.add(advanced, 0, 8);
        grid.add(expert, 1, 8);
        grid.add(all, 2, 8);
        grid.add(bodyLabel, 0, 9);
        grid.add(bodyField, 1, 9);
        grid.add(keywordsLabel, 0, 10);
        grid.add(keywordsField, 1, 10);
        grid.add(referencesLabel, 0, 11);
        grid.add(referencesField, 1, 11);
        grid.add(backButton, 0, 14);
        grid.add(messageLabel, 0, 12);
        
        // check if a valid id is given
        if(id == 0) {
        	// create article button added if a valid id is not given
        	grid.add(createButton, 0, 13);
            GridPane.setMargin(createButton, new Insets(10, 0, 0, 0));
            createButton.setOnAction(e -> createHandler());
        }
        else {
        	// update buttons added if the id is given
        	titleButton.setFont(Font.font(14));
        	headersButton.setFont(Font.font(14));
        	groupsButton.setFont(Font.font(14));
        	accessButton.setFont(Font.font(14));
        	abstractButton.setFont(Font.font(14));
        	bodyButton.setFont(Font.font(14));
        	keywordsButton.setFont(Font.font(14));
        	referencesButton.setFont(Font.font(14));
        	levelButton.setFont(Font.font(14));
        	
        	grid.add(titleButton, 2, 1);
            grid.add(headersButton, 2, 2);
            grid.add(groupsButton, 2, 3);
            grid.add(accessButton, 2, 4);
            grid.add(abstractButton, 2, 6);
            grid.add(levelButton, 3, 8);
            grid.add(bodyButton, 2, 9);
            grid.add(keywordsButton, 2, 10);
            grid.add(referencesButton, 2, 11);
            
            // Set action handlers to update database fields
            titleButton.setOnAction(e -> updateArticleField("title", titleField.getText()));
            headersButton.setOnAction(e -> updateArticleField("headers", headersField.getText()));
            groupsButton.setOnAction(e -> updateArticleField("groups", groupsField.getText()));
            accessButton.setOnAction(e -> updateArticleField("access", getAccessString()));
            abstractButton.setOnAction(e -> updateArticleField("abstract", abstractField.getText()));
            bodyButton.setOnAction(e -> updateArticleField("body", bodyField.getText()));
            keywordsButton.setOnAction(e -> updateArticleField("keywords", keywordsField.getText()));
            referencesButton.setOnAction(e -> updateArticleField("ref_list", referencesField.getText()));
            levelButton.setOnAction(e -> updateLevelAccess(id));
        }

        // Align buttons to the right
        GridPane.setMargin(backButton, new Insets(10, 0, 0, 0));

        // Add GridPane to VBox
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(grid);

        backButton.setOnAction(e -> {
            mainApp.showArticlesPage(prev); // Switch back to the login page
        });
    }
    
    // Method to handle updating a specifc data entry of an article
    private void updateArticleField(String field, String newValue) {
        if (newValue.isEmpty()) {
            messageLabel.setText("Please enter a value for " + field);
            messageLabel.setTextFill(Color.RED);
            return;
        }
        
        try {
            // Call the DatabaseHelper method to update the field in the database
            boolean success = CSE360HelpSystem.databaseHelper.updateArticleField(id, field, newValue);
            if (success) {
                messageLabel.setText(field + " updated successfully.");
                messageLabel.setTextFill(Color.GREEN);
            } else {
                messageLabel.setText("Failed to update " + field + ". Invalid article ID.");
                messageLabel.setTextFill(Color.RED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error updating " + field + ": " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
        }
    }
    
    private void updateLevelAccess(long id) {
    	// Determine selected levels
        boolean isBeginner = beginner.isSelected();
        boolean isIntermediate = intermediate.isSelected();
        boolean isAdvanced = advanced.isSelected();
        boolean isExpert = expert.isSelected();
        boolean isAll = all.isSelected();

        // Ensure at least one role is selected
        if(!isAll) {
            if (!isBeginner && !isIntermediate && !isAdvanced && !isExpert) {
                messageLabel.setText("Select at least one level");
                messageLabel.setTextFill(Color.RED);
                return;
            }
        }
        else {
        	isBeginner = true;
        	isIntermediate = true;
        	isAdvanced = true;
        	isExpert = true;
        }
        mainApp.databaseHelper.updateLevels(id, isBeginner, isIntermediate, isAdvanced, isExpert);
    }
    
    /**
     * Builds a string representation of the access levels based on checkbox selections.
     * 
     * @return A comma-separated string of access levels for admin, instructor, and student.
     */
    private String getAccessString() {
        return "admin:" + (admin.isSelected() ? "1" : "0") + "," +
               "instructor:" + (instructor.isSelected() ? "1" : "0") + "," +
               "student:" + (student.isSelected() ? "1" : "0");
    }
    
    // Method that handles the creation of a new article entry into the database
    private void createHandler() {
		
    	// Necessary variables
    	String title;
		String header;
		String groups;
		String abstracts;
		String body;
		String keywords;
		String references;
		boolean inserted = false;
		
		// Ensure all required fields are filled in
    	if(titleField.getText().isEmpty() || headersField.getText().isEmpty() || groupsField.getText().isEmpty() || abstractField.getText().isEmpty() || bodyField.getText().isEmpty() || keywordsField.getText().isEmpty() || referencesField.getText().isEmpty()) {
    		messageLabel.setText("Warning: Information missing");	
    		messageLabel.setTextFill(Color.RED);
    		return;
    	}
    	
    	// Obtain all necessary information
		title = titleField.getText();
		header = headersField.getText();
		groups = groupsField.getText();
		abstracts = abstractField.getText();
		body = bodyField.getText();
		keywords = keywordsField.getText();
		references = referencesField.getText();
		
        // Determine selected roles
        boolean isAdmin = admin.isSelected();
        boolean isInstructor = instructor.isSelected();
        boolean isStudent = student.isSelected();

        // Ensure at least one role is selected
        if (!isAdmin && !isInstructor && !isStudent) {
            messageLabel.setText("Select at least one role");
            messageLabel.setTextFill(Color.RED);
            return;
        }
        
        // Determine selected levels
        boolean isBeginner = beginner.isSelected();
        boolean isIntermediate = intermediate.isSelected();
        boolean isAdvanced = advanced.isSelected();
        boolean isExpert = expert.isSelected();
        boolean isAll = all.isSelected();

        // Ensure at least one role is selected
        if(!isAll) {
            if (!isBeginner && !isIntermediate && !isAdvanced && !isExpert) {
                messageLabel.setText("Select at least one level");
                messageLabel.setTextFill(Color.RED);
                return;
            }
        }
        else {
        	isBeginner = true;
        	isIntermediate = true;
        	isAdvanced = true;
        	isExpert = true;
        }
        
        try {
			inserted = mainApp.databaseHelper.insertArticle(title, header, groups, isAdmin, isInstructor, isStudent, isBeginner, isIntermediate, isAdvanced, isExpert, abstracts, keywords, body, references);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        if(inserted) {
        	messageLabel.setTextFill(Color.GREEN);
        	messageLabel.setText("Article created successfully.");
            // Clear inputs
            titleField.clear();
            headersField.clear();
            groupsField.clear();
            abstractField.clear();
            bodyField.clear();
            keywordsField.clear();
            referencesField.clear();
            admin.setSelected(false);
            instructor.setSelected(false);
            student.setSelected(false);
            beginner.setSelected(false);
            intermediate.setSelected(false);
            advanced.setSelected(false);
            expert.setSelected(false);
            all.setSelected(false);
        }
    }
}
