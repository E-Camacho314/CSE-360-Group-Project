package cse360helpsystem;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SearchPage extends VBox {

	// Reference to the main application to facilitate navigation between pages
    private CSE360HelpSystem mainApp;
    
    // Role of the currently logged-in user
    private String prev;
    
    // UI Components
    private Label titleLabel;
    private Label diffLabel;
    private Label groupLabel;
    private Label phraseLabel;
    private Label idLabel;
    private TextField groupField;
    private TextField phraseField;
    private TextField idField;
    private CheckBox beginner = new CheckBox ("Beginner");
	private CheckBox intermediate = new CheckBox ("Intermediate");
	private CheckBox advanced = new CheckBox ("Advanced");
	private CheckBox expert = new CheckBox ("Expert");
	private CheckBox all = new CheckBox ("All");
    private Button diffButton;
    private Button groupButton;
    private Button phraseButton;
    private Button idButton;
    private Button backButton;
    private Label messageLabel;

    /**
     * Constructor for SearchPage.
     * Initializes the UI components and sets up the layout.
     * 
     * @param mainApp the main application instance for navigation
     * @param prev the role of the current user
     */
    public SearchPage(CSE360HelpSystem mainApp, String prev) {
        this.mainApp = mainApp;
        this.prev = prev;
        initializeUI();
    }

    /**
     * Initializes and configures all UI components and layouts.
     * Sets up labels, text fields, buttons, and their event handlers.
     */
    private void initializeUI() {
        // Title
        titleLabel = new Label("Search Articles");
        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setFont(Font.font(16));

        // By Difficulty
        diffLabel = new Label("Search By Difficulty:");
        diffLabel.setTextFill(Color.BLACK);
        diffLabel.setFont(Font.font(14));

        // By Group 
        groupLabel = new Label("Search by Group:");
        groupLabel.setTextFill(Color.BLACK);
        groupLabel.setFont(Font.font(14));
        groupField = new TextField();
        groupField.setPromptText("Enter the Group");

        // By Words, Names, or Phrases 
        phraseLabel = new Label("Search by Words, Names, or Phrases:");
        phraseLabel.setTextFill(Color.BLACK);
        phraseLabel.setFont(Font.font(14));
        phraseField = new TextField();
        phraseField.setPromptText("Enter the Word or Phrase");
        
        // By ID
        idLabel = new Label("Search by ID:");
        idLabel.setTextFill(Color.BLACK);
        idLabel.setFont(Font.font(14));
        idField = new TextField();
        idField.setPromptText("Enter the Article ID");

        // Buttons
        diffButton = new Button("Search");
        diffButton.setFont(Font.font(14));
        groupButton = new Button("Search");
        groupButton.setFont(Font.font(14));
        phraseButton = new Button("Search");
        phraseButton.setFont(Font.font(14));
        idButton = new Button("Search");
        idButton.setFont(Font.font(14));
        backButton = new Button("Back to Main");
        backButton.setFont(Font.font(14));

        // Message Label
        messageLabel = new Label();
        messageLabel.setTextFill(Color.BLACK);
        messageLabel.setFont(Font.font(14));

        // Create a GridPane for organizing labels and text fields in a grid layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        // Adding components to the grid
        grid.add(titleLabel, 0, 0, 2, 1);
        grid.add(diffLabel, 0, 1);
        grid.add(all, 1, 1);        
        grid.add(beginner, 0, 2);
        grid.add(intermediate, 1, 2);
        grid.add(advanced, 0, 3);
        grid.add(expert, 1, 3);
        grid.add(diffButton, 2, 3);
        grid.add(groupLabel, 0, 4);
        grid.add(groupField, 0, 5);
        grid.add(groupButton, 1, 5);
        grid.add(phraseLabel, 0, 6);
        grid.add(phraseField, 0, 7);
        grid.add(phraseButton, 1, 7);
        grid.add(idLabel, 0, 8);
        grid.add(idField, 0, 9);
        grid.add(idButton, 1, 9);
        grid.add(backButton, 0, 10);
        grid.add(messageLabel, 0, 11, 2, 1);

        // Align buttons to the right
        GridPane.setMargin(backButton, new Insets(10, 0, 0, 0));

        // Add GridPane to VBox
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(grid);
        
        // SearchByDifficulty Button
        diffButton.setOnAction(e -> {
			try {
				searchByDifficulty();
			} catch (JSONException | SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        
        // SearchbyGroup Button
        groupButton.setOnAction(e -> {
			try {
				searchByGroup();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        
        // SearchbyPhrase Button
        phraseButton.setOnAction(e -> {
			try {
				searchByPhrase();
			} catch (JSONException | SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        
        // SearchByID Button
        idButton.setOnAction(e -> searchByID());
        
        // Button Actions
        backButton.setOnAction(e -> returnToPage(prev));
    }
    
    // Method to handle switching to previous page
    private void returnToPage(String prev) {
    	if(prev.equals("admin")) {
    		mainApp.showAdminPage();
    	}
    	if(prev.equals("instructor")) {
    		mainApp.showInstructorPage();
    	}
    	if(prev.equals("student")) {
    		mainApp.showStudentPage();
    	}
    }
    
    private void searchByDifficulty() throws JSONException, SQLException {
        // Determine selected levels
        boolean isBeginner = beginner.isSelected();
        boolean isIntermediate = intermediate.isSelected();
        boolean isAdvanced = advanced.isSelected();
        boolean isExpert = expert.isSelected();
        boolean isAll = all.isSelected();
        String username = mainApp.databaseHelper.findLoggedInUser();

        // Ensure at least one role is selected
        if(!isAll) {
            if (!isBeginner && !isIntermediate && !isAdvanced && !isExpert) {
                messageLabel.setText("Select at least one level");
                messageLabel.setTextFill(Color.RED);
                return;
            }
        }
        else {
        	isBeginner = true;
        	isIntermediate = true;
        	isAdvanced = true;
        	isExpert = true;
        }
        List<Long> idList = mainApp.databaseHelper.getArticlesByDifficulty(username, isBeginner, isIntermediate, isAdvanced, isExpert);
        mainApp.showArticlesListPage(prev, "",  -2, idList, true);
    }
    
    private void searchByGroup() throws SQLException {
    	if(groupField.getText().isEmpty()) {
    		messageLabel.setText("Please enter a valid group");
            messageLabel.setTextFill(Color.RED);
            return;
    	}
    	String group = groupField.getText();
    	
        String username = mainApp.databaseHelper.findLoggedInUser();
    	List<Long> idList = mainApp.databaseHelper.searchArticlesByGroups(username, group);
    	if(idList.isEmpty()) {
    		messageLabel.setText("Please enter a valid group");
            messageLabel.setTextFill(Color.RED);
            return;
    	}
    	mainApp.showArticlesListPage(prev, group,  -3, idList, true);
    }
    
    private void searchByPhrase() throws JSONException, SQLException {
    	if(phraseField.getText().isEmpty()) {
    		messageLabel.setText("Please enter a valid phrase");
            messageLabel.setTextFill(Color.RED);
            return;
    	}
    	String phrase = phraseField.getText();
        String username = mainApp.databaseHelper.findLoggedInUser();
    	List<Long> idList = mainApp.databaseHelper.searchArticlesByKeywordWithAccess(username, phrase);
    	if(idList.isEmpty()) {
    		messageLabel.setText("Please enter a valid phrase");
            messageLabel.setTextFill(Color.RED);
            return;
    	}
    	mainApp.showArticlesListPage(prev, phrase,  -4, idList, true);
    }
    
    private void searchByID() {
    	String username = mainApp.databaseHelper.findLoggedInUser();
    	if (idField.getText().isEmpty()) {
    		messageLabel.setText("Warning: ID needed to view");
    		messageLabel.setTextFill(Color.RED);
            return;
        }
        try {
            long id = Long.parseLong(idField.getText());
            if(mainApp.databaseHelper.canUserViewArticle(prev, username, id) && mainApp.databaseHelper.isUserInAccessGroups(username, id)) {
                mainApp.showArticlesListPage(prev, "", id, null, true);
            }
            else {
            	messageLabel.setText("Warning: " + prev + " cannot view Article: " + id);
            	messageLabel.setTextFill(Color.RED);
                return;
            }
        } catch (NumberFormatException e) {
        	messageLabel.setText("Invalid ID format.");
        	messageLabel.setTextFill(Color.RED);
        } catch (SQLException e) {
			e.printStackTrace();
		}
    }
}
