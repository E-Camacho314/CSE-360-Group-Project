package cse360helpsystem;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

public class CSE360HelpSystem extends Application
{
    public static final int WIDTH = 600, HEIGHT = 400;

    public void start(Stage stage)
    {
        StackPane root = new StackPane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setTitle("CSE 360 Help System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
