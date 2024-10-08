package cse360helpsystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class StudentPage extends HBox {
	private Label welcome = new Label("Student View");
	private Button logoutbutton = new Button ("Log Out");
	private CSE360HelpSystem mainApp = new CSE360HelpSystem();
	
	public StudentPage(){
		BorderPane mainPane = new BorderPane();
		
		welcome.setTextFill(Color.BLUE);
        welcome.setFont(Font.font(null, 14));

        logoutbutton.setTextFill(Color.BLUE);
        logoutbutton.setFont(Font.font(null, 14));
        
        VBox studPane = new VBox();
        studPane.setSpacing(20);
        studPane.setPadding(new Insets(10, 10, 10, 10));
        studPane.getChildren().addAll(welcome, logoutbutton);

        mainPane.setCenter(studPane);
        this.getChildren().addAll(mainPane);
        this.setAlignment(Pos.CENTER);
        
        logoutbutton.setOnAction(e -> {
            mainApp.showLoginPage(); // Switch back to the login page
        });

	}
}
