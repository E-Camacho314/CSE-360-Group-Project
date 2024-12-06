package cse360helpsystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
* <p>SpecialAccess Class</p>
* 
* <p>Description: This class represents the special access functions a user can perform in a group.
*  Admins and users with special access can perform tasks including adding or removing articles, 
*  adding or deleting users, view users or articles, delete, backup, and restore a group. Regular 
*  users can only view other users and articles belonging to the group. </p> 
* 
* <p>Authors: Erik Camacho, Thienban Nguyen, Sarvesh Shanmugam, Ivan Mancillas, Tanis Peterson</p>
*/

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
        addinstLabel = new Label("Add an Instructor or Admin:");
        addinstLabel.setTextFill(Color.BLACK);
        addinstLabel.setFont(Font.font(14));
        addinstField = new TextField();
        addinstField.setPromptText("Enter Username");
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
        addinstButton = new Button("Add User");
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
        
        // if user has special access rights
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
					e1.printStackTrace();
				}
			});
            
            addstudButton.setOnAction(e -> {
				try {
					AddStudent();
				} catch (JSONException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			});
            
            addarticleButton.setOnAction(e -> {
				try {
					AddArticle();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			});
            
            removeButton.setOnAction(e -> {
                try {
                    RemoveArticle();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            });

            
            deluserButton.setOnAction(e -> {
                try {
                    deleteUserFromGroup();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            });
            
            deleteButton.setOnAction(e -> {
            	deleteSpecialGroup();
            });
            
            backupButton.setOnAction(e -> {
            	backupSpecialAccessGroup();
            });
            
            restoreButton.setOnAction(e ->{
            	restoreSpecialAccessGroup();
            });
        }
        // user does not have special access rights
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
        viewartButton.setOnAction(e -> viewArticle());
        viewButton.setOnAction(e -> viewUsers());
        viewstudButton.setOnAction(e -> viewStudents());
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
    
    // Method to retrieve list of articles in the group
    private void viewArticle() {
    	if(viewartField.getText().isEmpty()) {
			messageLabel.setText("Missing Article ID");
			messageLabel.setTextFill(Color.RED);
            return;
    	}
        try {
        	String ID = viewartField.getText();
        	int id = Integer.parseInt(ID);
        	if(mainApp.databaseHelper.isArticleInSpecialAccessGroup(id, group)) {
    			mainApp.showArticlesListPage(prev, group, id, null, false);
        	}
        	else {
    			messageLabel.setText("Article does not exist in Group");
    			messageLabel.setTextFill(Color.RED);
                return;
        	}
        } catch (NumberFormatException e) {
        	messageLabel.setText("Invalid ID format.");
        	messageLabel.setTextFill(Color.RED);
        } catch (SQLException e) {
			e.printStackTrace();
		}  	
    }
    
    // Method to add a user with the instructor role to the group
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
    	if(!user.isInstructor() && !user.isAdmin()) {
			messageLabel.setText("User is not an Instructor or Admin");
			messageLabel.setTextFill(Color.RED);
            return;
    	}
    	mainApp.databaseHelper.addInstructor(group, inst, role);
    	mainApp.databaseHelper.printSpecialAccessTable();
    	addinstField.clear();
    	roleField.clear();
    	messageLabel.setText("User Added");
		messageLabel.setTextFill(Color.GREEN);
    }
    
    // Method to add a user with the student role to the group
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
    
    // Method to add an article to the group
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
    	if(mainApp.databaseHelper.isArticleInSpecialAccessGroup(id, group)) {
			messageLabel.setText("Article is already in Group");
			messageLabel.setTextFill(Color.RED);
            return;
    	}
    	mainApp.databaseHelper.addArticleToGroup(id, group);
    	mainApp.databaseHelper.printSpecialAccessTable();
    	addarticleField.clear();
    	messageLabel.setText("Article Added");
		messageLabel.setTextFill(Color.GREEN);
    }
    
    // Method to display users based on the special access group
    private void viewUsers() {
        try {
            // Get all users in the special access group
            List<User> users = mainApp.databaseHelper.getAllUsersInGroup(group);
            
            // Display users in a new list page
            if (users.isEmpty()) {
                messageLabel.setText("No users found in the group.");
                messageLabel.setTextFill(Color.RED);
            } else {
                mainApp.showUsersListPage(users, group, "All Users");
            }
        } catch (SQLException e) {
            messageLabel.setText("Error retrieving users.");
            messageLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }
    }
    
    // Method to display students based on the special access group
    private void viewStudents() {
        // Get all students in the special access group
		List<User> students = null;
		try {
			try {
				students = mainApp.databaseHelper.getStudentsInGroup(group);
			} catch (SQLException e) {
				messageLabel.setText("Error retrieving students. SQL Exception");
	            messageLabel.setTextFill(Color.RED);
				e.printStackTrace();
			}
		} catch (JSONException e) {
			messageLabel.setText("Error retrieving students. JSON Exception");
            messageLabel.setTextFill(Color.RED);
			e.printStackTrace();
		}
		
		// Display students in a new list page
		if (students.isEmpty()) {
		    messageLabel.setText("No students found in the group.");
		    messageLabel.setTextFill(Color.RED);
		} else {
		    mainApp.showUsersListPage(students, group, "Students");
		}
    }  
    
    // Method to remove a user from the group
    private void deleteUserFromGroup() throws SQLException {
        if (deluserField.getText().isEmpty()) {
            messageLabel.setText("Missing Username");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        String username = deluserField.getText();

        // Check if the user exists
        if (!mainApp.databaseHelper.doesUserExist(username)) {
            messageLabel.setText("Invalid User");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        // Call the existing function to check if the user is the last admin
        if (mainApp.databaseHelper.isLastAdminInGroup(group, username)) {
            messageLabel.setText("Cannot delete the last Admin from the group");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        // Proceed to delete the user
        mainApp.databaseHelper.deleteUserFromGroup(group, username);
        mainApp.databaseHelper.printSpecialAccessTable();
        deluserField.clear();
        messageLabel.setText("User deleted successfully");
        messageLabel.setTextFill(Color.GREEN);
    }
       
    // Method to remove an article from the group
    private void RemoveArticle() throws SQLException {
        if (removeField.getText().isEmpty()) {
            messageLabel.setText("Missing Article ID");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        try {
            String ID = removeField.getText();
            int articleId = Integer.parseInt(ID);

            // Attempt to delete the article using the database helper
            boolean success = mainApp.databaseHelper.deleteArticleFromSpecialAccessGroup(group, articleId);

            if (success) {
                removeField.clear();
                messageLabel.setText("Article removed successfully");
                messageLabel.setTextFill(Color.GREEN);
            } else {
                messageLabel.setText("Article not found in group");
                messageLabel.setTextFill(Color.RED);
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid Article ID format");
            messageLabel.setTextFill(Color.RED);
        } catch (JSONException e) {
            messageLabel.setText("Error processing JSON data");
            messageLabel.setTextFill(Color.RED);
            e.printStackTrace();
        } catch (SQLException e) {
            messageLabel.setText("Database error occurred");
            messageLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }
    }
    
    // Method to delete the group
    private void deleteSpecialGroup() {
    	try {
			mainApp.databaseHelper.deleteSpecialAccessGroup(group);
	    	if(prev.equals("admin")) {
	    		mainApp.showAdminPage();
	    	}
	    	if(prev.equals("instructor")) {
	    		mainApp.showInstructorPage();
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    // Method to backup the group
    private void backupSpecialAccessGroup() {
    	boolean known = false;
    	if (backupField.getText().isEmpty()) {
    		messageLabel.setText("Warning: No File specified!");
    		messageLabel.setTextFill(Color.RED);
    		return;
    	}
    	String file = backupField.getText();
    	String group = this.group;
    	try {
    		List<Long> idList = mainApp.databaseHelper.getSpecialAccessByGroups(group);
    		System.out.print(idList);
    		known = mainApp.databaseHelper.backupGroup(file, idList);
    		if(!known) {
    			messageLabel.setText("No articles found in " + group);
        		messageLabel.setTextFill(Color.RED);
    		} else {
    			messageLabel.setText("Backed up " + group + " to " + file + "!");
        		messageLabel.setTextFill(Color.GREEN);
        		backupField.clear();
    		}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    // Method to restore a deleted group
    private void restoreSpecialAccessGroup() {
    	if (backupField.getText().isEmpty()) {
    		messageLabel.setText("Warning: No file specified!");
    		messageLabel.setTextFill(Color.RED);
    		return;
    	}
    	String file = backupField.getText();
    	
    	Alert firstAlert = new Alert(Alert.AlertType.CONFIRMATION);
    	ButtonType deleteButton = new ButtonType("Delete");
    	ButtonType mergeButton = new ButtonType("Merge");
        ButtonType cancelButton = new ButtonType("Cancel");
        
    	firstAlert.setTitle("Confirmation Dialog");
        firstAlert.setHeaderText("Delete current table or merge?");
        firstAlert.setContentText("Please choose an option.");
        firstAlert.getButtonTypes().setAll(deleteButton, mergeButton, cancelButton);
        
        // Wait for the user's response
        Optional<ButtonType> result = firstAlert.showAndWait();

        try {
            if (mainApp.databaseHelper == null) {
                throw new IllegalStateException("DatabaseHelper is not initialized.");
            }

            if (result.get() == deleteButton) {
                System.out.println("Delete Table");
                String groupName = getGroupNameFromFile(file);
                List<Long> articleIdsToRemove = getArticleIdsToRemoveFromFile(file);

                mainApp.databaseHelper.removeArticlesFromGroup(file, articleIdsToRemove);
                mainApp.databaseHelper.restore(file);
                
                int articleId = mainApp.databaseHelper.getNewArticleId();
                String articleTitle = mainApp.databaseHelper.getRestoredArticleTitle(articleId);
                mainApp.databaseHelper.addArticlesToGroups(file, articleTitle);
                messageLabel.setText("Group Restored!");
                messageLabel.setTextFill(Color.GREEN);

            } else if (result.get() == mergeButton) {
                System.out.println("Merge Table");
                mainApp.databaseHelper.mergeArticlesforGroups(file, group, file);
                messageLabel.setText("Group Merged!");
                messageLabel.setTextFill(Color.GREEN);

            } else {
                System.out.println("User cancelled.");
                messageLabel.setText("Operation cancelled by the user.");
                messageLabel.setTextFill(Color.ORANGE);
            }
        } catch (Exception e) {
            System.err.println("Error during table operation: " + e.getMessage());
            e.printStackTrace();

            messageLabel.setText("Error: Operation failed. Check logs for details.");
            messageLabel.setTextFill(Color.RED);
        }
    }
    
    // Method to extract article IDs from the backup file
    private List<Long> getArticleIdsToRemoveFromFile(String filePath) throws IOException {
        List<Long> articleIdsToRemove = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Article ID: ")) {
                    String idStr = line.substring(12).trim();
                    try {
                        Long articleId = Long.parseLong(idStr);
                        articleIdsToRemove.add(articleId);
                    } catch (NumberFormatException e) {
                    	e.printStackTrace();
                    }
                }
            }
        }
        return articleIdsToRemove;
    }
    
    // Method to extract group name from the backup file
    private String getGroupNameFromFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        
        while ((line = reader.readLine()) != null) {
            // Assuming the group name is on a line like "Group Name: <groupName>"
            if (line.startsWith("Group Name: ")) {
                return line.substring("Group Name: ".length()).trim();
            }
        }
        
        reader.close();
        return null; // Return null if no group name is found
    }
}
