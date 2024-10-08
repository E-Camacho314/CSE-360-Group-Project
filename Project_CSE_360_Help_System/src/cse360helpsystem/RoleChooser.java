package cse360helpsystem;

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
	private CSE360HelpSystem mainApp = new CSE360HelpSystem();
	private Label welcome = new Label("Please Choose the Role to Log In to");
	private Label warning = new Label("");
	private Button adminbutton = new Button ("Administrator");
	private Button instructbutton = new Button ("Instructor");
	private Button studbutton = new Button ("Student");
	
	public RoleChooser(){
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
        RoleChoose.setPadding(new Insets(20, 20, 20, 20));
        RoleChoose.getChildren().addAll(welcome, warning, adminbutton, instructbutton, studbutton);

        mainPane.setCenter(RoleChoose);
        this.getChildren().addAll(mainPane);
        this.setAlignment(Pos.CENTER);
        
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
}