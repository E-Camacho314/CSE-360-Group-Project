package cse360helpsystem;

import java.sql.SQLException;

import org.json.JSONException;

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

public class SpecialAccess extends VBox {

	// Reference to the main application to facilitate navigation between pages
    private CSE360HelpSystem mainApp;
    
    // Role of the currently logged-in user
    private String prev;
    
    // Name of Current Group
    private String group;
    
    // If the user has admin rights or not
    private Boolean rights;
    
    // UI Components
    private Label titleLabel;
    private Label addarticleLabel;
    private Label removeLabel;
    private Label addstudLabel;
    private Label addinstLabel;
    private Label deluserLabel;
    private Label viewstudLabel;
    private Label viewLabel;
    private Label viewartLabel;
    private TextField addarticleField;
    private TextField removeField;
    private TextField addstudField;
    private TextField addinstField;
    private TextField roleField;
    private TextField deluserField;
    private TextField viewartField;
    private TextField backupField;
    private Button addarticleButton;
    private Button removeButton;
    private Button addstudButton;
    private Button addinstButton;
    private Button deluserButton;
    private Button viewstudButton;
    private Button viewButton;
    private Button viewartButton;
    private Button backupButton;
    private Button restoreButton;
    private Button deleteButton;
    private Button backButton;
    private Label messageLabel;

    /**
     * Constructor for SpecialAccess.
     * Initializes the UI components and sets up the layout.
     * 
     * @param mainApp the main application instance for navigation
     * @param prev the role of the current user
     * @param group the name of the group 
     * @param rights whether the user has admin rights or not
     */
    public SpecialAccess(CSE360HelpSystem mainApp, String prev, String group, Boolean rights) {
        this.mainApp = mainApp;
        this.prev = prev;
        this.group = group;
        this.rights = rights;
        initializeUI();
    }

    /**
     * Initializes and configures all UI components and layouts.
     * Sets up labels, text fields, buttons, and their event handlers.
     */
    private void initializeUI() {
        // Title
        titleLabel = new Label("Special Access Group: " + group);
        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setFont(Font.font(16));
        
        // Add an Article to a Special Access Group 
        addarticleLabel = new Label("Add an Article:");
        addarticleLabel.setTextFill(Color.BLACK);
        addarticleLabel.setFont(Font.font(14));
        addarticleField = new TextField();
        addarticleField.setPromptText("Enter Article ID");
        
        // Remove an Article from a Special Access Group 
        removeLabel = new Label("Remove an Article:");
        removeLabel.setTextFill(Color.BLACK);
        removeLabel.setFont(Font.font(14));
        removeField = new TextField();
        removeField.setPromptText("Enter Article ID");

        // Add a Student to a Special Access Group 
        addstudLabel = new Label("Add a Student:");
        addstudLabel.setTextFill(Color.BLACK);
        addstudLabel.setFont(Font.font(14));
        addstudField = new TextField();
        addstudField.setPromptText("Enter Student Username");
        
        // Add an Instructor to a Special Access Group 
        addinstLabel = new Label("Add an Instructor:");
        addinstLabel.setTextFill(Color.BLACK);
        addinstLabel.setFont(Font.font(14));
        addinstField = new TextField();
        addinstField.setPromptText("Enter Instructor Username");
        roleField = new TextField();
        roleField.setPromptText("Enter Permissions");
        
        // Delete a User to a Special Access Group 
        deluserLabel = new Label("Delete a User:");
        deluserLabel.setTextFill(Color.BLACK);
        deluserLabel.setFont(Font.font(14));
        deluserField = new TextField();
        deluserField.setPromptText("Enter Username");

        // View Students in the Special Access Group
        viewstudLabel = new Label("View Students:");
        viewstudLabel.setTextFill(Color.BLACK);
        viewstudLabel.setFont(Font.font(14));
        
        // View All Users in the Special Access Group
        viewLabel = new Label("View All Users:");
        viewLabel.setTextFill(Color.BLACK);
        viewLabel.setFont(Font.font(14));
        
        // View an article in the Special Access Group
        viewartLabel = new Label("View An Article:");
        viewartLabel.setTextFill(Color.BLACK);
        viewartLabel.setFont(Font.font(14));
        viewartField = new TextField();
        viewartField.setPromptText("Enter Article ID");
        
     // Backup Special Access Group 
        backupField = new TextField();
        backupField.setPromptText("Enter File to Backup To");

        // Buttons
        addarticleButton = new Button("Add Article");
        addarticleButton.setFont(Font.font(14));
        removeButton = new Button("Remove Article");
        removeButton.setFont(Font.font(14));
        addstudButton = new Button("Add Student");
        addstudButton.setFont(Font.font(14));
        addinstButton = new Button("Add Instructor");
        addinstButton.setFont(Font.font(14));
        deluserButton = new Button("Delete User");
        deluserButton.setFont(Font.font(14));
        viewstudButton = new Button("View Students");
        viewstudButton.setFont(Font.font(14));
        viewButton = new Button("View All");
        viewButton.setFont(Font.font(14));
        viewartButton = new Button("View Article");
        viewartButton.setFont(Font.font(14));
        backupButton = new Button("Backup Group");
        backupButton.setFont(Font.font(14));
        restoreButton = new Button("Restore Group");
        restoreButton.setFont(Font.font(14));
        deleteButton = new Button("Delete Group");
        deleteButton.setFont(Font.font(14));
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
        
        if(rights == true) {
            // Adding components to the grid
            grid.add(titleLabel, 0, 0, 2, 1);
            grid.add(addarticleLabel, 0, 1);
            grid.add(addarticleField, 0, 2);
            grid.add(addarticleButton, 1, 2);
            grid.add(removeLabel, 0, 3);
            grid.add(removeField, 0, 4);
            grid.add(removeButton, 1, 4);
            grid.add(addstudLabel, 0, 5);
            grid.add(addstudField, 0, 6);
            grid.add(addstudButton, 1, 6);
            grid.add(addinstLabel, 0, 7);
            grid.add(addinstField, 0, 8);
            grid.add(roleField, 1, 8);
            grid.add(addinstButton, 2, 8);
            grid.add(deluserLabel, 0, 9);
            grid.add(deluserField, 0, 10);
            grid.add(deluserButton, 1, 10);
            grid.add(viewstudLabel, 0, 11);
            grid.add(viewstudButton, 1, 11);
            grid.add(viewLabel, 0, 12);
            grid.add(viewButton, 1, 12);
            grid.add(viewartLabel, 0, 13);
            grid.add(viewartField, 0, 14);
            grid.add(viewartButton, 1, 14);
            grid.add(backupField, 0, 15);
            grid.add(backupButton, 1, 15);
            grid.add(restoreButton, 2, 15);
            grid.add(deleteButton, 0, 16);
            grid.add(backButton, 0, 18);
            grid.add(messageLabel, 0, 17, 2, 1);
            
            addinstButton.setOnAction(e -> {
				try {
					AddInstructor();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
            
            addstudButton.setOnAction(e -> {
				try {
					AddStudent();
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
            
            addarticleButton.setOnAction(e -> {
				try {
					AddArticle();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
        }
        else {
        	// Adding components to the grid
            grid.add(titleLabel, 0, 0, 2, 1);
            grid.add(viewstudLabel, 0, 1);
			grid.add(viewstudButton, 1, 1);
			grid.add(viewLabel, 0, 2);
			grid.add(viewButton, 1, 2);
			grid.add(viewartLabel, 0, 3);
			grid.add(viewartField, 0, 4);
			grid.add(viewartButton, 1, 4);
			grid.add(backButton, 0, 6);
            grid.add(messageLabel, 0, 5, 2, 1);
        }



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
    	messageLabel.setText("");
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
    
    private void AddInstructor() throws SQLException {
		if(addinstField.getText().isEmpty() || roleField.getText().isEmpty()) {
			messageLabel.setText("Missing Information");
			messageLabel.setTextFill(Color.RED);
            return;
		}
    	String inst = addinstField.getText();
    	String role = roleField.getText();
    	if((!role.equals("admin")) && (!role.equals("view"))) {
			messageLabel.setText("Enter 'view' or 'admin' for privileges");
			messageLabel.setTextFill(Color.RED);
            return;
    	}
    	if(!mainApp.databaseHelper.doesUserExist(inst)) {
			messageLabel.setText("Invalid User");
			messageLabel.setTextFill(Color.RED);
            return;
    	}
    	User user = mainApp.databaseHelper.getUserByUsername(inst);
    	if(!user.isInstructor() || !user.isAdmin()) {
			messageLabel.setText("User is not an Instructor or Admin");
			messageLabel.setTextFill(Color.RED);
            return;
    	}
    	mainApp.databaseHelper.addInstructor(group, inst, role);
    	mainApp.databaseHelper.printSpecialAccessTable();
    	addinstField.clear();
    	roleField.clear();
    	messageLabel.setText("Instructor Added");
		messageLabel.setTextFill(Color.GREEN);

    }
    
    private void AddStudent() throws JSONException, SQLException {
		if(addstudField.getText().isEmpty()) {
			messageLabel.setText("Missing Information");
			messageLabel.setTextFill(Color.RED);
            return;
		}
    	String student = addstudField.getText();
    	if(!mainApp.databaseHelper.doesUserExist(student)) {
			messageLabel.setText("Invalid User");
			messageLabel.setTextFill(Color.RED);
            return;
    	}
    	User user = mainApp.databaseHelper.getUserByUsername(student);
    	if(!user.isStudent()) {
			messageLabel.setText("User is not a Student");
			messageLabel.setTextFill(Color.RED);
            return;
    	}
    	mainApp.databaseHelper.addStudentToViewAccess(group, student);
    	mainApp.databaseHelper.printSpecialAccessTable();
    	addstudField.clear();
    	messageLabel.setText("Student Added");
		messageLabel.setTextFill(Color.GREEN);
    }
    
    private void AddArticle() throws SQLException {
		if(addarticleField.getText().isEmpty()) {
			messageLabel.setText("Missing Information");
			messageLabel.setTextFill(Color.RED);
            return;
		}
    	String ID = addarticleField.getText();
    	int id = Integer.parseInt(ID);
    	if(!mainApp.databaseHelper.isArticleIDValid(id)) {
			messageLabel.setText("Invalid Article");
			messageLabel.setTextFill(Color.RED);
            return;
    	}
    	mainApp.databaseHelper.addArticleToGroup(id, group);
    	mainApp.databaseHelper.printSpecialAccessTable();
    	addarticleField.clear();
    	messageLabel.setText("Article Added");
		messageLabel.setTextFill(Color.GREEN);
    }
}
