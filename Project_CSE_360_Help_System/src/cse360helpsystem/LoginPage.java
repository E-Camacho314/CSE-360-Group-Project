package cse360helpsystem;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
/**
 * <p> LoginPage Class </p>
 * 
 * <p> Description: .</p>
 * 
 * @author Erik Camacho, Thienban Nguyen, Sarvesh Shanmugam, Ivan Mancillas, Tanis Peterson
 * 
 */

public class LoginPage extends HBox {
	private CSE360HelpSystem mainApp = new CSE360HelpSystem();
	private PasswordField passfield = new PasswordField();
	private TextField userfield = new TextField();
	private TextField inviteField = new TextField();
	private Button loginbutton = new Button ("Log In");
	private Button logoutbutton = new Button ("Log Out");
	private Button createAccount = new Button ("Create Account");
	private Label warning = new Label();
	private String username;
	private String passwords;
	private Label welcome = new Label("Welcome to the ASU CSE 360 Help System!");
	private Label login = new Label("Username:");
	private Label password = new Label("Password:");
	private Button redirect = new Button("Have a One-Time Password?");
	private Label inviteLabel = new Label("Have an invite code?");
	
	public LoginPage(){
		BorderPane mainPane = new BorderPane();
		
		welcome.setTextFill(Color.BLACK);
        welcome.setFont(Font.font(null, 14));
        
        login.setTextFill(Color.BLACK);
        login.setFont(Font.font(null, 14));
        
        userfield.setPromptText("Enter your username");

        password.setTextFill(Color.BLACK);
        password.setFont(Font.font(null, 14));
        
        passfield.setPromptText("Enter your password");
        
        loginbutton.setTextFill(Color.BLACK);
        loginbutton.setFont(Font.font(null, 14));

        redirect.setTextFill(Color.BLACK);
        redirect.setFont(Font.font(null, 14));
        
        inviteLabel.setTextFill(Color.BLACK);
        inviteLabel.setFont(Font.font(null, 14));
        
        inviteField.setPromptText("Enter your invite code");
        
        createAccount.setTextFill(Color.BLACK);
        createAccount.setFont(Font.font(null, 14));
        
        GridPane loginPane = new GridPane();
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setVgap(10);
        loginPane.setHgap(10);
        loginPane.setPadding(new Insets(20, 20, 20, 20));

        loginPane.add(welcome, 0, 0, 2, 1);
        loginPane.add(login, 0, 1);
        loginPane.add(userfield, 1, 1);
        loginPane.add(password, 0, 2);
        loginPane.add(passfield, 1, 2);
        loginPane.add(loginbutton, 0, 3);
        loginPane.add(warning, 0, 4);
        loginPane.add(inviteLabel, 0, 5);
        loginPane.add(inviteField, 1, 5);
        loginPane.add(createAccount, 0, 6);
        
        mainPane.setCenter(loginPane);
        this.getChildren().addAll(mainPane);
        this.setAlignment(Pos.CENTER);
        
        loginbutton.setOnAction(new ButtonHandler());
        createAccount.setOnAction(e -> mainApp.showCreateAccountPage()); // Redirect to CreateAccount page
	}
	
	private class ButtonHandler implements EventHandler<ActionEvent>{
	    public void handle(ActionEvent e){
	        try {
	        	//check to see if the add button is clicked and the textfields are filled
					if (e.getSource() == loginbutton && userfield.getText().isEmpty() != true && passfield.getText().isEmpty() != true) {
						warning.setText("");
						username = userfield.getText();
			        	passwords = passfield.getText();
			        	//Check if the database is empty. If so, set up new user as Admin
			        	if (1 == 1) {
			        		mainApp.showRoleChooser();
		                	userfield.clear();
							passfield.clear();
						}
			        	/*//if the course is new, it is added to the checkboxContainer and changing the label
	            	  if (isNew == true){
	            		  courseList.add(new Course(subject, courseN, instructor));
	            		  updateCheckBoxContainer();
						   labelLB.setText("Course added successfully");
						   labelLB.setTextFill(Color.BLACK);
						   labelLB.setFont(Font.font(null, 14));
					  }
	            	  //if the course is a duplicate, the label is changed
					   else {
						   labelLB.setText("Duplicated Course - Not added");
						   labelLB.setTextFill(Color.RED);
						   labelLB.setFont(Font.font(null, 14));
						   
					 }*/
	            	  //clear all the text fields
	            	  userfield.clear();
					  passfield.clear();
	          	    }

					//check if the addbutton is clicked and atleast one textfield is empty
	                  else if (e.getSource()  == loginbutton && (userfield.getText().isEmpty() == true || passfield.getText().isEmpty() == true)){
	                	   warning.setText("At least one field is empty.");
						   warning.setTextFill(Color.RED);
						   warning.setFont(Font.font(null, 14));
	                 }					
	           } //end of try*/

	        //exception if the courseNum is not an integer
	           catch(NumberFormatException ex){
	        	   warning.setText("Error!");
				   warning.setTextFill(Color.RED);
				   warning.setFont(Font.font(null, 14)); 
	            }
	           catch(Exception exception)
	            {
	        	   warning.setText("Error");
				   warning.setTextFill(Color.RED);
				   warning.setFont(Font.font(null, 14)); 
	            }
	        
	        } //end of handle() method                 
	} //end of ButtonHandler class
	
}

