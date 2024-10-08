package cse360helpsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {  
    static final String DB_URL = "jdbc:sqlite:helpsystem.db";  

    private Connection connection = null;
    private Statement statement = null; 

    public void connectToDatabase() throws SQLException {
        System.out.println("Connecting to database...");
        connection = DriverManager.getConnection(DB_URL);
        if (connection != null) {
            System.out.println("Database created or opened successfully.");
            statement = connection.createStatement(); 
            createTables();  // Create the necessary tables if they don't exist
        }
    }
    
    public void emptyDatabase() {
        try {
            connectToDatabase(); // Establish connection
            String dropUserTable = "DROP TABLE IF EXISTS cse360users;";
            statement.executeUpdate(dropUserTable);
            System.out.println("Database emptied successfully.");
        } catch (SQLException e) {
            System.err.println("SQL error while emptying the database: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void createTables() throws SQLException {
        String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
               // + "firstname TEXT, "
                //+ "middlename TEXT, "
               // + "lastname TEXT, "
               // + "preferred TEXT, "
                //+ "username TEXT, "
                + "email TEXT UNIQUE, "
                + "password TEXT, "
                //+ "role TEXT" // Add role column
                + "admin TEXT, "
                + "instructor TEXT, "
                + "student TEXT"
                + ");";
        statement.execute(userTable);
    }

    // Check if the database is empty
    public boolean isDatabaseEmpty() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM cse360users";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            System.out.println("Not Empty");
            return resultSet.getInt("count") == 0;
        }
        System.out.println("Empty");
        return true;
    }

    public void register(String email, String password, String admin, String instructor, String student) throws SQLException {
        String insertUser = "INSERT INTO cse360users (email, password, admin, instructor, student) VALUES (?, ?, ?, ?, ?)";
        System.out.println("User registering");
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, admin);
            pstmt.setString(4, instructor);
            pstmt.setString(5, student);
            pstmt.executeUpdate();
            System.out.println("User registered successfully.");
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
    }

    public boolean login(String email, String password, String role) throws SQLException {
        String query = "SELECT * FROM cse360users WHERE email = ? AND password = ? AND role = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean doesUserExist(String email) {
        String query = "SELECT COUNT(*) FROM cse360users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // If an error occurs, assume user doesn't exist
    }

    public void displayUsers() throws SQLException {
        String sql = "SELECT * FROM cse360users"; 
        try (Statement stmt = connection.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) { 
                // Retrieve by column name 
                int id  = rs.getInt("id"); 
                String email = rs.getString("email"); 
                String password = rs.getString("password"); 
                String role = rs.getString("role");  

                // Display values 
                System.out.print("ID: " + id); 
                System.out.print(", Email: " + email); 
                System.out.print(", Password: " + password); 
                System.out.println(", Role: " + role); 
            } 
        }
    }

    public void closeConnection() {
        try { 
            if (statement != null) statement.close(); 
        } catch(SQLException se2) { 
            se2.printStackTrace();
        } 
        try { 
            if (connection != null) connection.close(); 
        } catch(SQLException se){ 
            se.printStackTrace(); 
        } 
    }
}
