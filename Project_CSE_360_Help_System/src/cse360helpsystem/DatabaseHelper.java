package cse360helpsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {  
    static final String DB_URL = "jdbc:sqlite:helpsystem.db";  

    private static Connection connection = null;
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
                + "firstname TEXT, "
                + "middlename TEXT, "
                + "lastname TEXT, "
                + "preferred TEXT, "
                + "username TEXT, "
                + "email TEXT UNIQUE, "
                + "password TEXT, "
                + "admin INTEGER, "
                + "instructor INTEGER, "
                + "student INTEGER"
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

    public void register(String email, String password, int admin, int instructor, int student) throws SQLException {
        String insertUser = "INSERT INTO cse360users (email, password, admin, instructor, student) VALUES (?, ?, ?, ?, ?)";
        System.out.println("User registering");
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setInt(3, admin);
            pstmt.setInt(4, instructor);
            pstmt.setInt(5, student);
            pstmt.executeUpdate();
            System.out.println("User registered successfully.");
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
    }

    public User login(String username, String password) throws SQLException {
        String query = "SELECT * FROM cse360users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String email = rs.getString("email");
                    boolean isAdmin = rs.getInt("admin") == 1;
                    boolean isInstructor = rs.getInt("instructor") == 1;
                    boolean isStudent = rs.getInt("student") == 1;
                    return new User(username, email, isAdmin, isInstructor, isStudent);
                }
            }
        }
        return null;
    }
    
    public void changeUserRoles(String email, int admin, int instructor, int student) throws SQLException {
        // Prepare the SQL statement to update all roles at once
        String updateSQL = "UPDATE cse360users SET admin = ?, instructor = ?, student = ? WHERE email = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            // Set the roles based on the integer values: 1 means enabled, 0 means disabled
            pstmt.setInt(1, admin == 1 ? 1 : 0);
            pstmt.setInt(2, instructor == 1 ? 1 : 0);
            pstmt.setInt(3, student == 1 ? 1 : 0);
            pstmt.setString(4, email);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("User roles updated successfully.");
            } else {
                System.out.println("No user found with the email '" + email + "'.");
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
    }
    
    public void deleteUser(String email) throws SQLException {
        String deleteUserSQL = "DELETE FROM cse360users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteUserSQL)) {
            pstmt.setString(1, email);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("User with email '" + email + "' deleted successfully.");
            } else {
                System.out.println("No user found with the email '" + email + "'.");
            }
        } catch (SQLException e) {
            System.err.println("SQL error while deleting user: " + e.getMessage());
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
    
    public ResultSet getAllUsers() throws SQLException {
        String query = "SELECT email, admin, instructor, student FROM cse360users";
        return statement.executeQuery(query);
    }


    public void displayUsers() throws SQLException {
        String sql = "SELECT * FROM cse360users"; 
        try (Statement stmt = connection.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { 
                // Retrieve by column name 
                int id = rs.getInt("id"); 
                String email = rs.getString("email"); 
                String password = rs.getString("password"); 
                String admin = rs.getString("admin");
                String instructor = rs.getString("instructor");
                String student = rs.getString("student");

                // Display values 
                System.out.print("ID: " + id); 
                System.out.print(", Email: " + email); 
                // Avoid printing passwords in production
                System.out.print(", Password: " + password); // Consider removing this line
                System.out.print(", Admin: " + admin); 
                System.out.print(", Instructor: " + instructor); 
                System.out.println(", Student: " + student); 
            } 
        } catch (SQLException e) {
            System.err.println("SQL error while displaying users: " + e.getMessage());
        }
    }
    
    public static boolean updateUserSetup(String username, String firstName, String middleName, String lastName, String preferredName) throws SQLException {
        String update = "UPDATE cse360users SET firstname = ?, middlename = ?, lastname = ?, preferred = ? WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(update)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, middleName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, preferredName.isEmpty() ? null : preferredName);
            pstmt.setString(5, username);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // Check if account setup is complete
    public boolean isSetupComplete(String username) throws SQLException {
        String query = "SELECT firstname, lastname FROM cse360users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String firstName = rs.getString("firstname");
                    String lastName = rs.getString("lastname");
                    return (firstName != null && !firstName.isEmpty()) &&
                           (lastName != null && !lastName.isEmpty());
                }
            }
        }
        return false;
    }
    
    public User getUserByUsername(String username) throws SQLException {
        String query = "SELECT * FROM cse360users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String email = rs.getString("email");
                    boolean isAdmin = rs.getInt("admin") == 1;
                    boolean isInstructor = rs.getInt("instructor") == 1;
                    boolean isStudent = rs.getInt("student") == 1;
                    return new User(username, email, isAdmin, isInstructor, isStudent);
                }
            }
        }
        return null;
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
