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
    private static final DatabaseHelper databaseHelper = new DatabaseHelper();
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
    	databaseHelper.emptyDatabase();
    	adminpage = new AdminPage();
    	loginpage = new LoginPage();
    	studentpage = new StudentPage();
    	instructorpage = new InstructorPage();
    	rolechoose = new RoleChooser();
        root.getChildren().add(loginpage);
        Scene scene = new Scene(root, WIDTH, HEIGHT);        
        stage.setTitle("CSE 360 Help System");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
     
	public void showCreateAccountPage() {
        String invitationCode = inviteField.getText().trim(); // Get the invitation code
        CreateAccount createAccountPage = new CreateAccount(this, invitationCode); // Pass it to CreateAccount
        root.getChildren().clear();
        root.getChildren().add(createAccountPage);
        //Creating a new scene seems to have been the problem with createAccount being unable to go back to LoginPage
        /*Scene createAccountScene = new Scene(createAccountPage, 400, 300);
        Stage stage = (Stage) createAccount.getScene().getWindow();
        stage.setScene(createAccountScene);*/
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
    public void showRoleChooser() {
        root.getChildren().clear();
        root.getChildren().add(rolechoose);
        System.out.println("Switched to Instructor Page"); // For debugging
    }

    public static void main(String[] args)
    {
    	try {
    	    Class.forName("org.sqlite.JDBC");
    	} catch (ClassNotFoundException e) {
    	    e.printStackTrace();
    	}
    	try { 
			
			databaseHelper.connectToDatabase();  // Connect to the database
		    launch(args);
		} catch (SQLException e) {
			System.err.println("Database error: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			databaseHelper.closeConnection();
		}
    }
}