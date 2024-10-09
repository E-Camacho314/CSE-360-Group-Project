package cse360helpsystem;

import java.sql.SQLException;

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

public class RoleChooser extends HBox {
	private CSE360HelpSystem mainApp;
	private Label welcome = new Label("Please Choose the Role to Log In to");
	private Label warning = new Label("");
	private Button adminbutton = new Button ("Administrator");
	private Button instructbutton = new Button ("Instructor");
	private Button studbutton = new Button ("Student");
	
	public RoleChooser(CSE360HelpSystem mainApp, String username){
		try {
			this.mainApp = mainApp;
			BorderPane mainPane = new BorderPane();
			
			welcome.setTextFill(Color.BLACK);
	        welcome.setFont(Font.font(null, 14));
	        
	        adminbutton.setTextFill(Color.BLACK);
	        adminbutton.setFont(Font.font(null, 14));
	        
	        instructbutton.setTextFill(Color.BLACK);
	        instructbutton.setFont(Font.font(null, 14));

	        studbutton.setTextFill(Color.BLACK);
	        studbutton.setFont(Font.font(null, 14));
	        
	        VBox RoleChoose = new VBox();
	        RoleChoose.setAlignment(Pos.CENTER);
	        RoleChoose.setPadding(new Insets(30, 30, 30, 30));
	        RoleChoose.getChildren().addAll(welcome, warning);

	        mainPane.setCenter(RoleChoose);
	        this.getChildren().addAll(mainPane);
	        this.setAlignment(Pos.CENTER);
	        
	        User user = mainApp.databaseHelper.getUserByUsername(username);
	        
	        if(user.isAdmin()) {
		        RoleChoose.getChildren().add(adminbutton);
	        }
	        if(user.isInstructor()) {
		        RoleChoose.getChildren().addAll(instructbutton);
	        }
	        if(user.isStudent()) {
		        RoleChoose.getChildren().addAll(studbutton);
	        }
	        adminbutton.setOnAction(e -> {
	            mainApp.showAdminPage(); // Switch back to the login page
	        });
	        
	        instructbutton.setOnAction(e -> {
	            mainApp.showInstructorPage(); // Switch back to the login page
	        });
	        
	        studbutton.setOnAction(e -> {
	            mainApp.showStudentPage(); // Switch back to the login page
	        });

		}
		catch (SQLException e) {
			System.err.println("Database error: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
    		System.out.println("path chosen");
		}
	}
}