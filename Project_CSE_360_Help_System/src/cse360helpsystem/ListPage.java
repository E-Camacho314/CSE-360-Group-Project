package cse360helpsystem;

import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ListPage extends HBox {
    private TableView<User> table = new TableView<>();
    private ObservableList<User> data = FXCollections.observableArrayList();
    private CSE360HelpSystem mainApp = new CSE360HelpSystem();
    private Label titleLabel = new Label("List of Users");
    private Button backButton = new Button("Back");
    public ListPage() {
        BorderPane mainPane = new BorderPane();
        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setFont(Font.font(16));
        // Set up the table columns
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<User, String> adminCol = new TableColumn<>("Admin");
        adminCol.setCellValueFactory(new PropertyValueFactory<>("admin"));
        TableColumn<User, String> instructorCol = new TableColumn<>("Instructor");
        instructorCol.setCellValueFactory(new PropertyValueFactory<>("instructor"));
        TableColumn<User, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(new PropertyValueFactory<>("student"));
        table.getColumns().addAll(emailCol, adminCol, instructorCol, studentCol);
        // Load data from the database
        loadUserData();
        // Set up layout
        table.setItems(data);
        table.setPrefWidth(400);
        HBox buttonBox = new HBox(10, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        VBox vBox = new VBox(10, titleLabel, table, buttonBox);
        vBox.setPadding(new Insets(10));
        vBox.setAlignment(Pos.CENTER);
        mainPane.setCenter(vBox);
        this.getChildren().add(mainPane);
        this.setAlignment(Pos.CENTER);
        // Back button action
        backButton.setOnAction(e -> mainApp.showAdminPage());
    }
    // Load users from the database and add them to the ObservableList
    private void loadUserData() {
        try {
            DatabaseHelper databaseHelper = new DatabaseHelper();
            databaseHelper.connectToDatabase();
            ResultSet rs = databaseHelper.getAllUsers();
            while (rs.next()) {
                String email = rs.getString("email");
                int admin = rs.getInt("admin");
                int instructor = rs.getInt("instructor");
                int student = rs.getInt("student");
                // Convert int values to "Yes" or "No" for displaying roles
                boolean adminStatus = (admin == 1) ? true : false;
                boolean instructorStatus = (instructor == 1) ? true : false;
                boolean studentStatus = (student == 1) ? true : false;
                data.add(new User("", email, adminStatus, instructorStatus, studentStatus));
            }
            databaseHelper.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}