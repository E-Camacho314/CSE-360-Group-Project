package cse360helpsystem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ArticleListPage extends VBox {
    private CSE360HelpSystem mainApp;
    private String prev;
    private long id;
    private Label pageTitle = new Label("All Articles");
    private Label warning = new Label("");
    private Button returnButton = new Button("Return to Main");
    private GridPane articlesGrid = new GridPane(); 

    public ArticleListPage(CSE360HelpSystem mainApp, String prev, long id) {
        this.mainApp = mainApp;
        this.prev = prev;
        this.id = id;
        initializeUI();
    }

    private void initializeUI() {
        BorderPane mainPane = new BorderPane();
        
        pageTitle.setTextFill(Color.BLACK);
        pageTitle.setFont(Font.font(null, 20));
        warning.setTextFill(Color.RED);

        returnButton.setTextFill(Color.BLACK);
        returnButton.setFont(Font.font(null, 14));
        returnButton.setOnAction(e -> mainApp.showArticlesPage(prev));
        
        // Configure articlesGrid for listing article titles
        articlesGrid.setAlignment(Pos.CENTER);
        articlesGrid.setVgap(10);
        articlesGrid.setHgap(10);
        articlesGrid.setPadding(new Insets(20, 20, 20, 20));

        // Fetch and display articles based on ID
        if (id == 0) {
            List<String> articles = fetchAllArticles();
            displayArticles(articles);
        } else {
        	pageTitle.setText("Showing Article: " + id);
            viewArticleById(id);
        }

        // Organize layout
        VBox headerPane = new VBox(10, pageTitle, warning);
        headerPane.setAlignment(Pos.CENTER);
        
        mainPane.setTop(headerPane);
        mainPane.setCenter(articlesGrid);

        // Add to VBox
        this.getChildren().addAll(mainPane, returnButton);
        this.setAlignment(Pos.CENTER);
    }

    // Fetches a list of all articles with titles, IDs, and abstracts
    private List<String> fetchAllArticles() {
        List<String> articles = new ArrayList<>();
        try {
            articles = mainApp.databaseHelper.getAllArticlesLimited(); 
        } catch (SQLException e) {
            warning.setText("Failed to retrieve articles.");
            e.printStackTrace();
        }
        return articles;
    }

    // Displays articles in the GridPane
    private void displayArticles(List<String> articles) {
        int row = 0;
        articlesGrid.getChildren().clear();
        
        for (String articleInfo : articles) {
            Label articleLabel = new Label(articleInfo);
            articleLabel.setTextFill(Color.BLACK);
            articleLabel.setFont(Font.font(null, 16));
            articlesGrid.add(articleLabel, 0, row++);
        }
    }

    // View a specific article by ID and display in TextArea
    private void viewArticleById(long id) {
        try {
        	int row = 0;
            String articleDetails = mainApp.databaseHelper.getArticleDetailsById(id);
            Label articleLabel = new Label(articleDetails); // Display article details in TextArea
            articleLabel.setTextFill(Color.BLACK);
            articleLabel.setFont(Font.font(null, 16));
            articlesGrid.add(articleLabel, 0, row++);
            warning.setTextFill(Color.GREEN);
            warning.setText("Article details displayed below.");
        } catch (Exception e) {
            warning.setText("Failed to retrieve article details.");
            warning.setTextFill(Color.RED);
            e.printStackTrace();
        }
    }
}
