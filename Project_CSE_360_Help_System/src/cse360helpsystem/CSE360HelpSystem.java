package cse360helpsystem;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
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

    public void start(Stage stage)
    {
        StackPane root = new StackPane();
        AdminPage loginpage = new AdminPage();
        root.getChildren().add(loginpage);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setTitle("CSE 360 Help System");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
