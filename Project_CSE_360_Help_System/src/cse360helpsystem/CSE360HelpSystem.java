package cse360helpsystem;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

/**
 * <p> CSE360HelpSystem Class </p>
 * 
 * <p> Description: .</p>
 * 
 * @author Erik Camacho, Thienban Nguyen, Sarvesh Shanmugam, Ivan Mancillas, Tanis Peterson
 * 
 */

public class CSE360HelpSystem extends Application
{
    public static final int WIDTH = 600, HEIGHT = 400;
	private static StackPane root = new StackPane();
	private static LoginPage loginpage;
	private static AdminPage adminpage;
	private TextField passfield = new TextField();
	private TextField userfield = new TextField();
	private Button loginbutton = new Button ("Log In");
	private Button logoutbutton = new Button ("Log Out");
	private Label warning = new Label();
	private String username;
	private String passwords;

    public void start(Stage stage)
    {
    	adminpage = new AdminPage();
    	loginpage = new LoginPage();
        root.getChildren().add(loginpage);
        Scene scene = new Scene(root, WIDTH, HEIGHT);        
        stage.setTitle("CSE 360 Help System");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
    
    public class LoginPage extends HBox {
    	private Label welcome = new Label("Welcome to the ASU CSE 360 Help System!");
    	private Label login = new Label("Username:");
    	private Label password = new Label("Password:");
    	private Button redirect = new Button("Have a One-Time Password?");
    	
    	public LoginPage(){
    		BorderPane mainPane = new BorderPane();
    		
    		welcome.setTextFill(Color.BLUE);
            welcome.setFont(Font.font(null, 14));
            
            login.setTextFill(Color.BLACK);
            login.setFont(Font.font(null, 14));

            password.setTextFill(Color.BLACK);
            password.setFont(Font.font(null, 14));
            
            loginbutton.setTextFill(Color.BLUE);
            loginbutton.setFont(Font.font(null, 14));

            redirect.setTextFill(Color.BLUE);
            redirect.setFont(Font.font(null, 14));
            
            VBox loginPane = new VBox();
            loginPane.setSpacing(20);
            loginPane.setPadding(new Insets(10, 10, 10, 10));
            loginPane.getChildren().addAll(welcome, warning, login, userfield, password, passfield, loginbutton, redirect);

            mainPane.setCenter(loginPane);
            this.getChildren().addAll(mainPane);
            this.setAlignment(Pos.CENTER);
            
            loginbutton.setOnAction(new ButtonHandler());

    	}
    }
    
	public class AdminPage extends HBox {
		private Label welcome = new Label("Admin View");
		private Button invitebutton = new Button ("Invite User");
		private Button resetbutton = new Button ("Reset User");
		private Button deletebutton = new Button ("Delete User");
		private Button listbutton = new Button ("List Users");
		private Button addpermsbutton = new Button ("Add Roles to User");
		private Button removepermsbutton = new Button ("Remove Roles from User");
		
		public AdminPage(){
			BorderPane mainPane = new BorderPane();
			
			welcome.setTextFill(Color.BLUE);
	        welcome.setFont(Font.font(null, 14));

	        invitebutton.setTextFill(Color.BLUE);
	        invitebutton.setFont(Font.font(null, 14));
	        
	        resetbutton.setTextFill(Color.BLUE);
	        resetbutton.setFont(Font.font(null, 14));
	        
	        deletebutton.setTextFill(Color.RED);
	        deletebutton.setFont(Font.font(null, 14));
	        
	        listbutton.setTextFill(Color.BLUE);
	        listbutton.setFont(Font.font(null, 14));
	        
	        addpermsbutton.setTextFill(Color.BLUE);
	        addpermsbutton.setFont(Font.font(null, 14));
	        
	        removepermsbutton.setTextFill(Color.RED);
	        removepermsbutton.setFont(Font.font(null, 14));
	        
	        logoutbutton.setTextFill(Color.BLUE);
	        logoutbutton.setFont(Font.font(null, 14));
	        
	        VBox adminPane = new VBox();
	        adminPane.setSpacing(20);
	        adminPane.setPadding(new Insets(10, 10, 10, 10));
	        adminPane.getChildren().addAll(welcome,invitebutton, resetbutton, deletebutton, listbutton, addpermsbutton, removepermsbutton, logoutbutton);

	        mainPane.setCenter(adminPane);
	        this.getChildren().addAll(mainPane);
	        this.setAlignment(Pos.CENTER);
	        
	        logoutbutton.setOnAction(new ButtonHandler());

		}
	}
	
	private class ButtonHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent e){
            try {
            	//check to see if the add button is clicked and the textfields are filled
					if (e.getSource() == loginbutton && userfield.getText().isEmpty() != true && passfield.getText().isEmpty() != true) {
						warning.setText("");
						username = userfield.getText();
			        	passwords = passfield.getText();
			        	/*//Check if the database is empty. If so, set up new user as Admin
			        	if (databaseHelper.isDatabaseEmpty()) {
			        		databaseHelper.register(username, passwords, "admin");
			        		sceneChanger("admin");
		                	userfield.clear();
							passfield.clear();
						}
			        	else if (databaseHelper.login(username, passwords, "admin")) {
			        		sceneChanger("admin");
		                	userfield.clear();
							passfield.clear();
			    		} 
			        	else {
			        		sceneChanger("logout");
		                	userfield.clear();
							passfield.clear();
			        	}
			        /*	//if the course is new, it is added to the checkboxContainer and changing the label
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
						   
					 } */
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
	                	sceneChanger("logout");
	                } 
	               else{
						throw new Exception();
					}

	           } //end of try

            //exception if the courseNum is not an integer
	           catch(NumberFormatException ex){
	        	 /*  labelLB.setText("Error! Course number must be an integer");
				   labelLB.setTextFill(Color.RED);
				   labelLB.setFont(Font.font(null, 14)); */
	            }
	           catch(Exception exception)
	            {
	        	 /*  labelLB.setText("Error");
				   labelLB.setTextFill(Color.RED);
				   labelLB.setFont(Font.font(null, 14)); */
	            }
            
	        } //end of handle() method                 
    } //end of ButtonHandler class
    
    private static void sceneChanger(String needed){
    	root.getChildren().clear();
    	if(needed.equals("admin")) {
    		root.getChildren().add(adminpage);
    	}
    	else if(needed.equals("logout")) {
    		root.getChildren().add(loginpage);
    	}
    }


    public static void main(String[] args)
    {
		    launch(args);
    }
}
