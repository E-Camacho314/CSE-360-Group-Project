package cse360helpsystem;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

public class AdminPage extends HBox {
	private CSE360HelpSystem mainApp = new CSE360HelpSystem();
	private Label welcome = new Label("Admin View");
	private Label userinfo = new Label("Manipulate Users:");
	private Label perms = new Label("Permissions:");
	private Button invitebutton = new Button ("Invite User");
	private Button resetbutton = new Button ("Reset User");
	private Button deletebutton = new Button ("Delete User");
	private Button listbutton = new Button ("List Users");
	private Button addpermsbutton = new Button ("Add Roles");
	private Button removepermsbutton = new Button ("Remove Roles");
	private Button logoutbutton = new Button ("Log Out");
	
	public AdminPage(){
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
        
        addpermsbutton.setTextFill(Color.BLACK);
        addpermsbutton.setFont(Font.font(null, 14));
        
        removepermsbutton.setTextFill(Color.RED);
        removepermsbutton.setFont(Font.font(null, 14));
        
        logoutbutton.setTextFill(Color.BLACK);
        logoutbutton.setFont(Font.font(null, 14));
        
        GridPane adminPane = new GridPane();
        adminPane.setAlignment(Pos.CENTER);
        adminPane.setVgap(10);
        adminPane.setHgap(10);
        adminPane.setPadding(new Insets(20, 20, 20, 20));

        adminPane.add(welcome, 0, 0, 2, 1);
        adminPane.add(userinfo, 0, 1);
        adminPane.add(invitebutton, 0, 2);
        adminPane.add(resetbutton, 1, 2);
        adminPane.add(deletebutton, 1, 3);
        adminPane.add(listbutton, 0, 3);
        adminPane.add(perms, 0, 4);
        adminPane.add(addpermsbutton, 0, 5);
        adminPane.add(removepermsbutton, 1, 5);
        adminPane.add(logoutbutton, 0, 6);

        mainPane.setCenter(adminPane);
        this.getChildren().addAll(mainPane);
        this.setAlignment(Pos.CENTER);
        
        logoutbutton.setOnAction(new ButtonHandler());
        logoutbutton.setOnAction(e -> {
            mainApp.showLoginPage(); // Switch back to the login page
        });

	}
	
	private class ButtonHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent e){
            /*try {
            	//check to see if the add button is clicked and the textfields are filled
					if (e.getSource() == loginbutton && userfield.getText().isEmpty() != true && passfield.getText().isEmpty() != true) {
						warning.setText("");
						username = userfield.getText();
			        	passwords = passfield.getText();
			        	//Check if the database is empty. If so, set up new user as Admin
			        	if (1 == 1) {
			        		sceneChanger("admin");
		                	userfield.clear();
							passfield.clear();
						}
			        	else {
			        		sceneChanger("logout");
		                	userfield.clear();
							passfield.clear();
			        	}
			        	//if the course is new, it is added to the checkboxContainer and changing the label
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
						   
					 }
                	  //clear all the text fields
                	  userfield.clear();
					  passfield.clear();
              	    }

					//check if the addbutton is clicked and atleast one textfield is empty
	                  else if (e.getSource()  == loginbutton && (userfield.getText().isEmpty() == true || passfield.getText().isEmpty() == true)){
	                	   warning.setText("At least one field is empty. Fill all fields");
						   warning.setTextFill(Color.RED);
						   warning.setFont(Font.font(null, 14));
	                 }

					//check if the dropbutton was clicked
	                else if (e.getSource() == logoutbutton){
	                	
	                } 
	               else{
						throw new Exception();
					}

	           } //end of try*/

            /*//exception if the courseNum is not an integer
	           catch(NumberFormatException ex){
	        	   labelLB.setText("Error! Course number must be an integer");
				   labelLB.setTextFill(Color.RED);
				   labelLB.setFont(Font.font(null, 14)); 
	            }
	           catch(Exception exception)
	            {
	        	   labelLB.setText("Error");
				   labelLB.setTextFill(Color.RED);
				   labelLB.setFont(Font.font(null, 14)); 
	            }*/
            
	        } //end of handle() method                 
    } //end of ButtonHandler class
}