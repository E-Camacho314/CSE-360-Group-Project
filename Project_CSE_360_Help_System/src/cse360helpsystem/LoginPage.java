package cse360helpsystem;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

public class LoginPage extends HBox {
	private Label welcome = new Label("Welcome to the ASU CSE 360 Help System!");
	private Label login = new Label("Username:");
	private TextField userfield = new TextField();
	private Label password = new Label("Password:");
	private TextField passfield = new TextField();
	private Button loginbutton = new Button ("Log In");
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
        loginPane.getChildren().addAll(welcome, login, userfield, password, passfield, loginbutton, redirect);

        mainPane.setCenter(loginPane);
        this.getChildren().addAll(mainPane);
        this.setAlignment(Pos.CENTER);

	}
}
