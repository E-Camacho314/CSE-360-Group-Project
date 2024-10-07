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

public class AdminPage extends HBox {
	private Label welcome = new Label("Admin View");
	private Button invitebutton = new Button ("Invite User");
	private Button resetbutton = new Button ("Reset User");
	private Button deletebutton = new Button ("Delete User");
	private Button listbutton = new Button ("List Users");
	private Button addpermsbutton = new Button ("Add Roles to User");
	private Button removepermsbutton = new Button ("Remove Roles from User");
	private Button logoutbutton = new Button ("Log Out");
	
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

	}
}
