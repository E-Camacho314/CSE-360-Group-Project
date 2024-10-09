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
    private CSE360HelpSystem mainApp;
    private Label titleLabel = new Label("List of Users");
    private Button backButton = new Button("Back");

    public ListPage(CSE360HelpSystem mainApp) {
        this.mainApp = mainApp;
        BorderPane mainPane = new BorderPane();

        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setFont(Font.font(16));

        // Set up the table columns

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, Boolean> adminCol = new TableColumn<>("Admin");
        adminCol.setCellValueFactory(new PropertyValueFactory<>("isAdmin"));

        TableColumn<User, Boolean> instructorCol = new TableColumn<>("Instructor");
        instructorCol.setCellValueFactory(new PropertyValueFactory<>("isInstructor"));

        TableColumn<User, Boolean> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(new PropertyValueFactory<>("isStudent"));

        TableColumn<User, String> firstnameCol = new TableColumn<>("First Name");
        firstnameCol.setCellValueFactory(new PropertyValueFactory<>("firstname"));

        TableColumn<User, String> middlenameCol = new TableColumn<>("Middle Name");
        middlenameCol.setCellValueFactory(new PropertyValueFactory<>("middlename"));

        TableColumn<User, String> preferrednameCol = new TableColumn<>("Preferred Name");
        preferrednameCol.setCellValueFactory(new PropertyValueFactory<>("preferredname"));

        TableColumn<User, String> lastnameCol = new TableColumn<>("Last Name");
        lastnameCol.setCellValueFactory(new PropertyValueFactory<>("lastname"));

        // Add columns to the table, excluding email and flag status
        table.getColumns().addAll(usernameCol, adminCol, instructorCol, studentCol, firstnameCol, middlenameCol, lastnameCol, preferrednameCol);

        // Load data from the database
        loadUserData();

        // Set up layout
        table.setItems(data);
        table.setPrefWidth(600);

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
                // Retrieve data but do not add email or flag status to the data model
                String username = rs.getString("username");
                String firstname = rs.getString("firstname");
                String middlename = rs.getString("middlename");
                String lastname = rs.getString("lastname");
                String preferredName = rs.getString("preferred");
                boolean adminStatus = rs.getInt("admin") == 1;
                boolean instructorStatus = rs.getInt("instructor") == 1;
                boolean studentStatus = rs.getInt("student") == 1;

                // Create a User object without email and flag status
                data.add(new User(username, null, firstname, middlename, lastname, preferredName, adminStatus, instructorStatus, studentStatus, false)); // Assuming flagStatus is false as it's not displayed
            }

            databaseHelper.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
