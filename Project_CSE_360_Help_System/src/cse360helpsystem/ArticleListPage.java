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

/**
 * <p>ArticleListPage Class</p>
 * 
 * <p>Description: The ArticleListPage class represents a page in the CSE360HelpSystem that displays a list of articles from the database. </p>
 * 
 * <p>Authors: Erik Camacho, Thienban Nguyen, Sarvesh Shanmugam, Ivan Mancillas, Tanis Peterson</p>
 */

public class ArticleListPage extends VBox {
	
	// UI components
    private CSE360HelpSystem mainApp;
    private String prev;
    private long id;
    private List<Long> idList;
    private Label pageTitle = new Label("All Articles");
    private Label warning = new Label("");
    private Button returnButton = new Button("Return to Main");
    private GridPane articlesGrid = new GridPane(); 

     /**
     * Constructor for ArticleListPage.
     * Initializes the page, sets up the table with columns, loads the data, and configures the layout.
     * 
     * @param mainApp the main application instance to facilitate page navigation and database access
     * @param prev string to point at previous page
     * @param id ID of the article to display
     * @param idList list of articles to be displayed by id
     */
    public ArticleListPage(CSE360HelpSystem mainApp, String prev, long id, List<Long> idList) {
        this.mainApp = mainApp;
        this.idList = idList;
        this.prev = prev;
        this.id = id;
        initializeUI();
    }

    // Method to initialize the UI components and layout
    private void initializeUI() {
        BorderPane mainPane = new BorderPane();
        
        // Set page title properties
        pageTitle.setTextFill(Color.BLACK);
        pageTitle.setFont(Font.font(null, 20));
        warning.setTextFill(Color.RED);

        // Set return button properties and action
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
        } else if(id == -1){
            pageTitle.setText("Showing Groups");
        	List<String> articles = fetchAllArticleGroup(idList);
            displayArticles(articles);
        }else {
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
    
    // Fetches a list of all articles with titles, IDs, and abstracts
    private List<String> fetchAllArticleGroup(List<Long> idList) {
    	List<String> articles = new ArrayList<>();
        try {
            articles = mainApp.databaseHelper.getAllArticlesGroups(idList); 
        } catch (SQLException e) {
            warning.setText("Failed to retrieve articles.");
            e.printStackTrace();
        }
        return articles;
    }

    // Displays articles in the GridPane
    private void displayArticles(List<String> articles) {
        if(articles.isEmpty()) {
        	pageTitle.setText("No Articles Found");
        	pageTitle.setTextFill(Color.RED);
        	return;
        }
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

