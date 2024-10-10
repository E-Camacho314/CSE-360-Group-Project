package cse360helpsystem;

import java.sql.SQLException;
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
    public static final int WIDTH = 400, HEIGHT = 400;
    public static final DatabaseHelper databaseHelper = new DatabaseHelper();
	private static StackPane root = new StackPane();
	private static LoginPage loginpage;
	private static AdminPage adminpage;
	private static StudentPage studentpage;
	private static InstructorPage instructorpage;
	private static RoleChooser rolechoose;
	private TextField inviteField = new TextField();
	private Button createAccount = new Button ("Create Account");

    public void start(Stage stage)
    {
    	try {
        	adminpage = new AdminPage(this);
        	loginpage = new LoginPage(this);
        	studentpage = new StudentPage(this);
        	instructorpage = new InstructorPage(this);
        	if(databaseHelper.isDatabaseEmpty()) {
        		showCreateAccountPage("");
        	}
        	else {
        		root.getChildren().add(loginpage);
        	}
        	Scene scene = new Scene(root, WIDTH, HEIGHT);        
            stage.setTitle("CSE 360 Help System");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
    	}
    	catch(SQLException e) {
			System.err.println("Database error: " + e.getMessage());
			e.printStackTrace();
    	}
    	finally {
    		System.out.println("path chosen");
    	}
    }
     
	public void showCreateAccountPage(String invite) {
        CreateAccount createAccountPage = new CreateAccount(this, invite); // Pass it to CreateAccount
        root.getChildren().clear();
        root.getChildren().add(createAccountPage);
    }
	
	public void showFinishSetupPage(String username) {
        FinishSetupPage finishsetupPage = new FinishSetupPage(this, username); // Pass it to FinishSetupPage
        root.getChildren().clear();
        root.getChildren().add(finishsetupPage);
    }
	    
    // method used by other pages to return to login page
    public void showLoginPage() {
        root.getChildren().clear();
        root.getChildren().add(loginpage);
        System.out.println("Switched to Login Page"); // For debugging
    }
    
    // method used by other pages to return to admin page
    public void showAdminPage() {
        root.getChildren().clear();
        root.getChildren().add(adminpage);
        System.out.println("Switched to Admin Page"); // For debugging
    }
    
    // method used by other pages to return to student page
    public void showStudentPage() {
        root.getChildren().clear();
        root.getChildren().add(studentpage);
        System.out.println("Switched to Student Page"); // For debugging
    }
    
    // method used by other pages to return to instructor page
    public void showInstructorPage() {
        root.getChildren().clear();
        root.getChildren().add(instructorpage);
        System.out.println("Switched to Instructor Page"); // For debugging
    }
    
    // method used by other pages to return to rolechooser page
    public void showRoleChooser(String username) {
        root.getChildren().clear();
    	RoleChooser rolechoose = new RoleChooser(this, username);
        root.getChildren().add(rolechoose);
        System.out.println("Switched to Role Chooser Page"); // For debugging
    }
    
    // method used by other pages to return to rolechooser page
    public void showNewPass(String username) {
    	NewPassword pass = new NewPassword(this, username);
    	root.getChildren().clear();
        root.getChildren().add(pass);
        System.out.println("Switched to Role Chooser Page"); // For debugging
    }
    
    public void showListPage() {
        ListPage listPage = new ListPage(this); // Create a new ListPage instance
        root.getChildren().clear();
        root.getChildren().add(listPage);
        System.out.println("Switched to List Page"); // For debugging
    }
    
    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }
    
    public static void main(String[] args)
    {
    	try {
    	    Class.forName("org.sqlite.JDBC");
    	} catch (ClassNotFoundException e) {
    	    e.printStackTrace();
    	}
    	try { 
			databaseHelper.connectToDatabase();
			databaseHelper.emptyDatabase();
			databaseHelper.closeConnection();
			databaseHelper.connectToDatabase();  // Connect to the database
		    launch(args);
		} catch (SQLException e) {
			System.err.println("Database error: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			System.out.println("Connected");
		}
    }
}