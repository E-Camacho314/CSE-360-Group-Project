package cse360helpsystem;

import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;

/**
 * <p>AdmiPage Class</p>
 * 
 * <p>Description: This class represents the admin interface for managing users
 * in the CSE360 Help System. Admins can invite users, reset passwords, delete users,
 * and change user roles.</p>
 * 
 * <p>Authors: Erik Camacho, Thienban Nguyen, Sarvesh Shanmugam, Ivan Mancillas, Tanis Peterson</p>
 */

public class AdminPage extends HBox {
	// Reference to the main application class to access shared resources
	private CSE360HelpSystem mainApp;
	
	// UI Components
	private Label welcome = new Label("Admin View");
	private Label warning = new Label("");
	private Label code = new Label("");
	private Label codetype = new Label("");
	private Label userinfo = new Label("Manipulate Users:");
	private Label perms = new Label("Permissions:");
	private Button invitebutton = new Button ("Invite User");
	private Button resetbutton = new Button ("Reset User");
	private Button deletebutton = new Button ("Delete");
	private Button listbutton = new Button ("List Users");
	private Button changepermsbutton = new Button ("Update Roles");
	private Button logoutbutton = new Button ("Log Out");
	private Button articlesbutton = new Button ("Article View");
	private Button specialbutton = new Button ("Special Access View");
	private CheckBox admin1 = new CheckBox ("Admin");
	private CheckBox instructor1 = new CheckBox ("Instructor");
	private CheckBox student1 = new CheckBox ("Student");
	private CheckBox admin = new CheckBox ("Admin");
	private CheckBox instructor = new CheckBox ("Instructor");
	private CheckBox student = new CheckBox ("Student");
	private TextField deleteField = new TextField();
	private TextField permsField = new TextField();
	private TextField resetField = new TextField();
	private TextField inviteField = new TextField();
	private TextField specialField = new TextField();
	private String user;
	private String current = "admin";
	private boolean confirmation;
	
	// Constructor for the AdminPage
	public AdminPage(CSE360HelpSystem mainApp){
		// Store reference to the main application
		this.mainApp = mainApp;
	
		// Create a BorderPane to organize the layout
		BorderPane mainPane = new BorderPane();
		
		// Configure the welcome label
		welcome.setTextFill(Color.BLACK);
        welcome.setFont(Font.font(null, 16));
        
        // Configure the userinfo label
        userinfo.setTextFill(Color.BLACK);
        userinfo.setFont(Font.font(null, 14));
        
    	// Set up the permissions label
        perms.setTextFill(Color.BLACK);
        perms.setFont(Font.font(null, 14));

        // Configure buttons and text fields
        invitebutton.setTextFill(Color.BLACK);
        invitebutton.setFont(Font.font(null, 14));        
        resetbutton.setTextFill(Color.BLACK);
        resetbutton.setFont(Font.font(null, 14));       
        deletebutton.setTextFill(Color.RED);
        deletebutton.setFont(Font.font(null, 14));        
        listbutton.setTextFill(Color.BLACK);
        listbutton.setFont(Font.font(null, 14));
        changepermsbutton.setTextFill(Color.BLACK);
        changepermsbutton.setFont(Font.font(null, 14));
        logoutbutton.setTextFill(Color.BLACK);
        logoutbutton.setFont(Font.font(null, 14));
        articlesbutton.setTextFill(Color.BLACK);
        articlesbutton.setFont(Font.font(null, 14));
        specialbutton.setTextFill(Color.BLACK);
        specialbutton.setFont(Font.font(null, 14));
        
        // Set prompt texts for text fields
        deleteField.setPromptText("Username to delete");
        inviteField.setPromptText("User to invite");
        resetField.setPromptText("Username to reset");
        permsField.setPromptText("User to change Roles");     
        specialField.setPromptText("Select Group to View");   
        
        // Create a GridPane to arrange the UI components
        GridPane adminPane = new GridPane();
        adminPane.setAlignment(Pos.CENTER);
        adminPane.setVgap(10);
        adminPane.setHgap(10);
        adminPane.setPadding(new Insets(20, 20, 20, 20));

        // Add components to the GridPane
        adminPane.add(welcome, 0, 0, 2, 1);
        adminPane.add(userinfo, 0, 1);
        adminPane.add(warning, 1, 1);
        adminPane.add(codetype, 0, 2);
        adminPane.add(code, 1, 2);
        adminPane.add(inviteField, 0, 3);
        adminPane.add(invitebutton, 1, 3);
        adminPane.add(admin1, 0, 4);
        adminPane.add(instructor1, 0, 5);
        adminPane.add(student1, 1, 5);
        adminPane.add(resetField, 0, 6);
        adminPane.add(resetbutton, 1, 6);
        adminPane.add(deleteField, 0, 7);
        adminPane.add(deletebutton, 1, 7);
        adminPane.add(listbutton, 0, 8);
        adminPane.add(permsField, 0, 9);
        adminPane.add(changepermsbutton, 1, 9);
        adminPane.add(instructor, 0, 10);
        adminPane.add(student, 1, 10);
        adminPane.add(admin, 0, 11);
        adminPane.add(specialField, 0, 12);
        adminPane.add(specialbutton, 1, 12);
        adminPane.add(logoutbutton, 0, 13);
        adminPane.add(articlesbutton, 1, 13);

        // Set the VBox to the center of the BorderPane
        mainPane.setCenter(adminPane);
        
        // Add the BorderPane to the HBox (the root container of this page)
        this.getChildren().addAll(mainPane);
        this.setAlignment(Pos.CENTER);

        // Set up action handlers for the buttons
        logoutbutton.setOnAction(e -> logout());
        deletebutton.setOnAction(e -> delete());
        changepermsbutton.setOnAction(e -> changePerms());
        listbutton.setOnAction(e -> {showUsers(); 
        	mainApp.showListPage();});
        invitebutton.setOnAction(e -> inviteUser());
        resetbutton.setOnAction(e -> resetUserPassword());
        articlesbutton.setOnAction(e -> mainApp.showArticlesPage(current));
        specialbutton.setOnAction(e -> getSpecialAccess());
	}
	
	// Method to delete a user from the database
	private void delete() {
	    try {
	        if (!deleteField.getText().isEmpty()) {
	            user = deleteField.getText();
	            
	            // popup for confirmation
	            deleteConfirmation(user);
	            
	            if (confirmation == true) {
	            	if(mainApp.databaseHelper.moreThanOneAdmin()) {
	            		String current = mainApp.databaseHelper.findLoggedInUser();
		            	mainApp.databaseHelper.deleteUser(user);
		            	warning.setText("User deleted.");
		            	warning.setTextFill(Color.GREEN);
		            	if(user.equals(current)){
		            		logout();
		            	}
	            	}
	            	else {
	            		warning.setText("Only One Admin Exists");
		            	warning.setTextFill(Color.RED);
		            	deleteField.clear();
		            	return;
	            	}
	            }
	        } 
	        else {
	            warning.setText("Please enter a username.");
	            warning.setTextFill(Color.RED);
	        }
	        deleteField.clear();
	    } 
	    catch (SQLException e) {
	        warning.setText("Error deleting user: " + e.getMessage());
	        warning.setTextFill(Color.RED);
	    } 
	    finally {
	    	System.out.println("deleted");
	    }
	}
	
	 // Method to display users
    public void showUsers() {
        try {
            mainApp.databaseHelper.displayUsers();
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
    }
    
    // Method to log out
    private void logout() {
    	warning.setText("");
    	code.setText("");
    	codetype.setText("");
    	inviteField.clear();
    	resetField.clear();
    	mainApp.databaseHelper.logoutUser();
    	mainApp.showLoginPage();
    }
	
    // Method to show confirmation dialog for deletion
	private void deleteConfirmation(String user) {
		Stage popup = new Stage();
		popup.initModality(Modality.APPLICATION_MODAL); // Ensure it blocks input to other windows
		popup.setTitle("Delete Confirmation");
		Label warning = new Label("Are You Sure?"); // Confirmation message
		Button yes = new Button("Yes");
		Button no = new Button("No");
		
		yes.setOnAction(e -> {
			confirmation = true;
			popup.close();
		});
		
		no.setOnAction(e -> {
			confirmation = false;
			popup.close();
		});
		
		// Layout for confirmation buttons
		HBox confirmationButtons = new HBox(10);
		confirmationButtons.getChildren().addAll(yes, no);
		confirmationButtons.setAlignment(Pos.CENTER);
		
		// Layout for the popup
		HBox layout = new HBox(10);
		layout.getChildren().addAll(warning, confirmationButtons);
		layout.setAlignment(Pos.CENTER);
		
		Scene popupScene = new Scene(layout, 400, 100);
		popup.setScene(popupScene);
		popup.showAndWait();	
	}

	// Method to change a user roles
	private void changePerms() {
	    try {
	        if (!permsField.getText().isEmpty()) {
	            user = permsField.getText();
	            int adminChoice = admin.isSelected() ? 1 : 0;
	            int instChoice = instructor.isSelected() ? 1 : 0;
	            int studChoice = student.isSelected() ? 1 : 0;

	            mainApp.databaseHelper.changeUserRoles(user, adminChoice, instChoice, studChoice);
	            warning.setText("Roles updated.");
	            warning.setTextFill(Color.GREEN);

	            // Clear inputs
	            permsField.clear();
	            admin.setSelected(false);
	            instructor.setSelected(false);
	            student.setSelected(false);
	        } 
	        else {
	            warning.setText("Please enter a username.");
	            warning.setTextFill(Color.RED);
	        }
	    } 
	    catch (SQLException e) {
	        warning.setText("Error updating roles: " + e.getMessage());
	        warning.setTextFill(Color.RED);
	    } 
	    finally {
	        System.out.println("updated");
	    } 
	}
	
	// Method to convert boolean types to int as needed for other methods
	public int booleanToInt(boolean value) {
	    return value ? 1 : 0;
	}

	// Method to allow an admin to create an invite code
	private void inviteUser() {
	    try {
	        String inviteUsername = inviteField.getText().trim();
	        if (inviteUsername.isEmpty()) {
	            warning.setText("Please enter a username to invite.");
	            warning.setTextFill(Color.RED);
	            return;
	        }

	        // Check if the user already exists
	        if (mainApp.getDatabaseHelper().doesUserExist(inviteUsername)) {
	            warning.setText("Username already exists. Choose a different one.");
	            warning.setTextFill(Color.RED);
	            return;
	        }

	        // Determine selected roles
	        boolean isAdmin = admin1.isSelected();
	        boolean isInstructor = instructor1.isSelected();
	        boolean isStudent = student1.isSelected();

	        // Ensure at least one role is selected
	        if (!isAdmin && !isInstructor && !isStudent) {
	            warning.setText("Select at least one role for the invite.");
	            warning.setTextFill(Color.RED);
	            return;
	        }

	        // Generate a unique invite code
	        String inviteCode = generateInviteCode();

	        // Store the invite code with roles
	        boolean stored = mainApp.getDatabaseHelper().storeInviteCode(inviteUsername, inviteCode, isAdmin, isInstructor, isStudent);
	        mainApp.databaseHelper.register(inviteUsername, "", booleanToInt(isAdmin), booleanToInt(isInstructor), booleanToInt(isStudent));
	        if (stored) {
	            // Optionally, send the invite code via email or display it
	            warning.setTextFill(Color.GREEN);
	            warning.setText("Invite created successfully.");
		        codetype.setText("Code: ");
		        codetype.setTextFill(Color.GREEN);
		        code.setText(inviteCode);
		        code.setTextFill(Color.GREEN);
	            // Clear inputs
	            inviteField.clear();
	            admin1.setSelected(false);
	            instructor1.setSelected(false);
	            student1.setSelected(false);
	        } else {
	            warning.setText("Failed to create invite. Try again.");
	            warning.setTextFill(Color.RED);
	        }

	    } catch (SQLException e) {
	        warning.setText("Error creating invite: " + e.getMessage());
	        warning.setTextFill(Color.RED);
	    }
	}

	// Method to generate a random string to be used as an invite code
	private String generateInviteCode() {
	    return Long.toHexString(Double.doubleToLongBits(Math.random()));
	}
	
	// Method to allow an admin to change a user's password to an one time password
	private void resetUserPassword() {
	    try {
	        String resetUser = resetField.getText();
	        if (resetUser.isEmpty()) {
	            warning.setText("Please enter a username.");
	            warning.setTextFill(Color.RED);
	            return;
	        }
	
	        String oneTimePassword = generateOneTimePassword();
	
	        mainApp.databaseHelper.resetPassword(resetUser, oneTimePassword, 1);
	        
	        warning.setText("Password reset.");
	        warning.setTextFill(Color.GREEN);
	        codetype.setText("One-time password: ");
	        codetype.setTextFill(Color.GREEN);
	        code.setText(oneTimePassword);
	        code.setTextFill(Color.GREEN);
	
	        // Clear inputs
	        resetField.clear();
	
	    } catch (SQLException e) {
	        warning.setText("Error resetting password: " + e.getMessage());
	        warning.setTextFill(Color.RED);
	    }
	}
	
	// Method to generate a random string to be used as a one time password
	private String generateOneTimePassword() {
	    return Long.toHexString(Double.doubleToLongBits(Math.random()));
	}
	
	// Access Special Access Page for a specified group
	private void getSpecialAccess() {
		try {
			String group;
			String username;
			// Store the username of the current user
			username = mainApp.databaseHelper.findLoggedInUser();
			if(specialField.getText().isEmpty()) {
				warning.setText("Enter a Group Name");
				warning.setTextFill(Color.RED);
	            return;
			}
			else {
				group = specialField.getText();
				if(mainApp.databaseHelper.doesSpecialGroupExist(group)) {
					mainApp.databaseHelper.printSpecialAccessTable();
					if(mainApp.databaseHelper.isUserInGroup(group, username)) {
						if(mainApp.databaseHelper.isUserAdmin(group, username)) {
							mainApp.showSpecialAccessPage(current, group, true);
							specialField.clear();
							warning.setText("");
						}
						else {
							mainApp.showSpecialAccessPage(current, group, false);
							specialField.clear();
							warning.setText("");
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
}