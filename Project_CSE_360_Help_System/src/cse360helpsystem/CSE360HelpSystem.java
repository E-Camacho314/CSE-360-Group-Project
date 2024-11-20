package cse360helpsystem;

import java.sql.SQLException;
import java.util.List;

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
 * <p> Description: A JavaFX application uses a GUI and that connects to a database and allows users to perform various functions.</p>
 * 
 * @author Erik Camacho, Thienban Nguyen, Sarvesh Shanmugam, Ivan Mancillas, Tanis Peterson
 * 
 */

public class CSE360HelpSystem extends Application
{
	// Constants for window dimensions
    public static final int WIDTH = 600, HEIGHT = 600;
    
    // Singleton instance of the DatabaseHelper class to manage database connections
    public static DatabaseHelper databaseHelper;
    
    // StackPane root will hold the current UI page
	private static StackPane root = new StackPane();
	
	// Pages for different roles and functionalities
	private static LoginPage loginpage;
	private static AdminPage adminpage;
	private static StudentPage studentpage;
	private static InstructorPage instructorpage;
	private static RoleChooser rolechoose;
	
	// UI components for creating a new account
	private TextField inviteField = new TextField();
	private Button createAccount = new Button ("Create Account");

	/**
     * The main entry point for the JavaFX application.
     * Initializes the different pages and loads the correct UI based on the database state.
	 * @throws Exception 
     */
    public void start(Stage stage) throws Exception
    {
    	try {
    		// Initialize different pages (Admin, Login, Student, Instructor)
        	adminpage = new AdminPage(this);
        	loginpage = new LoginPage(this);
        	studentpage = new StudentPage(this);
        	instructorpage = new InstructorPage(this);
        	
        	// Check if the database is empty
        	if(databaseHelper.isDatabaseEmpty()) {
        		showCreateAccountPage("");
        	}
        	else {
        		// If users exist, load the login page
        		root.getChildren().add(loginpage);
        	}
        	
        	// Create the main scene for the application
        	Scene scene = new Scene(root, WIDTH, HEIGHT);        
            stage.setTitle("CSE 360 Help System");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
    	}
    	catch(SQLException e) {
    		// Handle any SQL exceptions and print the error message
			System.err.println("Database error: " + e.getMessage());
			e.printStackTrace();
    	}
    	finally {
    		System.out.println("path chosen");
    	}
    }
    
    // Displays the Create Account page where users can enter an invitation code to sign up
	public void showCreateAccountPage(String invite) {
        CreateAccount createAccountPage = new CreateAccount(this, invite); // Pass it to CreateAccount
        root.getChildren().clear();
        root.getChildren().add(createAccountPage);
    }
	
	// Displays the Finish Setup page for completing account setup after the user is created
	public void showFinishSetupPage(String username) {
        FinishSetupPage finishsetupPage = new FinishSetupPage(this, username); // Pass it to FinishSetupPage
        root.getChildren().clear();
        root.getChildren().add(finishsetupPage);
    }
	
	// Displays the Articles page for manipulating articles if the user is an instructor or admin
	public void showArticlesPage(String prev) {
        ArticlesPage articlesPage = new ArticlesPage(this, prev); // Pass it to ArticlesPage
        root.getChildren().clear();
        root.getChildren().add(articlesPage);
    }
	
	// Displays the Search page for searching articles
	public void showSearchPage(String prev) {
        SearchPage searchPage = new SearchPage(this, prev); // Pass it to ArticlesPage
        root.getChildren().clear();
        root.getChildren().add(searchPage);
    }
	    
    // Displays the login page
    public void showLoginPage() {
        root.getChildren().clear();
        root.getChildren().add(loginpage);
        System.out.println("Switched to Login Page"); // For debugging
    }
    
    // Displays the article creation page
    public void showArticleCreatePage(String prev, long id) {
        ArticleCreationPage articlecreatePage = new ArticleCreationPage(this, prev, id); // Pass it to ArticlesPage
        root.getChildren().clear();
        root.getChildren().add(articlecreatePage);
        System.out.println("Switched to Article Creation Page"); // For debugging
    }
    
    // Displays the special access group page
    public void showSpecialAccessPage(String prev, String name, Boolean access) {
        SpecialAccess specialaccessPage = new SpecialAccess(this, prev, name, access); // Pass it to ArticlesPage
        root.getChildren().clear();
        root.getChildren().add(specialaccessPage);
        System.out.println("Switched to Special Access Page"); // For debugging
    }
    
    // Displays the admin page for admin users
    public void showAdminPage() {
        root.getChildren().clear();
        root.getChildren().add(adminpage);
        System.out.println("Switched to Admin Page"); // For debugging
    }
    
    // Displays the student page for student users
    public void showStudentPage() {
        root.getChildren().clear();
        root.getChildren().add(studentpage);
        System.out.println("Switched to Student Page"); // For debugging
    }
    
    // Displays the instructor page for instructor users
    public void showInstructorPage() {
        root.getChildren().clear();
        root.getChildren().add(instructorpage);
        System.out.println("Switched to Instructor Page"); // For debugging
    }
    
    // Displays the role chooser page for users who need to select their role
    public void showRoleChooser(String username) {
        root.getChildren().clear();
    	RoleChooser rolechoose = new RoleChooser(this, username);
        root.getChildren().add(rolechoose);
        System.out.println("Switched to Role Chooser Page"); // For debugging
    }
    
    // Displays a new password to be used for the user
    public void showNewPass(String username) {
    	NewPassword pass = new NewPassword(this, username);
    	root.getChildren().clear();
        root.getChildren().add(pass);
        System.out.println("Switched to New Password Page"); // For debugging
    }
    
    // Displays a page containing a list of information for the admin
    public void showListPage() {
        ListPage listPage = new ListPage(this); // Create a new ListPage instance
        root.getChildren().clear();
        root.getChildren().add(listPage);
        System.out.println("Switched to List Page"); // For debugging
    }
    
    // Displays a page containing a list of information for the articles
    public void showArticlesListPage(String prev, long id, List<Long> idList) {
        ArticleListPage articlelistPage = new ArticleListPage(this, prev, id, idList); // Create a new ListPage instance
        root.getChildren().clear();
        root.getChildren().add(articlelistPage);
        System.out.println("Switched to Articles List Page"); // For debugging
    }
    
    // Provides access to the database helper for other parts of the application
    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }
    
    // The main method that sets up the database connection and launches the JavaFX application
    public static void main(String[] args) throws Exception
    {
    	try {
    		// Load the SQLite JDBC driver
    	    Class.forName("org.sqlite.JDBC");
    	} catch (ClassNotFoundException e) {
    	    e.printStackTrace();
    	}
    	try { 
    		databaseHelper = new DatabaseHelper();
			databaseHelper.connectToDatabase();
			databaseHelper.emptySpecial();
			//databaseHelper.emptyDatabase();		// Empty the database for testing purposes
			databaseHelper.closeConnection();
			databaseHelper.connectToDatabase();  // Connect to the database
			databaseHelper.logoutAllUsers();
		    launch(args);
		} catch (SQLException e) {
			// Handle SQL exceptions and print error message
			System.err.println("Database error: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			System.out.println("Connected");
		}
    }
}