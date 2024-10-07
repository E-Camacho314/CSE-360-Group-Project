package cse360helpsystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class InstructorPage extends HBox {
	private Label welcome = new Label("Instructor View");
	private Button logoutbutton = new Button ("Log Out");
	
	public InstructorPage(){
		BorderPane mainPane = new BorderPane();
		
		welcome.setTextFill(Color.BLUE);
        welcome.setFont(Font.font(null, 14));

        logoutbutton.setTextFill(Color.BLUE);
        logoutbutton.setFont(Font.font(null, 14));
        
        VBox instructPane = new VBox();
        instructPane.setSpacing(20);
        instructPane.setPadding(new Insets(10, 10, 10, 10));
        instructPane.getChildren().addAll(welcome, logoutbutton);

        mainPane.setCenter(instructPane);
        this.getChildren().addAll(mainPane);
        this.setAlignment(Pos.CENTER);

	}
}