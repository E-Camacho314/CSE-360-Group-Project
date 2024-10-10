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


public class AdminPage extends HBox {
	private CSE360HelpSystem mainApp;
	private Label welcome = new Label("Admin View");
	private Label warning = new Label("");
	private Label userinfo = new Label("Manipulate Users:");
	private Label perms = new Label("Permissions:");
	private Button invitebutton = new Button ("Invite User");
	private Button resetbutton = new Button ("Reset User");
	private Button deletebutton = new Button ("Delete");
	private Button listbutton = new Button ("List Users");
	private Button changepermsbutton = new Button ("Update Roles");
	private Button logoutbutton = new Button ("Log Out");
	private CheckBox admin = new CheckBox ("Admin");
	private CheckBox instructor = new CheckBox ("Instructor");
	private CheckBox student = new CheckBox ("Student");
	private TextField deleteField = new TextField();
	private TextField permsField = new TextField();
	private TextField resetField = new TextField();
	private TextField inviteField = new TextField();
	private String user;
	private boolean confirmation;
	
	public AdminPage(CSE360HelpSystem mainApp){
		this.mainApp = mainApp;
		BorderPane mainPane = new BorderPane();
		
		welcome.setTextFill(Color.BLACK);
        welcome.setFont(Font.font(null, 14));
        
        userinfo.setTextFill(Color.BLACK);
        userinfo.setFont(Font.font(null, 14));
        
        perms.setTextFill(Color.BLACK);
        perms.setFont(Font.font(null, 14));

        invitebutton.setTextFill(Color.BLACK);
        invitebutton.setFont(Font.font(null, 14));
        
        resetbutton.setTextFill(Color.BLACK);
        resetbutton.setFont(Font.font(null, 14));
        
        deletebutton.setTextFill(Color.RED);
        deletebutton.setFont(Font.font(null, 14));
        
        listbutton.setTextFill(Color.BLACK);
        listbutton.setFont(Font.font(null, 14));
        
        deleteField.setPromptText("Username to delete");
        inviteField.setPromptText("User to invite");
        resetField.setPromptText("Username to reset");
        permsField.setPromptText("User to change Roles");
        
        changepermsbutton.setTextFill(Color.BLACK);
        changepermsbutton.setFont(Font.font(null, 14));
        
        logoutbutton.setTextFill(Color.BLACK);
        logoutbutton.setFont(Font.font(null, 14));
        
        GridPane adminPane = new GridPane();
        adminPane.setAlignment(Pos.CENTER);
        adminPane.setVgap(10);
        adminPane.setHgap(10);
        adminPane.setPadding(new Insets(20, 20, 20, 20));

        adminPane.add(welcome, 0, 0, 2, 1);
        adminPane.add(userinfo, 0, 1);
        adminPane.add(warning, 1, 1);
        adminPane.add(inviteField, 0, 2);
        adminPane.add(invitebutton, 1, 2);
        adminPane.add(resetField, 0, 3);
        adminPane.add(resetbutton, 1, 3);
        adminPane.add(deleteField, 0, 4);
        adminPane.add(deletebutton, 1, 4);
        adminPane.add(listbutton, 0, 5);
        adminPane.add(permsField, 0, 6);
        adminPane.add(admin, 0, 8);
        adminPane.add(instructor, 0, 7);
        adminPane.add(student, 1, 7);
        adminPane.add(changepermsbutton, 2, 8);
        adminPane.add(logoutbutton, 0, 9);

        mainPane.setCenter(adminPane);
        this.getChildren().addAll(mainPane);
        this.setAlignment(Pos.CENTER);

        logoutbutton.setOnAction(e -> mainApp.showLoginPage());
        deletebutton.setOnAction(e -> delete());
        changepermsbutton.setOnAction(e -> changePerms());
        listbutton.setOnAction(e -> {showUsers(); 
        	mainApp.showListPage();});
        invitebutton.setOnAction(e -> inviteUser());
        resetbutton.setOnAction(e -> resetUserPassword());
	}
	
	private void delete() {
	    try {
	        if (!deleteField.getText().isEmpty()) {
	            user = deleteField.getText();
	            
	            // popup for confirmation
	            deleteConfirmation(user);
	            
	            if (confirmation == true) {
	            	mainApp.databaseHelper.deleteUser(user);
	            	warning.setText("User deleted.");
	            	warning.setTextFill(Color.GREEN);
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
	
    public void showUsers() {
        try {
            mainApp.databaseHelper.displayUsers();
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
    }
	
	private void deleteConfirmation(String user) {
		Stage popup = new Stage();
		popup.initModality(Modality.APPLICATION_MODAL);
		popup.setTitle("Delete Confirmation");
		Label warning = new Label("Are You Sure?");
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
		
		HBox confirmationButtons = new HBox(10);
		confirmationButtons.getChildren().addAll(yes, no);
		confirmationButtons.setAlignment(Pos.CENTER);
		
		HBox layout = new HBox(10);
		layout.getChildren().addAll(warning, confirmationButtons);
		layout.setAlignment(Pos.CENTER);
		
		Scene popupScene = new Scene(layout, 400, 100);
		popup.setScene(popupScene);
		popup.showAndWait();	
	}

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
	        boolean isAdmin = admin.isSelected();
	        boolean isInstructor = instructor.isSelected();
	        boolean isStudent = student.isSelected();

	        // Ensure at least one role is selected
	        if (!isAdmin && !isInstructor && !isStudent) {
	            warning.setText("Select at least one role for the invite.");
	            warning.setTextFill(Color.RED);
	            return;
	        }

	        // Generate a unique invite code
	        String inviteCode = generateInviteCode();

	        // Store the invite code with roles
	        boolean stored = mainApp.getDatabaseHelper().storeInviteCode(inviteCode, isAdmin, isInstructor, isStudent);
	        if (stored) {
	            // Optionally, send the invite code via email or display it
	            warning.setTextFill(Color.GREEN);
	            warning.setText("Invite created successfully. Code: " + inviteCode);
	            // Clear inputs
	            inviteField.clear();
	            admin.setSelected(false);
	            instructor.setSelected(false);
	            student.setSelected(false);
	        } else {
	            warning.setText("Failed to create invite. Try again.");
	            warning.setTextFill(Color.RED);
	        }

	    } catch (SQLException e) {
	        warning.setText("Error creating invite: " + e.getMessage());
	        warning.setTextFill(Color.RED);
	    }
	}

	private String generateInviteCode() {
	    return Long.toHexString(Double.doubleToLongBits(Math.random()));
	}
	

	private void resetUserPassword() {
	    try {
	        String resetUser = resetField.getText();
	        if (resetUser.isEmpty()) {
	            warning.setText("Please enter a username.");
	            warning.setTextFill(Color.RED);
	            return;
	        }
	
	        String oneTimePassword = generateOneTimePassword();
	
	        mainApp.databaseHelper.resetPassword(resetUser, oneTimePassword);
	        
	        warning.setText("Password reset. One-time password: " + oneTimePassword);
	        warning.setTextFill(Color.GREEN);
	
	        // Clear inputs
	        resetField.clear();
	
	    } catch (SQLException e) {
	        warning.setText("Error resetting password: " + e.getMessage());
	        warning.setTextFill(Color.RED);
	    }
	}
	
	private String generateOneTimePassword() {
	    return Long.toHexString(Double.doubleToLongBits(Math.random()));
}
}