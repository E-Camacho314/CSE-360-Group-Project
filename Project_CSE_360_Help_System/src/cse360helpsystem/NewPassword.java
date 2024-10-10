package cse360helpsystem;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.sql.SQLException;

public class NewPassword extends VBox {
    private CSE360HelpSystem mainApp;
    private static final DatabaseHelper databaseHelper = new DatabaseHelper();
    private String username;
    private Label titleLabel;
    private Label passLabel;
    private TextField passField;
    private Button submitButton;
    private Label messageLabel;

    public NewPassword(CSE360HelpSystem mainApp, String username) {
        this.mainApp = mainApp;
        this.username = username;
        initializeUI();
    }

    private void initializeUI() {
    	 // Title
		titleLabel = new Label("Create a New Password");
        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setFont(Font.font(16));
        
        // Password
        passLabel = new Label("password:");
        passLabel.setTextFill(Color.BLACK);
        passLabel.setFont(Font.font(14));
        passField = new TextField();
        passField.setPromptText("Enter your password");
        
        // Buttons
        submitButton = new Button("Submit");
        submitButton.setFont(Font.font(14));
        
        // Button Actions
        submitButton.setOnAction(e -> handlepass());
    }

    private void handlepass() {
    	try {
    		if(passField.getText().isEmpty() != true) {
    			String password = passField.getText();
    			mainApp.databaseHelper.changePassword(username, password);
    			passField.clear();
    			User user = mainApp.databaseHelper.getUserByUsername(username);
                if (user != null) {
                    if ((user.isAdmin() && user.isInstructor()) || (user.isAdmin() && user.isStudent()) || (user.isInstructor() && user.isStudent())) {
                        mainApp.showRoleChooser(username);
                    }
                    else if (user.isInstructor()) {
                        mainApp.showInstructorPage();
                    }
                    else if (user.isStudent()) {
                        mainApp.showStudentPage();
                    }
                    else if(user.isAdmin()){
                        mainApp.showAdminPage();
                    }
                }
    		}
    		else {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Failed to update password.");
    		}
    	}
    	catch(SQLException e) {
            e.printStackTrace();
            mainApp.showLoginPage();
    	}
    }
}
