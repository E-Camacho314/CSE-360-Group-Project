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

        } catch (SQLException e) {
            System.err.println("SQL error while emptying the database: " + e.getMessage());
        } finally {
            System.out.println("Database emptied successfully.");
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
                + "student INTEGER, "
                + "flag INTEGER"
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

    public void register(String username, String password, int admin, int instructor, int student) throws SQLException {
        // Adjusted SQL insert statement to include all columns
        String insertUser = "INSERT INTO cse360users (email, username, password, admin, instructor, student, firstname, middlename, lastname, preferred) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        System.out.println("User registering");
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
            pstmt.setString(1, "");           // Email
            pstmt.setString(2, username);           // Username
            pstmt.setString(3, password);        // Password
            pstmt.setInt(4, admin);              // Admin status
            pstmt.setInt(5, instructor);         // Instructor status
            pstmt.setInt(6, student);            // Student status

            // Set additional fields to empty strings or null
            pstmt.setString(7, "");              // First name
            pstmt.setString(8, "");              // Middle name
            pstmt.setString(9, "");              // Last name
            pstmt.setString(10, "");             // Preferred name

            pstmt.executeUpdate();
            System.out.println("User registered successfully.");
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
    }
    
    public boolean registerWithEmailAndNames(String username, String email, String firstname, String middlename, String lastname, String preferredName) throws SQLException {
        // Update existing user based on username
        String updateUser = "UPDATE cse360users SET email = ?, firstname = ?, middlename = ?, lastname = ?, preferred = ? WHERE username = ?";
        System.out.println("Modifying user with username and new details");
        
        try (PreparedStatement pstmt = connection.prepareStatement(updateUser)) {
            pstmt.setString(1, email);            // Update email
            pstmt.setString(2, firstname);        // Update first name
            pstmt.setString(3, middlename);       // Update middle name
            pstmt.setString(4, lastname);         // Update last name
            pstmt.setString(5, preferredName);    // Update preferred name
            pstmt.setString(6, username);         // Username to identify the user

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User modified successfully with username and new details.");
                return true;  // Modification successful
            } else {
                System.out.println("User modification failed. User might not exist.");
                return false; // Modification failed
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
            return false; // Modification failed due to an exception
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
                    String firstname = rs.getString("firstname");
                    String middlename = rs.getString("middlename");
                    String lastname = rs.getString("lastname");
                    String preferredName = rs.getString("preferred");
                    boolean isAdmin = rs.getInt("admin") == 1;
                    boolean isInstructor = rs.getInt("instructor") == 1;
                    boolean isStudent = rs.getInt("student") == 1;
                    boolean isFlagged = rs.getInt("flag") == 1;
                    
                    return new User(username, email, firstname, middlename, lastname, preferredName, isAdmin, isInstructor, isStudent, isFlagged);
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

    public boolean doesUserExist(String username) {
        String query = "SELECT COUNT(*) FROM cse360users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
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
        String query = "SELECT email, username, firstname, middlename, lastname, preferred, admin, instructor, student, flag FROM cse360users";
        PreparedStatement pstmt = connection.prepareStatement(query);
        return pstmt.executeQuery();
    }


    public void displayUsers() throws SQLException {
        String sql = "SELECT * FROM cse360users"; 
        try (Statement stmt = connection.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { 
                // Retrieve by column name 
                int id = rs.getInt("id"); 
                String email = rs.getString("email"); 
                String password = rs.getString("password"); // Consider not printing this
                String admin = rs.getString("admin");
                String instructor = rs.getString("instructor");
                String student = rs.getString("student");
                String firstname = rs.getString("firstname");
                String middlename = rs.getString("middlename");
                String lastname = rs.getString("lastname");
                String preferredName = rs.getString("preferred");
                boolean isAdmin = admin != null && admin.equals("1");
                boolean isInstructor = instructor != null && instructor.equals("1");
                boolean isStudent = student != null && student.equals("1");

                // Display values 
                System.out.print("ID: " + id); 
                System.out.print(", Email: " + email); 
                // Avoid printing passwords in production
                System.out.print(", Password: " + password); // Consider removing this line
                System.out.print(", First Name: " + firstname);
                System.out.print(", Middle Name: " + middlename);
                System.out.print(", Last Name: " + lastname);
                System.out.print(", Preferred Name: " + preferredName);
                System.out.print(", Admin: " + isAdmin); 
                System.out.print(", Instructor: " + isInstructor); 
                System.out.println(", Student: " + isStudent); 
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
                    String firstname = rs.getString("firstname");
                    String middlename = rs.getString("middlename");
                    String lastname = rs.getString("lastname");
                    String preferredName = rs.getString("preferred");
                    boolean isAdmin = rs.getInt("admin") == 1;
                    boolean isInstructor = rs.getInt("instructor") == 1;
                    boolean isStudent = rs.getInt("student") == 1;
                    boolean isFlagged = rs.getInt("flag") == 1;
                    
                    return new User(username, email, firstname, middlename, lastname, preferredName, isAdmin, isInstructor, isStudent, isFlagged);
                }
            }
        }
        return null;
    }


    public void closeConnection() {
        try { 
            if (statement != null) statement.close(); 
            System.out.println("Connection Closed");
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
