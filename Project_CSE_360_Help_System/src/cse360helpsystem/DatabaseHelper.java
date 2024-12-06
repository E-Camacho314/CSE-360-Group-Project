package cse360helpsystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;

import Encryption.EncryptionHelper;
import Encryption.EncryptionUtils;

/**
* <p>DatabaseHelper Class</p>
* 
* <p> Description: This class manages all interactions with the SQLite database used by the CSE360HelpSystem.
* It handles connecting to the database, creating necessary tables, and performing CRUD operations
* related to users, invite codes, and password resets.</p>
* 
* <p>Authors: Erik Camacho, Thienban Nguyen, Sarvesh Shanmugam, Ivan Mancillas, Tanis Peterson</p>
*/

public class DatabaseHelper {  
	// Database URL pointing to the SQLite database file
    static final String DB_URL = "jdbc:sqlite:helpsystem.db";  
    
    // Singleton instance of the database connection
    private static Connection connection = null;
    private Statement statement = null; 
    
	private EncryptionHelper encryptionHelper;

	// new EncryptionHelper created to help encrypt passwords and articles
	public DatabaseHelper() throws Exception {
		encryptionHelper = new EncryptionHelper();
	}
	
	/**
     * Creates the necessary tables for the database
     * cse360users: Stores user information.
     * invite_codes: Manages invite codes for user registrations.
     * password_resets: Handles password reset requests.
     * @throws SQLException
     */
    private void createTables() throws SQLException {
    	
    	// table for all user accounts
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
                + "flag INTEGER, "
                + "isloggedin INTEGER"
                + ");";
        statement.execute(userTable);
        
        // invite code table
        String inviteTable = "CREATE TABLE IF NOT EXISTS invite_codes ("
                + "code TEXT PRIMARY KEY, "
        		+ "username TEXT, "
                + "is_admin INTEGER, "
                + "is_instructor INTEGER, "
                + "is_student INTEGER, "
                + "is_used INTEGER DEFAULT 0"
                + ");";
        statement.execute(inviteTable);
        
        // password_resets table
        String resetTable = "CREATE TABLE IF NOT EXISTS password_resets ("
                + "username TEXT PRIMARY KEY, "
                + "one_time_password TEXT, "
                + "is_used INTEGER DEFAULT 0, "
                + "FOREIGN KEY(username) REFERENCES cse360users(username) ON DELETE CASCADE"
                + ");";
        statement.execute(resetTable);
        
		// table for all the articles
        String articleTable = "CREATE TABLE IF NOT EXISTS articles ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "  // Ensures id is a unique long integer
                + "title TEXT NOT NULL, "
                + "headers TEXT, "  // Added commas between columns
                + "groups JSON, "
                + "access TEXT, "
                + "beginner INTEGER, "
                + "intermediate INTEGER, "
                + "advanced INTEGER, "
                + "expert INTEGER, "
                + "abstract TEXT, "
                + "keywords TEXT, "
                + "body TEXT NOT NULL, "
                + "ref_list TEXT, "
                + "specialaccessgroups JSON"
                + ");";
        statement.execute(articleTable);
        
        // table for special access groups
        String accessTable = "CREATE TABLE IF NOT EXISTS specialaccess ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "  // Ensures id is a unique long integer
                + "groupname TEXT, "
                + "instructors_with_view_access JSON, "       // JSON array for instructors with view access
                + "instructors_with_admin_access JSON, "   // JSON array for instructors with admin access
                + "article_ids JSON, "                        // JSON array for article IDs
                + "students_with_view_access JSON "           // JSON array for students with view access
                + ");";
        statement.execute(accessTable);
        
        // table for requests from students
        String requestTable = "CREATE TABLE IF NOT EXISTS requests("
        		+ "id INTEGER PRIMARY KEY AUTOINCREMENT, "
        		+ "studentid INTEGER, "
        		+ "username TEXT, "
        		+ "firstname TEXT, "
        		+ "request TEXT NOT NULL"
        		+ ");";
        statement.execute(requestTable);
    }

    /**
     * Establishes a connection to the SQLite database.
     * If the database does not exist, it will be created automatically.
     * After establishing the connection, it initializes the necessary tables.
     * 
     * @throws SQLException if a database access error occurs
     */
    public void connectToDatabase() throws SQLException {
        System.out.println("Connecting to database...");
        connection = DriverManager.getConnection(DB_URL);
        if (connection != null) {
            System.out.println("Database created or opened successfully.");
            statement = connection.createStatement(); 
            createTables();  // Create the necessary tables if they don't exist
        }
    }
    
    // Method to reset all existing tables in order to test
    public void emptyDatabase() {
        try {
            connectToDatabase(); // Establish connection
            String dropUserTable = "DROP TABLE IF EXISTS cse360users;";
            statement.executeUpdate(dropUserTable);
            String dropinviteTable = "DROP TABLE IF EXISTS invite_codes;";
            statement.executeUpdate(dropinviteTable);
            String dropotherTable = "DROP TABLE IF EXISTS password_resets;";
            statement.executeUpdate(dropotherTable);
            String droparticlesTable = "DROP TABLE IF EXISTS articles;";
            statement.executeUpdate(droparticlesTable);
            String dropaccessTable = "DROP TABLE IF EXISTS specialaccess;";
            statement.executeUpdate(dropaccessTable);
            String droprequestTable = "DROP TABLE IF EXISTS requests;";
            statement.executeUpdate(droprequestTable);

        } catch (SQLException e) {
            System.err.println("SQL error while emptying the database: " + e.getMessage());
        } finally {
            System.out.println("Database emptied successfully.");
        }
    }
    
    // Method to reset all existing tables in order to test
    public void emptySpecial() {
        try {
            connectToDatabase(); // Establish connection
            String dropaccessTable = "DROP TABLE IF EXISTS specialaccess;";
            statement.executeUpdate(dropaccessTable);
            String droparticlesTable = "DROP TABLE IF EXISTS articles;";
            statement.executeUpdate(droparticlesTable);

        } catch (SQLException e) {
            System.err.println("SQL error while emptying the database: " + e.getMessage());
        } finally {
            System.out.println("Database emptied successfully.");
        }
    }

    // Check if the database is empty in order to prompt an initial account setup for the first user
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
    
    // Logs in a user by setting the isloggedin column to 1.
    public boolean loginUser(String username, String password) throws SQLException {
        String query = "SELECT * FROM cse360users WHERE username = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Check if the user is already logged in
                if (resultSet.getInt("isloggedin") == 1) {
                    System.out.println("User is already logged in.");
                    return false;
                }

                // Update the user's isloggedin status to 1
                String updateQuery = "UPDATE cse360users SET isloggedin = 1 WHERE username = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setString(1, username);
                    updateStatement.executeUpdate();
                }

                System.out.println("Login successful for user: " + username);
                return true;
            } else {
                System.out.println("Invalid username or password.");
                return false;
            }
        }
    }

    // Logs out a user by setting the isloggedin column to 0.
    public void logoutUser() {
        String findLoggedInUser = "SELECT id FROM cse360users WHERE isloggedin = 1;";
        String updateLogout = "UPDATE cse360users SET isloggedin = 0 WHERE id = ?;";
        
        try (PreparedStatement findUserStmt = connection.prepareStatement(findLoggedInUser);
             PreparedStatement updateLogoutStmt = connection.prepareStatement(updateLogout)) {
            
            ResultSet rs = findUserStmt.executeQuery();
            
            if (rs.next()) {  // If a logged-in user is found
                int userId = rs.getInt("id");
                
                // Set the isloggedin flag to 0 for the current user
                updateLogoutStmt.setInt(1, userId);
                updateLogoutStmt.executeUpdate();
                System.out.println("User with ID " + userId + " has been logged out.");
            } else {
                System.out.println("No user is currently logged in.");
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Logs out all users by setting the isloggedin column to 0 for all users.
    public void logoutAllUsers() throws SQLException {
        String query = "UPDATE cse360users SET isloggedin = 0";
        try (Statement statement = connection.createStatement()) {
            int rowsAffected = statement.executeUpdate(query);
            System.out.println("Logged out all users. Total affected rows: " + rowsAffected);
        }
    }
    
    // Finds and returns the username of the user who is currently logged in
    public String findLoggedInUser() {
        String findLoggedInUserQuery = "SELECT username FROM cse360users WHERE isloggedin = 1;";
        
        try (PreparedStatement findUserStmt = connection.prepareStatement(findLoggedInUserQuery)) {
            
            ResultSet rs = findUserStmt.executeQuery();
            
            if (rs.next()) {  // If a logged-in user is found
                return rs.getString("username");
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        // If no user is logged in
        return null;
    }
    
    // checks if there is more than one admin in existence, returns true if so
    public boolean moreThanOneAdmin() {
        String countAdminsQuery = "SELECT COUNT(*) AS adminCount FROM cse360users WHERE admin = 1;";
        
        try (PreparedStatement countAdminsStmt = connection.prepareStatement(countAdminsQuery)) {
            
            ResultSet rs = countAdminsStmt.executeQuery();
            
            if (rs.next()) {  // Check if we got a result
                int adminCount = rs.getInt("adminCount");
                return adminCount > 1;  // Return true if more than one admin exists
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        // Return false if there's one or zero admins
        return false;
    }
    
    // Method to update database with a different password in the case of resetting
    public boolean changePassword(String username, String newPassword) throws SQLException {
        String updatePassword = "UPDATE cse360users SET password = ? WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(updatePassword)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Password updated successfully for user: " + username);
                return true;
            } else {
                System.out.println("Failed to update password. User not found.");
            }
        } catch (SQLException e) {
            System.err.println("SQL error while updating password: " + e.getMessage());
        }
        
        return false; // Return false if the update failed
    }

    // Method to verify password entered by user upon login attempt
    public boolean checkPassword(String username, String password) throws SQLException {
        String query = "SELECT password FROM cse360users WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password);
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            System.err.println("SQL error while checking password: " + e.getMessage());
        }
        
        return false; // Return false if user not found or password does not match
    }
    
    // Method to update the database when a user changes their email and password
    public boolean setEmailAndPassword(String username, String email, String password) throws SQLException {
        String updateQuery = "UPDATE cse360users SET email = ?, password = ? WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, username);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Email and password updated successfully for user: " + username);
                return true;
            } else {
                System.out.println("User not found or no changes made for user: " + username);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("SQL error while updating email and password: " + e.getMessage());
            return false;
        }
    }

    /** Method to add a new user to the database given a username, password, and what roles they have.
     * 
     * @param username		the username of the new user
     * @param password		the password of the new user
     * @param admin			1 if the user is an admin, 0 otherwise
     * @param instructor	1 if the user is a instructor, 0 otherwise
     * @param student		1 if the user is a student, 0 otherwise
     * @return true if registration is successful, false otherwise
     * @throws SQLException	SQLException if a database access error occurs
     */
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
    
    /**
     * Updates an existing user's email and name details based on their username.
     * 
     * @param username     the username of the user to be updated
     * @param email        the new email address
     * @param firstname    the new first name
     * @param middlename   the new middle name
     * @param lastname     the new last name
     * @param preferredName the new preferred name
     * @return true if the update is successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
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

    /**
     * Authenticates a user by checking if the provided username and password match a record in the database.
     * 
     * @param username the username entered by the user
     * @param password the password entered by the user
     * @return a User object if authentication is successful, null otherwise
     * @throws SQLException if a database access error occurs
     */
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

    /**
     * Changes the roles of a user based on their email address.
     * Updates the admin, instructor, and student status flags in the database.
     * 
     * @param email       the email of the user whose roles are to be changed
     * @param admin       1 to grant admin privileges, 0 to revoke
     * @param instructor  1 to grant instructor privileges, 0 to revoke
     * @param student     1 to grant student privileges, 0 to revoke
     * @throws SQLException if a database access error occurs
     */
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
    
    /**
     * Deletes a user from the database based on their email address.
     * 
     * @param email the email of the user to be deleted
     * @throws SQLException if a database access error occurs
     */
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


    /**
     * Checks if a user exists in the database based on their username.
     * 
     * @param username the username to check
     * @return true if the user exists, false otherwise
     */
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
    
    /**
     * Retrieves all users from the database.
     * Useful for admin functionalities like listing all users.
     * 
     * @return a ResultSet containing all user records
     * @throws SQLException if a database access error occurs
     */
    public ResultSet getAllUsers() throws SQLException {
        String query = "SELECT email, username, firstname, middlename, lastname, preferred, admin, instructor, student, flag FROM cse360users";
        PreparedStatement pstmt = connection.prepareStatement(query);
        return pstmt.executeQuery();
    }

    /**
     * Displays all users in the console.
     * 
     * @throws SQLException if a database access error occurs
     */
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
                System.out.print(", Password: " + password);
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

    /**
     * Updates the user's setup information, including first name, middle name, last name, and preferred name.
     * 
     * @param username     the username of the user to update
     * @param firstName    the new first name
     * @param middleName   the new middle name
     * @param lastName     the new last name
     * @param preferredName the new preferred name
     * @return true if the update was successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
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
    
    /**
     * Checks whether the account setup is complete for a given user.
     * An account setup is considered complete if both first name and last name are provided.
     * 
     * @param username the username of the user to check
     * @return true if setup is complete, false otherwise
     * @throws SQLException if a database access error occurs
     */
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
    
    /**
     * Retrieves a User object based on the provided username.
     * 
     * @param username the username of the user to retrieve
     * @return a User object with the user's details, or null if not found
     * @throws SQLException if a database access error occurs
     */
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
    
    /**
     * Stores an invite code associated with a user and their roles.
     * 
     * @param username the username associated with the invite code
     * @param code the unique invite code
     * @param isAdmin true if the invite grants admin privileges
     * @param isInstructor true if the invite grants instructor privileges
     * @param isStudent true if the invite grants student privileges
     * @return true if the invite code is stored successfully, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean storeInviteCode(String username, String code, boolean isAdmin, boolean isInstructor, boolean isStudent) throws SQLException {
        String insertInvite = "INSERT INTO invite_codes (code, username, is_admin, is_instructor, is_student) VALUES (?, ?, ?, ?, ?)";     
        try (PreparedStatement pstmt = connection.prepareStatement(insertInvite)) {
            pstmt.setString(1, code);
            pstmt.setString(2, username);
            pstmt.setInt(3, isAdmin ? 1 : 0);
            pstmt.setInt(4, isInstructor ? 1 : 0);
            pstmt.setInt(5, isStudent ? 1 : 0);
            pstmt.executeUpdate();
            System.out.println("Invite code stored successfully: " + code);
            return true;
        } catch (SQLException e) {
            System.err.println("SQL error while storing invite code: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves the username associated with a given invite code.
     * 
     * @param inviteCode the invite code to search for
     * @return the username associated with the invite code, or null if not found
     * @throws SQLException if a database access error occurs
     */
    public String getUsernameByInviteCode(String inviteCode) throws SQLException {
        String query = "SELECT username FROM invite_codes WHERE code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, inviteCode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            System.err.println("SQL error while retrieving username by invite code: " + e.getMessage());
        }
        return null;
    }

   /**
    * 
    * @param username the username associated with the invite code
    * @return a string displaying the invite code, or null
    * @throws SQLException if a database access error occurs
    */
    public String getInviteCode(String username) throws SQLException {
        String query = "SELECT invite_code FROM cse360users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("invite_code");
            }
        } catch (SQLException e) {
            System.err.println("SQL error while retrieving invite code: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Assigns roles to a user based on a provided invite code and registers the user.
     * After assigning roles, it marks the invite code as used to prevent reuse.
     * 
     * @param username   the username of the user to assign roles to
     * @param inviteCode the invite code used for role assignment
     * @return true if roles are assigned and user is registered successfully, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean assignRolesBasedOnInviteCode(String username, String inviteCode) throws SQLException {
        // Validate the invite code
        if (!isInviteCodeValid(inviteCode)) {
            System.out.println("Invalid or expired invite code.");
            return false;
        }

        // Retrieve roles associated with the invite code
        String query = "SELECT is_admin, is_instructor, is_student FROM invite_codes WHERE code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, inviteCode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                boolean isAdmin = rs.getInt("is_admin") == 1;
                boolean isInstructor = rs.getInt("is_instructor") == 1;
                boolean isStudent = rs.getInt("is_student") == 1;

                // Insert the new user with assigned roles
                String insertUser = "INSERT INTO cse360users (username, email, password, admin, instructor, student) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertPstmt = connection.prepareStatement(insertUser)) {
                    insertPstmt.setString(1, username);
                    insertPstmt.setString(2, ""); // Email to be set during account setup
                    insertPstmt.setString(3, ""); // Password to be set during account setup
                    insertPstmt.setInt(4, isAdmin ? 1 : 0);
                    insertPstmt.setInt(5, isInstructor ? 1 : 0);
                    insertPstmt.setInt(6, isStudent ? 1 : 0);
                    insertPstmt.executeUpdate();
                }

                // Mark the invite code as used
                markInviteCodeAsUsed(inviteCode);

                System.out.println("Roles assigned and user registered successfully.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("SQL error while assigning roles: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Validates whether an invite code is valid and unused.
     * 
     * @param inviteCode the invite code to validate
     * @return true if the invite code is valid and not used, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean isInviteCodeValid(String inviteCode) throws SQLException {
        String query = "SELECT is_used FROM invite_codes WHERE code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, inviteCode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                boolean isUsed = rs.getInt("is_used") == 1;
                if (!isUsed) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL error while validating invite code: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Marks an invite code as used to prevent its reuse.
     * 
     * @param inviteCode the invite code to mark as used
     * @throws SQLException if a database access error occurs
     */
    public void markInviteCodeAsUsed(String inviteCode) throws SQLException {
        String update = "UPDATE invite_codes SET is_used = 1 WHERE code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(update)) {
            pstmt.setString(1, inviteCode);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Invite code marked as used.");
            } else {
                System.out.println("Failed to mark invite code as used. Code might not exist.");
            }
        } catch (SQLException e) {
            System.err.println("SQL error while marking invite code as used: " + e.getMessage());
        }
    }
    
    /**
     * Resets a user's password to a new value.
     * 
     * @param username    the username of the user whose password is to be reset
     * @param newPassword the new password to set
     * @return true if the password was reset successfully, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean resetPassword(String username, String newPassword, int flag) throws SQLException {
        String updatePassword = "UPDATE cse360users SET password = ?, flag= ? WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updatePassword)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2,  flag);
            pstmt.setString(3, username);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Password reset successfully for user: " + username);
                System.out.println("New Password: " + newPassword);
                return true;
            } else {
                System.out.println("Failed to reset password. User not found.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("SQL error while resetting password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Stores a password reset request by associating a one-time password (OTP) with a username.
     * If a reset request already exists for the user, it updates the OTP.
     * 
     * @param username         the username of the user requesting a password reset
     * @param oneTimePassword  the generated one-time password for resetting
     * @return true if the OTP was stored successfully, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean storePasswordReset(String username, String oneTimePassword) throws SQLException {
        String insertReset = "INSERT INTO password_resets (username, one_time_password) VALUES (?, ?)"
                + "ON CONFLICT(username) DO UPDATE SET one_time_password = excluded.one_time_password";       
        try (PreparedStatement pstmt = connection.prepareStatement(insertReset)) {
            pstmt.setString(1, username);
            pstmt.setString(2, oneTimePassword);
            pstmt.executeUpdate();
            System.out.println("Password reset stored successfully for user: " + username);
            return true;
        } catch (SQLException e) {
            System.err.println("SQL error while storing password reset: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validates whether a provided one-time password (OTP) is valid and unused for a given user.
     * 
     * @param username         the username of the user attempting to reset their password
     * @param oneTimePassword  the OTP provided by the user
     * @return true if the OTP is valid and unused, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean isOTPValid(String username, String oneTimePassword) throws SQLException {
        String query = "SELECT is_used FROM password_resets WHERE username = ? AND one_time_password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, oneTimePassword);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                boolean isUsed = rs.getInt("is_used") == 1;
                if (!isUsed) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL error while validating OTP: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Marks a one-time password (OTP) as used for a given user to prevent its reuse.
     * 
     * @param username the username of the user whose OTP is to be marked as used
     * @throws SQLException if a database access error occurs
     */
    public void markOTPAsUsed(String username) throws SQLException {
        String update = "UPDATE password_resets SET is_used = 1 WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(update)) {
            pstmt.setString(1, username);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("OTP marked as used for user: " + username);
            } else {
                System.out.println("Failed to mark OTP as used. User might not exist.");
            }
        } catch (SQLException e) {
            System.err.println("SQL error while marking OTP as used: " + e.getMessage());
        }
    }
    
    // Check if articles is empty
 	public boolean isArticlesEmpty() throws SQLException {
 		// creates new articles table if the table is already empty
        String createArticlesTableSQL = "CREATE TABLE IF NOT EXISTS articles ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "  // Ensures id is a unique long integer
                + "title TEXT NOT NULL, "
                + "headers TEXT, "  // Added commas between columns
                + "groups JSON, "
                + "access TEXT, "
                + "beginner INTEGER, "
                + "intermediate INTEGER, "
                + "advanced INTEGER, "
                + "expert INTEGER, "
                + "abstract TEXT, "
                + "keywords TEXT, "
                + "body TEXT NOT NULL, "
                + "ref_list TEXT, "
                + "specialaccessgroups JSON"
                + ");";
         statement.execute(createArticlesTableSQL);
 		String query = "SELECT COUNT(*) AS count FROM articles";
 		ResultSet resultSet = statement.executeQuery(query);
 		if (resultSet.next()) {
             System.out.println("Articles is in existence");
 			return resultSet.getInt("count") == 0;
 		}
         System.out.println("No articles exist.");
 		return true;
 	}
 	
 	// empties the articles table
     public void emptyArticles() throws Exception {
         try {
             String droparticleTable = "DROP TABLE IF EXISTS articles;";
             statement.executeUpdate(droparticleTable);

         } catch (SQLException e) {
             System.err.println("SQL error while emptying the database: " + e.getMessage());
         } finally {
             System.out.println("Database emptied successfully.");
         }
     }
     
     // inserts the information from the articles table into the user specified file
     public boolean backup(String filePath) throws Exception {
         String query = "SELECT title, headers, groups, access, beginner, intermediate, advanced, expert, abstract, keywords, body, ref_list, specialaccessgroups FROM articles";
         
         // Create a file writer and buffered writer
         try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
              ResultSet resultSet = statement.executeQuery(query)) {

             // Write articles data
             while (resultSet.next()) {
                 writer.write("Title: " + resultSet.getString("title") + "\n");
                 writer.write("Headers: " + (resultSet.getString("headers") != null ? resultSet.getString("headers") : "N/A") + "\n");

                 // Handle groups JSON column
                 String groupsJson = resultSet.getString("groups");
                 String parsedGroups;
                 try {
                     if (groupsJson != null) {
                         JSONArray groupsArray = new JSONArray(groupsJson);
                         parsedGroups = groupsArray.toString(); // Write groups as a JSON string
                     } else {
                         parsedGroups = "N/A";
                     }
                 } catch (JSONException e) {
                     parsedGroups = "Invalid JSON format: " + groupsJson;
                 }
                 writer.write("Groups: " + parsedGroups + "\n");

                 writer.write("Access: " + (resultSet.getString("access") != null ? resultSet.getString("access") : "N/A") + "\n");
                 writer.write("Beginner: " + resultSet.getInt("beginner") + "\n");
                 writer.write("Intermediate: " + resultSet.getInt("intermediate") + "\n");
                 writer.write("Advanced: " + resultSet.getInt("advanced") + "\n");
                 writer.write("Expert: " + resultSet.getInt("expert") + "\n");
                 writer.write("Abstract: " + (resultSet.getString("abstract") != null ? resultSet.getString("abstract") : "N/A") + "\n");
                 writer.write("Keywords: " + (resultSet.getString("keywords") != null ? resultSet.getString("keywords") : "N/A") + "\n");
                 writer.write("Body: " + (resultSet.getString("body") != null ? resultSet.getString("body") : "N/A") + "\n");
                 writer.write("References: " + (resultSet.getString("ref_list") != null ? resultSet.getString("ref_list") : "N/A") + "\n");

                 // Handle specialaccessgroups JSON column after references
                 String specialAccessJson = resultSet.getString("specialaccessgroups");
                 String parsedSpecialAccess;
                 try {
                     if (specialAccessJson != null) {
                         JSONArray specialAccessArray = new JSONArray(specialAccessJson);
                         parsedSpecialAccess = specialAccessArray.toString(); // Write special access groups as a JSON string
                     } else {
                         parsedSpecialAccess = "N/A";
                     }
                 } catch (JSONException e) {
                     parsedSpecialAccess = "Invalid JSON format: " + specialAccessJson;
                 }
                 writer.write("Special Access Groups: " + parsedSpecialAccess + "\n");

                 writer.write("\n"); // Blank line between articles
             }

             System.out.println("Articles backup completed successfully.");
             return true;

         } catch (SQLException | IOException e) {
             System.err.println("Error while backing up articles: " + e.getMessage());
             return false;
         }
     }

     
     // inserts the information from the specified groups in articles table into the user specified file
     public boolean backupGroup(String filePath, List<Long> idList) throws Exception {
         if (idList == null || idList.isEmpty()) {
             return false;
         }

         // Build the SQL query with IN clause
         StringBuilder query = new StringBuilder("SELECT * FROM articles WHERE id IN (");
         for (int i = 0; i < idList.size(); i++) {
             query.append("?");
             if (i < idList.size() - 1) {
                 query.append(", ");
             }
         }
         query.append(")");

         // Prepare and execute the statement
         try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
             for (int i = 0; i < idList.size(); i++) {
                 stmt.setLong(i + 1, idList.get(i)); // Set each id in the IN clause
             }

             // Create a file writer and buffered writer
             try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
                  ResultSet resultSet = stmt.executeQuery()) {

                 // Write articles data
                 while (resultSet.next()) {
                     writer.write("Title: " + resultSet.getString("title") + "\n");
                     writer.write("Headers: " + (resultSet.getString("headers") != null ? resultSet.getString("headers") : "N/A") + "\n");

                     // Handle groups JSON column
                     String groupsJson = resultSet.getString("groups");
                     String parsedGroups;
                     try {
                         if (groupsJson != null) {
                             JSONArray groupsArray = new JSONArray(groupsJson);
                             parsedGroups = groupsArray.toString(); // Write groups as a JSON string
                         } else {
                             parsedGroups = "N/A";
                         }
                     } catch (JSONException e) {
                         parsedGroups = "Invalid JSON format: " + groupsJson;
                     }
                     writer.write("Groups: " + parsedGroups + "\n");

                     writer.write("Access: " + (resultSet.getString("access") != null ? resultSet.getString("access") : "N/A") + "\n");
                     writer.write("Beginner: " + resultSet.getInt("beginner") + "\n");
                     writer.write("Intermediate: " + resultSet.getInt("intermediate") + "\n");
                     writer.write("Advanced: " + resultSet.getInt("advanced") + "\n");
                     writer.write("Expert: " + resultSet.getInt("expert") + "\n");
                     writer.write("Abstract: " + (resultSet.getString("abstract") != null ? resultSet.getString("abstract") : "N/A") + "\n");
                     writer.write("Keywords: " + (resultSet.getString("keywords") != null ? resultSet.getString("keywords") : "N/A") + "\n");
                     writer.write("Body: " + (resultSet.getString("body") != null ? resultSet.getString("body") : "N/A") + "\n");
                     writer.write("References: " + (resultSet.getString("ref_list") != null ? resultSet.getString("ref_list") : "N/A") + "\n");

                     // Handle specialaccessgroups JSON column after references
                     String specialAccessJson = resultSet.getString("specialaccessgroups");
                     String parsedSpecialAccess;
                     try {
                         if (specialAccessJson != null) {
                             JSONArray specialAccessArray = new JSONArray(specialAccessJson);
                             parsedSpecialAccess = specialAccessArray.toString(); // Write special access groups as a JSON string
                         } else {
                             parsedSpecialAccess = "N/A";
                         }
                     } catch (JSONException e) {
                         parsedSpecialAccess = "Invalid JSON format: " + specialAccessJson;
                     }
                     writer.write("Special Access Groups: " + parsedSpecialAccess + "\n");

                     writer.write("\n"); // Blank line between articles
                 }

                 System.out.println("Articles backup completed successfully.");
                 return true;

             } catch (SQLException | IOException e) {
                 System.err.println("Error while backing up articles: " + e.getMessage());
                 return false;
             }
         }
     }


     // Method to restore articles from a file into the main articles table
     public boolean restore(String filePath) throws Exception {
         // SQL to create the articles table with specialaccessgroups field
         String createArticlesTableSQL = "CREATE TABLE IF NOT EXISTS articles ("
                 + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                 + "title TEXT NOT NULL, "
                 + "headers TEXT, "
                 + "groups JSON, "
                 + "access TEXT, "
                 + "beginner INTEGER, "
                 + "intermediate INTEGER, "
                 + "advanced INTEGER, "
                 + "expert INTEGER, "
                 + "abstract TEXT, "
                 + "keywords TEXT, "
                 + "body TEXT NOT NULL, "
                 + "ref_list TEXT, "
                 + "specialaccessgroups JSON"
                 + ");";

         try {
             statement.execute(createArticlesTableSQL);
         } catch (SQLException e) {
             System.err.println("Error creating articles table: " + e.getMessage());
             return false;
         }

         // SQL to insert articles
         String insertSQL = "INSERT INTO articles (title, headers, groups, access, beginner, intermediate, advanced, expert, abstract, keywords, body, ref_list, specialaccessgroups) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

         // Validate file existence and readability
         File file = new File(filePath);
         if (!file.exists()) {
             System.err.println("Error: File not found at " + filePath);
             return false;
         } else if (!file.canRead()) {
             System.err.println("Error: File is not readable at " + filePath);
             return false;
         }
         
         try (BufferedReader br = new BufferedReader(new FileReader(filePath));
              PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

             String line;
             String title = "", headers = "", groups = "", access = "", specialAccessGroups = "";
             int beginner = 0, intermediate = 0, advanced = 0, expert = 0;
             String abstractText = "", keywords = "", body = "", references = "";

             while ((line = br.readLine()) != null) {
                 if (line.startsWith("Title: ")) {
                     title = line.substring(7).trim();
                 } else if (line.startsWith("Headers: ")) {
                     headers = line.substring(9).trim();
                 } else if (line.startsWith("Groups: ")) {
                     groups = line.substring(8).trim();
                 } else if (line.startsWith("Access: ")) {
                     access = line.substring(8).trim();
                 } else if (line.startsWith("Beginner: ")) {
                     beginner = Integer.parseInt(line.substring(10).trim());
                 } else if (line.startsWith("Intermediate: ")) {
                     intermediate = Integer.parseInt(line.substring(14).trim());
                 } else if (line.startsWith("Advanced: ")) {
                     advanced = Integer.parseInt(line.substring(10).trim());
                 } else if (line.startsWith("Expert: ")) {
                     expert = Integer.parseInt(line.substring(8).trim());
                 } else if (line.startsWith("Abstract: ")) {
                     abstractText = line.substring(10).trim();
                 } else if (line.startsWith("Keywords: ")) {
                     keywords = line.substring(10).trim();
                 } else if (line.startsWith("Body: ")) {
                     body = line.substring(6).trim();
                 } else if (line.startsWith("References: ")) {
                     references = line.substring(11).trim();
                 } else if (line.startsWith("Special Access Groups: ")) {
                     specialAccessGroups = line.substring(22).trim(); // Extract the special access groups
                 } else if (line.isEmpty()) {
                     // Process each article once an empty line is encountered
                     try {
                         // If special access groups is a valid JSON string, parse it, else leave it as is
                         if (!specialAccessGroups.isEmpty()) {
                             new JSONArray(specialAccessGroups); // Validate JSON format
                         } else {
                             specialAccessGroups = "[]"; // Nullify if no special access groups
                         }                        
                         if (!groups.isEmpty()) {
                             new JSONArray(groups);  // Validate JSON format
                         } else {
                             groups = "[]";  // Default to empty JSON array
                         }
                     } catch (JSONException e) {
                         specialAccessGroups = "Invalid JSON format"; // Handle invalid JSON gracefully
                         groups = "[]";
                         specialAccessGroups = "[]";
                     }

                     // Set prepared statement parameters
                     preparedStatement.setString(1, title);
                     preparedStatement.setString(2, headers);
                     preparedStatement.setString(3, groups);
                     preparedStatement.setString(4, access);
                     preparedStatement.setInt(5, beginner);
                     preparedStatement.setInt(6, intermediate);
                     preparedStatement.setInt(7, advanced);
                     preparedStatement.setInt(8, expert);
                     preparedStatement.setString(9, abstractText);
                     preparedStatement.setString(10, keywords);
                     preparedStatement.setString(11, body);
                     preparedStatement.setString(12, references);
                     preparedStatement.setString(13, specialAccessGroups); // Set special access groups field

                     preparedStatement.addBatch();

                     // Reset variables
                     title = headers = groups = access = abstractText = keywords = body = references = specialAccessGroups = "";
                     beginner = intermediate = advanced = expert = 0;
                 }
             }

             // Execute batch insert
             int[] rowsAffected = preparedStatement.executeBatch();
             if (rowsAffected.length > 0) {
                 System.out.println("Articles restored successfully.");
                 return true;
             } else {
                 System.out.println("No articles were restored.");
                 return false;
             }

         } catch (SQLException | IOException e) {
             System.err.println("Error while restoring articles: " + e.getMessage());
             return false;
         }
     }
     
     // Method to add an article to a group after restoring
     void addArticlesToGroups(String groupNames, String articleTitle) {
    	    // Convert the group names (or IDs) to a list
    	    String[] groupsArray = groupNames.replaceAll("[\\[\\]\" ]", "").split(",");

    	    for (String groupName : groupsArray) {
    	        // Get the articles that were just inserted
    	        String getArticleSQL = "SELECT id FROM articles WHERE title = ?";  // Assuming title is unique
    	        try (PreparedStatement getArticleStmt = connection.prepareStatement(getArticleSQL)) {
    	            getArticleStmt.setString(1, articleTitle);
    	            ResultSet rs = getArticleStmt.executeQuery();
    	            
    	            if (rs.next()) {
    	                int articleId = rs.getInt("id");

    	                // Now, update the group's article_ids with the new article ID
    	                String updateGroupSQL = "UPDATE specialaccess SET article_ids = json_insert(article_ids, '$', ?) WHERE groupname = ?";
    	                try (PreparedStatement updateGroupStmt = connection.prepareStatement(updateGroupSQL)) {
    	                    updateGroupStmt.setInt(1, articleId);
    	                    updateGroupStmt.setString(2, groupName);
    	                    updateGroupStmt.executeUpdate();
    	                }
    	            }
    	        } catch (SQLException e) {
    	            System.err.println("Error adding article to group: " + e.getMessage());
    	        }
    	    }
    	}
     
     // Method to get the article title after restoring
     String getRestoredArticleTitle(int articleId) {
    	    String title = null;
    	    String getTitleSQL = "SELECT title FROM articles WHERE id = ?";
    	    
    	    try (PreparedStatement getTitleStmt = connection.prepareStatement(getTitleSQL)) {
    	        getTitleStmt.setInt(1, articleId); // Set the article ID to search for
    	        
    	        try (ResultSet rs = getTitleStmt.executeQuery()) {
    	            if (rs.next()) {
    	                title = rs.getString("title");  // Get the title from the result set
    	            } else {
    	                System.err.println("Article not found with ID: " + articleId);
    	            }
    	        }
    	    } catch (SQLException e) {
    	        System.err.println("Error retrieving article title: " + e.getMessage());
    	    }
    	    
    	    return title;
    	}
     
     // Method to get the article id after restoring
     int getNewArticleId() {
    	    int articleId = -1;  // Default value indicating failure
    	    String insertSQL = "INSERT INTO articles (title, headers, groups, access, beginner, intermediate, advanced, expert, abstract, keywords, body, ref_list, specialaccessgroups) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    	    
    	    try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
    	        
    	        // Set parameters for the insert query
    	        preparedStatement.setString(1, "Sample Title");
    	        preparedStatement.setString(2, "Sample Headers");
    	        preparedStatement.setString(3, "[]");  // Assuming empty JSON for simplicity
    	        preparedStatement.setString(4, "Public");
    	        preparedStatement.setInt(5, 1);
    	        preparedStatement.setInt(6, 1);
    	        preparedStatement.setInt(7, 1);
    	        preparedStatement.setInt(8, 1);
    	        preparedStatement.setString(9, "Abstract Text");
    	        preparedStatement.setString(10, "Keyword1, Keyword2");
    	        preparedStatement.setString(11, "Body content of the article");
    	        preparedStatement.setString(12, "Reference list");
    	        preparedStatement.setString(13, "[]");  // Assuming special access groups are empty

    	        int affectedRows = preparedStatement.executeUpdate();

    	        if (affectedRows > 0) {
    	            // Get the generated ID of the newly inserted article
    	            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
    	                if (generatedKeys.next()) {
    	                    articleId = generatedKeys.getInt(1);  // Retrieve the generated ID
    	                    System.out.println("New article ID: " + articleId);
    	                }
    	            }
    	        } else {
    	            System.err.println("No rows affected, article not inserted.");
    	        }

    	    } catch (SQLException e) {
    	        System.err.println("Error inserting article: " + e.getMessage());
    	    }
    	    
    	    return articleId;
    	}


     // Method to merge articles from a file into the main articles table for articlesPage method
     public boolean mergeArticles(String filePath) {
         // SQL to create temporary table for articles including the specialaccessgroups field
         String createTempTableSQL = "CREATE TEMP TABLE IF NOT EXISTS TempArticles ("
                 + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                 + "title TEXT NOT NULL, "
                 + "headers TEXT, "
                 + "groups JSON, "
                 + "access TEXT, "
                 + "beginner INTEGER, "
                 + "intermediate INTEGER, "
                 + "advanced INTEGER, "
                 + "expert INTEGER, "
                 + "abstract TEXT, "
                 + "keywords TEXT, "
                 + "body TEXT NOT NULL, "
                 + "ref_list TEXT, "
                 + "specialaccessgroups JSON);";

         // SQL to insert into the TempArticles table
         String insertTempSQL = "INSERT INTO TempArticles (title, headers, groups, access, beginner, intermediate, advanced, expert, abstract, keywords, body, ref_list, specialaccessgroups) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

         // SQL to merge articles into the main table, ensuring no duplicate titles
         String mergeSQL = """
             INSERT INTO articles (title, headers, groups, access, beginner, intermediate, advanced, expert, abstract, keywords, body, ref_list, specialaccessgroups)
             SELECT t.title, t.headers, t.groups, t.access, t.beginner, t.intermediate, t.advanced, t.expert, t.abstract, t.keywords, t.body, t.ref_list, t.specialaccessgroups
             FROM TempArticles t
             WHERE NOT EXISTS (
                 SELECT 1 FROM articles a WHERE a.title = t.title
             );
         """;

         try {
             statement.execute(createTempTableSQL);
         } catch (SQLException e) {
             System.err.println("Error creating temporary articles table: " + e.getMessage());
             return false;
         }

         // Prepare for reading the backup file and inserting data into the temporary table
         try (BufferedReader br = new BufferedReader(new FileReader(filePath));
              PreparedStatement tempStmt = connection.prepareStatement(insertTempSQL)) {

             String line;
             String title = "", headers = "", groups = "", access = "", specialAccessGroups = "";
             int beginner = 0, intermediate = 0, advanced = 0, expert = 0;
             String abstractText = "", keywords = "", body = "", references = "";

             while ((line = br.readLine()) != null) {
                 if (line.startsWith("Title: ")) {
                     title = line.substring(7).trim();
                 } else if (line.startsWith("Headers: ")) {
                     headers = line.substring(9).trim();
                 } else if (line.startsWith("Groups: ")) {
                     groups = line.substring(8).trim();
                 } else if (line.startsWith("Access: ")) {
                     access = line.substring(8).trim();
                 } else if (line.startsWith("Beginner: ")) {
                     beginner = Integer.parseInt(line.substring(10).trim());
                 } else if (line.startsWith("Intermediate: ")) {
                     intermediate = Integer.parseInt(line.substring(14).trim());
                 } else if (line.startsWith("Advanced: ")) {
                     advanced = Integer.parseInt(line.substring(10).trim());
                 } else if (line.startsWith("Expert: ")) {
                     expert = Integer.parseInt(line.substring(8).trim());
                 } else if (line.startsWith("Abstract: ")) {
                     abstractText = line.substring(10).trim();
                 } else if (line.startsWith("Keywords: ")) {
                     keywords = line.substring(10).trim();
                 } else if (line.startsWith("Body: ")) {
                     body = line.substring(6).trim();
                 } else if (line.startsWith("References: ")) {
                     references = line.substring(11).trim();
                 } else if (line.startsWith("Special Access Groups: ")) {
                     specialAccessGroups = line.substring(22).trim(); // Extract the special access groups field
                 } else if (line.isEmpty()) {
                     // Process each article when an empty line is encountered
                	 try {
                		    if (!groups.isEmpty()) {
                		        new JSONArray(groups); // Validate JSON format
                		    } else {
                		        groups = null; // Nullify if empty
                		    }
                		} catch (JSONException e) {
                		    System.err.println("Invalid JSON in 'groups'. Skipping article with title: " + title);
                		    groups = null; // Optionally set it to null or handle as needed
                		}

                		try {
                		    if (!specialAccessGroups.isEmpty()) {
                		        new JSONArray(specialAccessGroups); // Validate JSON format
                		    } else {
                		        specialAccessGroups = null; // Nullify if empty
                		    }
                		} catch (JSONException e) {
                		    System.err.println("Invalid JSON in 'specialAccessGroups'. Skipping article with title: " + title);
                		    specialAccessGroups = null; // Optionally set it to null or handle as needed
                		}

                     // Insert article into TempArticles table
                     tempStmt.setString(1, title);
                     tempStmt.setString(2, headers);
                     tempStmt.setString(3, groups);  // JSON formatted groups
                     tempStmt.setString(4, access);
                     tempStmt.setInt(5, beginner);
                     tempStmt.setInt(6, intermediate);
                     tempStmt.setInt(7, advanced);
                     tempStmt.setInt(8, expert);
                     tempStmt.setString(9, abstractText);
                     tempStmt.setString(10, keywords);
                     tempStmt.setString(11, body);
                     tempStmt.setString(12, references);
                     tempStmt.setString(13, specialAccessGroups);  // JSON formatted special access groups
                     tempStmt.addBatch();

                     // Reset variables for the next article
                     title = headers = groups = access = specialAccessGroups = "";
                     abstractText = keywords = body = references = "";
                     beginner = intermediate = advanced = expert = 0;
                 }
             }

             // Execute the batch insert into the temporary table
             tempStmt.executeBatch();
             // Merge the temporary table into the main articles table
             statement.executeUpdate(mergeSQL);
             System.out.println("Merge completed successfully.");
             return true;

         } catch (SQLException | IOException e) {
             System.err.println("Error during merging: " + e.getMessage());
             return false;
         }
     }
     
     // Method to merge articles for speical access groups
     public boolean mergeArticlesforGroups(String filePath, String sourceGroup, String targetGroup) {
    	    // SQL to create temporary table for articles including the specialaccessgroups field
    	    String createTempTableSQL = "CREATE TEMP TABLE IF NOT EXISTS TempArticles ("
    	            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
    	            + "title TEXT NOT NULL, "
    	            + "headers TEXT, "
    	            + "groups JSON, "
    	            + "access TEXT, "
    	            + "beginner INTEGER, "
    	            + "intermediate INTEGER, "
    	            + "advanced INTEGER, "
    	            + "expert INTEGER, "
    	            + "abstract TEXT, "
    	            + "keywords TEXT, "
    	            + "body TEXT NOT NULL, "
    	            + "ref_list TEXT, "
    	            + "specialaccessgroups JSON);";

    	    // SQL to insert into the TempArticles table
    	    String insertTempSQL = "INSERT INTO TempArticles (title, headers, groups, access, beginner, intermediate, advanced, expert, abstract, keywords, body, ref_list, specialaccessgroups) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    	    // SQL to merge articles into the main table, ensuring no duplicate titles
    	    String mergeSQL = """
    	        INSERT INTO articles (title, headers, groups, access, beginner, intermediate, advanced, expert, abstract, keywords, body, ref_list, specialaccessgroups)
    	        SELECT t.title, t.headers, t.groups, t.access, t.beginner, t.intermediate, t.advanced, t.expert, t.abstract, t.keywords, t.body, t.ref_list, t.specialaccessgroups
    	        FROM TempArticles t
    	        WHERE NOT EXISTS (
    	            SELECT 1 FROM articles a WHERE a.title = t.title
    	        );
    	    """;

    	    // SQL to update the groups field after the merge
    	    String updateGroupsSQL = """
    	        UPDATE articles
    	        SET groups = CASE
    	            WHEN groups IS NULL THEN json_array(?)
    	            WHEN groups NOT LIKE '%?%' THEN json_insert(groups, '$', ?)
    	            ELSE groups
    	        END
    	        WHERE groups LIKE ?;
    	    """;

    	    try {
    	        statement.execute(createTempTableSQL);
    	    } catch (SQLException e) {
    	        System.err.println("Error creating temporary articles table: " + e.getMessage());
    	        return false;
    	    }

    	    // Prepare for reading the backup file and inserting data into the temporary table
    	    try (BufferedReader br = new BufferedReader(new FileReader(filePath));
    	         PreparedStatement tempStmt = connection.prepareStatement(insertTempSQL)) {

    	        String line;
    	        String title = "", headers = "", groups = "", access = "", specialAccessGroups = "";
    	        int beginner = 0, intermediate = 0, advanced = 0, expert = 0;
    	        String abstractText = "", keywords = "", body = "", references = "";

    	        while ((line = br.readLine()) != null) {
    	            if (line.startsWith("Title: ")) {
    	                title = line.substring(7).trim();
    	            } else if (line.startsWith("Headers: ")) {
    	                headers = line.substring(9).trim();
    	            } else if (line.startsWith("Groups: ")) {
    	                groups = line.substring(8).trim();
    	            } else if (line.startsWith("Access: ")) {
    	                access = line.substring(8).trim();
    	            } else if (line.startsWith("Beginner: ")) {
    	                beginner = Integer.parseInt(line.substring(10).trim());
    	            } else if (line.startsWith("Intermediate: ")) {
    	                intermediate = Integer.parseInt(line.substring(14).trim());
    	            } else if (line.startsWith("Advanced: ")) {
    	                advanced = Integer.parseInt(line.substring(10).trim());
    	            } else if (line.startsWith("Expert: ")) {
    	                expert = Integer.parseInt(line.substring(8).trim());
    	            } else if (line.startsWith("Abstract: ")) {
    	                abstractText = line.substring(10).trim();
    	            } else if (line.startsWith("Keywords: ")) {
    	                keywords = line.substring(10).trim();
    	            } else if (line.startsWith("Body: ")) {
    	                body = line.substring(6).trim();
    	            } else if (line.startsWith("References: ")) {
    	                references = line.substring(11).trim();
    	            } else if (line.startsWith("Special Access Groups: ")) {
    	                specialAccessGroups = line.substring(22).trim(); // Extract the special access groups field
    	            } else if (line.isEmpty()) {
    	                // Process each article when an empty line is encountered
    	                try {
    	                    if (!groups.isEmpty()) {
    	                        new JSONArray(groups); // Validate JSON format
    	                    } else {
    	                        groups = null; // Nullify if empty
    	                    }
    	                } catch (JSONException e) {
    	                    System.err.println("Invalid JSON in 'groups'. Skipping article with title: " + title);
    	                    groups = null; // Optionally set it to null or handle as needed
    	                }

    	                try {
    	                    if (!specialAccessGroups.isEmpty()) {
    	                        new JSONArray(specialAccessGroups); // Validate JSON format
    	                    } else {
    	                        specialAccessGroups = null; // Nullify if empty
    	                    }
    	                } catch (JSONException e) {
    	                    System.err.println("Invalid JSON in 'specialAccessGroups'. Skipping article with title: " + title);
    	                    specialAccessGroups = null; // Optionally set it to null or handle as needed
    	                }

    	                // Insert article into TempArticles table
    	                tempStmt.setString(1, title);
    	                tempStmt.setString(2, headers);
    	                tempStmt.setString(3, groups);  // JSON formatted groups
    	                tempStmt.setString(4, access);
    	                tempStmt.setInt(5, beginner);
    	                tempStmt.setInt(6, intermediate);
    	                tempStmt.setInt(7, advanced);
    	                tempStmt.setInt(8, expert);
    	                tempStmt.setString(9, abstractText);
    	                tempStmt.setString(10, keywords);
    	                tempStmt.setString(11, body);
    	                tempStmt.setString(12, references);
    	                tempStmt.setString(13, specialAccessGroups);  // JSON formatted special access groups
    	                tempStmt.addBatch();

    	                // Reset variables for the next article
    	                title = headers = groups = access = specialAccessGroups = "";
    	                abstractText = keywords = body = references = "";
    	                beginner = intermediate = advanced = expert = 0;
    	            }
    	        }

    	        // Execute the batch insert into the temporary table
    	        tempStmt.executeBatch();

    	        // Merge the temporary table into the main articles table
    	        statement.executeUpdate(mergeSQL);

    	        // After merge, update the groups to add the target group to the articles that belong to the source group
    	        try (PreparedStatement updateStmt = connection.prepareStatement(updateGroupsSQL)) {
    	            // Add the target group to articles from the source group
    	            updateStmt.setString(1, targetGroup);  // Add target group
    	            updateStmt.setString(2, "%" + sourceGroup + "%");  // Match articles with the source group
    	            updateStmt.setString(3, targetGroup);  // Ensure the target group is added only if not already there
    	            updateStmt.executeUpdate();
    	            System.out.println("Group merge completed successfully.");
    	        }

    	        System.out.println("Merge completed successfully.");
    	        return true;

    	    } catch (SQLException | IOException e) {
    	        System.err.println("Error during merging: " + e.getMessage());
    	        return false;
    	    }
    	}

     
     // Method to retrieve a limited list of articles (title)
     public List<String> getAllArticlesLimited() throws SQLException {
    	    String query = "SELECT * FROM articles";
    	    List<String> articles = new ArrayList<>(); // List to store article information

    	    try (Statement stmt = connection.createStatement();
    	         ResultSet rs = stmt.executeQuery(query)) {
    	        while (rs.next()) {
    	            long id = rs.getLong("id"); // Retrieve the article id
    	            String title = rs.getString("title"); // Retrieve the article title
    	            String abstractText = rs.getString("abstract"); // Retrieve the article abstract

    	            // Format the output to include id, title, and abstract
    	            String articleInfo = "ID: " + id + ", Title: " + title + ", Abstract: " + abstractText;
    	            articles.add(articleInfo); // Add formatted string to the list
    	        }
    	    }  	    
    	    return articles; // Return the list of articles
    	}

 	
    // Method to retrieve detailed information about a specific article
	public String getArticleDetailsById(long id) throws Exception {
	    String query = "SELECT * FROM articles WHERE id = ?";
	    StringBuilder articleDetails = new StringBuilder();
	
	    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	        preparedStatement.setLong(1, id);
	
	        try (ResultSet rs = preparedStatement.executeQuery()) {
	            if (rs.next()) {
	                // Fetch article details
	                String title = rs.getString("title");
	                String headers = rs.getString("headers");
	                String groupsJson = rs.getString("groups"); // Fetch groups as JSON
	                String access = rs.getString("access");
	                int beginner = rs.getInt("beginner");
	                int intermediate = rs.getInt("intermediate");
	                int advanced = rs.getInt("advanced");
	                int expert = rs.getInt("expert");
	                String abstractText = rs.getString("abstract");
	                String keywords = rs.getString("keywords");
	                String body = rs.getString("body");
	    			String decryptedBody = new String(
	                        encryptionHelper.decrypt(
	                                Base64.getDecoder().decode(body),
	                                EncryptionUtils.getInitializationVector(title.toCharArray())
	                        )
	                );
	                String references = rs.getString("ref_list");
	
	                // Parse the groups JSON array
	                String parsedGroups;
	                try {
	                    if (groupsJson != null) {
	                        JSONArray groupsArray = new JSONArray(groupsJson);
	                        parsedGroups = groupsArray.toString();
	                    } else {
	                        parsedGroups = "N/A";
	                    }
	                } catch (JSONException e) {
	                    parsedGroups = "Invalid JSON format: " + groupsJson;
	                }
	
	                // Build the detailed information string
	                articleDetails.append("Article Details:\n")
	                        .append("Title: ").append(title).append("\n")
	                        .append("Headers: ").append(headers != null ? headers : "N/A").append("\n")
	                        .append("Groups: ").append(parsedGroups).append("\n")
	                        .append("Access: ").append(access != null ? access : "N/A").append("\n")
	                        .append("Beginner: ").append(beginner).append("\n")
	                        .append("Intermediate: ").append(intermediate).append("\n")
	                        .append("Advanced: ").append(advanced).append("\n")
	                        .append("Expert: ").append(expert).append("\n")
	                        .append("Abstract: ").append(abstractText != null ? abstractText : "N/A").append("\n")
	                        .append("Keywords: ").append(keywords != null ? keywords : "N/A").append("\n")
	                        .append("Body: ").append(decryptedBody != null ? decryptedBody : "N/A").append("\n")
	                        .append("References: ").append(references != null ? references : "N/A").append("\n");
	            } else {
	                articleDetails.append("No article found with ID: ").append(id);
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Failed to retrieve article details: " + e.getMessage());
	        throw e; // Rethrow the exception for the caller to handle
	    }
	
	    return articleDetails.toString();
	}
     
     // Method to get unique article IDs that belong to the specified group(s)
     public List<Long> getArticlesByGroups(String groupsString) throws SQLException {
	    Set<Long> uniqueArticleIds = new HashSet<>(); // Use a Set to avoid duplicates
	    String[] groupsArray = groupsString.split(","); // Split input groups by comma

	    String query = "SELECT id, groups FROM articles";

	    try (Statement stmt = connection.createStatement();
	         ResultSet rs = stmt.executeQuery(query)) {

	        while (rs.next()) {
	            long articleId = rs.getLong("id");
	            String articleGroups = rs.getString("groups");

	            if (articleGroups != null && !articleGroups.isEmpty()) {
	                try {
	                    // Parse the groups field as a JSON array
	                    JSONArray articleGroupsArray = new JSONArray(articleGroups);

	                    for (String group : groupsArray) {
	                        String trimmedGroup = group.trim();
	                        for (int i = 0; i < articleGroupsArray.length(); i++) {
	                            // Check if the group exists in the JSON array
	                            if (articleGroupsArray.getString(i).equals(trimmedGroup)) {
	                                uniqueArticleIds.add(articleId); // Add to Set to ensure uniqueness
	                                break; // Stop searching once a match is found
	                            }
	                        }
	                    }
	                } catch (JSONException e) {
	                    System.err.println("Error parsing groups JSON for article ID " + articleId + ": " + e.getMessage());
	                }
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Failed to retrieve articles by group(s): " + e.getMessage());
	        throw e;
	    }
	    // Convert Set to List and return
	    return new ArrayList<>(uniqueArticleIds);
	}

     // Method to print all articles in a given group
     public List<String> getAllArticlesGroups(List<Long> idList) throws SQLException {
    	    List<String> articles = new ArrayList<>(); // List to store article information

    	    // Check if idList is empty to avoid unnecessary query
    	    if (idList == null || idList.isEmpty()) {
    	        return articles;
    	    }

    	    // Build the SQL query with IN clause
    	    StringBuilder query = new StringBuilder("SELECT * FROM articles WHERE id IN (");
    	    for (int i = 0; i < idList.size(); i++) {
    	        query.append("?");
    	        if (i < idList.size() - 1) {
    	            query.append(", ");
    	        }
    	    }
    	    query.append(")");

    	    // Prepare and execute the statement
    	    try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
    	        for (int i = 0; i < idList.size(); i++) {
    	            stmt.setLong(i + 1, idList.get(i)); // Set each id in the IN clause
    	        }

    	        try (ResultSet rs = stmt.executeQuery()) {
    	            while (rs.next()) {
    	                long id = rs.getLong("id");
    	                String title = rs.getString("title");
    	                String abstractText = rs.getString("abstract");

    	                // Format and add article information to the list
    	                String articleInfo = "ID: " + id + ", Title: " + title + ", Abstract: " + abstractText;
    	                articles.add(articleInfo);
    	            }
    	        }
    	    }

    	    return articles; // Return the list of articles
    }

     // Method to insert an article into the table of articles
     public boolean insertArticle(String title, String headers, String groups, boolean admin, boolean instructor, boolean student, boolean beginner, boolean intermediate, boolean advanced, boolean expert, String abstractText, String keywords, String body, String references) throws Exception {
	    // Ensure the updated table exists
	    String createArticlesTableSQL = "CREATE TABLE IF NOT EXISTS articles ("
	            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
	            + "title TEXT NOT NULL, "
	            + "headers TEXT, "
	            + "groups JSON, "
	            + "access TEXT, "
	            + "beginner INTEGER, "
	            + "intermediate INTEGER, "
	            + "advanced INTEGER, "
	            + "expert INTEGER, "
	            + "abstract TEXT, "
	            + "keywords TEXT, "
	            + "body TEXT NOT NULL, "
	            + "ref_list TEXT, "
	            + "specialaccessgroups JSON"
	            + ");";
	    try {
	        statement.execute(createArticlesTableSQL);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    // Encrypt the body
	    String encryptedbody = Base64.getEncoder().encodeToString(
	            encryptionHelper.encrypt(body.getBytes(), EncryptionUtils.getInitializationVector(title.toCharArray()))
	    );
	    System.out.println("Original title: " + title);
	    System.out.println("Original headers: " + headers);
	    System.out.println("Original groups: " + groups);
	    System.out.println("Original keywords: " + keywords);
	    System.out.println("Original body: " + body);
	    System.out.println("New body: " + encryptedbody);
	    System.out.println("Original references: " + references);

	    // Convert boolean values into an access string
	    String access = "admin:" + (admin ? "1" : "0") + ","
	                  + "instructor:" + (instructor ? "1" : "0") + ","
	                  + "student:" + (student ? "1" : "0");

	    // Parse the groups into a JSON array
	    JSONArray groupsArray = new JSONArray();
	    if (groups != null && !groups.trim().isEmpty()) {
	        String[] groupIds = groups.split(",");
	        for (String groupId : groupIds) {
	            groupsArray.put(groupId.trim());
	        }
	    }

	    // Debugging log for parsed groups
	    System.out.println("Parsed groups JSON array: " + groupsArray.toString());

	    // SQL query to insert the article into the database
	    String insertSQL = "INSERT INTO articles (title, headers, groups, access, beginner, intermediate, advanced, expert, abstract, keywords, body, ref_list) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
	        // Set parameters for insertion
	        preparedStatement.setString(1, title);
	        preparedStatement.setString(2, headers);
	        preparedStatement.setString(3, groupsArray.toString()); // Store the groups JSON array as a string
	        preparedStatement.setString(4, access);
	        preparedStatement.setInt(5, beginner ? 1 : 0);
	        preparedStatement.setInt(6, intermediate ? 1 : 0);
	        preparedStatement.setInt(7, advanced ? 1 : 0);
	        preparedStatement.setInt(8, expert ? 1 : 0);
	        preparedStatement.setString(9, abstractText);
	        preparedStatement.setString(10, keywords);
	        preparedStatement.setString(11, encryptedbody);
	        preparedStatement.setString(12, references);

	        int rowsAffected = preparedStatement.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("Article inserted successfully.");
	            return true;
	        } else {
	            System.out.println("Failed to insert article.");
	            return false;
	        }
	    } catch (SQLException e) {
	        System.err.println("Error while inserting article: " + e.getMessage());
	        return false;
	    }
	}
 	
 	// Method to delete an article by ID
    public boolean deleteArticleById(long id) {
        String query = "DELETE FROM articles WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if an article was deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false on failure
        }
    }
    
    // Method to update a specific entry of an article
    public boolean updateArticleField(long articleId, String field, String newValue) {
        if(field.equals("body")) {
            // Encrypt the body
            try {
				newValue = Base64.getEncoder().encodeToString(
				        encryptionHelper.encrypt(newValue.getBytes(), EncryptionUtils.getInitializationVector(newValue.toCharArray()))
				);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    	
    	String updateSQL = "UPDATE articles SET " + field + " = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setString(1, newValue);
            pstmt.setLong(2, articleId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(field + " updated successfully for article ID: " + articleId);
                return true;
            } else {
                System.out.println("No article found with ID: " + articleId);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("SQL error while updating article: " + e.getMessage());
            return false;
        }
    }
    
    // updates the different levels all at once
    public boolean updateLevels(long id, boolean isBeginner, boolean isIntermediate, boolean isAdvanced, boolean isExpert) {
        // SQL statement to update levels based on the provided title
        String updateSQL = "UPDATE articles SET beginner = ?, intermediate = ?, advanced = ?, expert = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            // Set the levels based on boolean values (1 for true, 0 for false)
            preparedStatement.setInt(1, isBeginner ? 1 : 0);
            preparedStatement.setInt(2, isIntermediate ? 1 : 0);
            preparedStatement.setInt(3, isAdvanced ? 1 : 0);
            preparedStatement.setInt(4, isExpert ? 1 : 0);
            preparedStatement.setLong(5, id);

            // Execute the update and check if any rows were affected
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Levels updated successfully for article: " + id);
                return true;
            } else {
                System.out.println("No article found with the id: " + id);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error updating levels: " + e.getMessage());
            return false;
        }
    }

    // Checks if a user can view a specific article based on their role and special access group membership.
    public boolean canUserViewArticle(String userRole, String username, long articleId) throws SQLException {
        String query = "SELECT access, specialaccessgroups FROM articles WHERE id = ?";
        boolean hasAccess = false;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, articleId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    // Check role-based access
                    String accessRoles = rs.getString("access");
                    if (accessRoles != null) {
                        String[] rolesAllowed = accessRoles.split(",");
                        for (String roleAccess : rolesAllowed) {
                            String[] roleAccessPair = roleAccess.split(":");
                            if (roleAccessPair.length == 2) {
                                String role = roleAccessPair[0].trim();
                                String accessLevel = roleAccessPair[1].trim();
                                if (role.equalsIgnoreCase(userRole) && "1".equals(accessLevel)) {
                                    hasAccess = true;
                                    break;
                                }
                            }
                        }
                    }

                    // Check special access groups if not already granted access
                    if (!hasAccess) {
                        String specialAccessGroups = rs.getString("specialaccessgroups");
                        if (specialAccessGroups != null && !specialAccessGroups.isEmpty()) {
                            JSONArray specialGroups = new JSONArray(specialAccessGroups);
                            for (int i = 0; i < specialGroups.length(); i++) {
                                String groupName = specialGroups.getString(i);
                                if (isUserInGroup(groupName, username)) {
                                    hasAccess = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to check article access: " + e.getMessage());
            throw e;
        } catch (JSONException e) {
            System.err.println("Failed to parse special access groups JSON: " + e.getMessage());
        }
        return hasAccess;
    }

    
    // Method to see if the Article ID exists in the table
    public boolean isArticleIDValid(int articleId) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM articles WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, articleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0; // Returns true if count > 0
            }
        }
        return false; // If no result, the article ID is invalid
    }
    
    /**
     * THIS BEGINS THE SPECIAL ACCESS GROUP SECTION FOR THE DATABASE HELPER
     */
    
    public static void createGroup(String groupname, String username) throws SQLException {
        // Prepare the JSON arrays with the given username
        JSONArray viewAccessArray = new JSONArray();
        viewAccessArray.put(username);
        
        JSONArray adminAccessArray = new JSONArray();
        adminAccessArray.put(username);

        // SQL command to insert the new group with JSON arrays
        String sql = "INSERT INTO specialaccess (groupname, instructors_with_view_access, instructors_with_admin_access, article_ids, students_with_view_access) "
                   + "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Set the groupname and JSON fields in the SQL statement
            preparedStatement.setString(1, groupname);
            preparedStatement.setString(2, viewAccessArray.toString());  // JSON array for view access
            preparedStatement.setString(3, adminAccessArray.toString()); // JSON array for admin access
            preparedStatement.setString(4, "[]");                        // Empty JSON array for article_ids
            preparedStatement.setString(5, "[]");                        // Empty JSON array for students_with_view_access

            // Execute the insertion
            preparedStatement.executeUpdate();
            System.out.println("New group created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating group: " + e.getMessage());
            throw e;
        }
    }
    
    // Method to check if a group is in the database
    public boolean doesSpecialGroupExist(String groupName) throws SQLException {
        String query = "SELECT COUNT(*) FROM specialaccess WHERE groupname = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, groupName);

            try (ResultSet resultSet = statement.executeQuery()) {
                // If the count is greater than 0, the group exists
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false; // Group does not exist
    }
    
    // Method to check if a user is in the group
    public boolean isUserInGroup(String groupName, String username) throws SQLException, JSONException {
        String query = "SELECT instructors_with_view_access, instructors_with_admin_access FROM specialaccess WHERE groupname = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, groupName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve JSON arrays as Strings
                    String viewAccessJson = resultSet.getString("instructors_with_view_access");
                    String adminAccessJson = resultSet.getString("instructors_with_admin_access");

                    // Check both JSON arrays for the username
                    return isUserInJsonArray(viewAccessJson, username) || isUserInJsonArray(adminAccessJson, username);
                }
            }
        }
        return false; // User is not found in either access list
    }
    
    // Method to check if student user is in the group
    public boolean isStudentInGroup(String groupName, String username) throws SQLException, JSONException {
        String query = "SELECT students_with_view_access FROM specialaccess WHERE groupname = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, groupName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve JSON array as a String
                    String viewAccessJson = resultSet.getString("students_with_view_access");

                    // Check the JSON array for the username
                    return isUserInJsonArray(viewAccessJson, username);
                }
            }
        }
        return false; // User is not found in the view access list
    }

    // Method to check if user is in the JSON array
    private boolean isUserInJsonArray(String jsonArrayString, String username) {
        if (jsonArrayString == null || jsonArrayString.isEmpty()) {
            return false;
        }

        try {
            JSONArray jsonArray = new JSONArray(jsonArrayString);

            // Check if the username is in the JSON array
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getString(i).equals(username)) {
                    return true;
                }
            }
        } catch (JSONException e) {
            // Handle any JSON parsing errors
            e.printStackTrace();
        }
        return false;
    }
    
    // Method to check if user has admin access in the group
    public boolean isUserAdmin(String groupName, String username) throws SQLException, JSONException {
        String query = "SELECT instructors_with_admin_access FROM specialaccess WHERE groupname = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, groupName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve the JSON array as a String
                    String adminAccessJson = resultSet.getString("instructors_with_admin_access");
                    
                    // If JSON is null, return false
                    if (adminAccessJson == null || adminAccessJson.isEmpty()) {
                        return false;
                    }

                    // Parse the JSON array
                    JSONArray adminAccessArray = new JSONArray(adminAccessJson);

                    // Check if the username is in the JSON array
                    for (int i = 0; i < adminAccessArray.length(); i++) {
                        if (adminAccessArray.getString(i).equals(username)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false; // User is not found in the admin access list
    }
    
    // Method to add an instructor user to the group with either view or admin access
    public void addInstructor(String groupName, String instructorUsername, String accessType) throws SQLException, JSONException {
        if (!accessType.equals("view") && !accessType.equals("admin")) {
            throw new IllegalArgumentException("Invalid access type. Use 'view' or 'admin'.");
        }

        // Determine the correct column based on access type
        String column = accessType.equals("view") ? "instructors_with_view_access" : "instructors_with_admin_access";

        // Query to get the current JSON array for the specified access type
        String query = "SELECT " + column + " FROM specialaccess WHERE groupname = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String jsonArrayString = rs.getString(column);
                JSONArray jsonArray = (jsonArrayString != null && !jsonArrayString.isEmpty()) ? new JSONArray(jsonArrayString) : new JSONArray();

                // Add the instructor to the JSON array if not already present
                if (!isUserInJsonArray(jsonArray.toString(), instructorUsername)) {
                    jsonArray.put(instructorUsername);

                    // Update the database with the modified JSON array
                    String updateQuery = "UPDATE specialaccess SET " + column + " = ? WHERE groupname = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, jsonArray.toString());
                        updateStmt.setString(2, groupName);
                        updateStmt.executeUpdate();
                    }
                }
            }
        }
    }

    // Method to add a student user to the group
    public void addStudentToViewAccess(String groupName, String studentUsername) throws SQLException, JSONException {
        String query = "SELECT students_with_view_access FROM specialaccess WHERE groupname = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String jsonArrayString = rs.getString("students_with_view_access");
                JSONArray jsonArray = (jsonArrayString != null && !jsonArrayString.isEmpty()) ? new JSONArray(jsonArrayString) : new JSONArray();

                // Add the student to the JSON array if not already present
                if (!isUserInJsonArray(jsonArray.toString(), studentUsername)) {
                    jsonArray.put(studentUsername);

                    // Update the students_with_view_access field in the database
                    String updateQuery = "UPDATE specialaccess SET students_with_view_access = ? WHERE groupname = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, jsonArray.toString());
                        updateStmt.setString(2, groupName);
                        updateStmt.executeUpdate();
                    }
                }
            }
        }
    }
    
    // Method to check if an article is in the group
    public boolean isArticleInSpecialAccessGroup(int articleId, String groupName) throws SQLException {
        String query = "SELECT article_ids FROM specialaccess WHERE groupname = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, groupName);  // Set the groupname parameter

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String articleIdsJson = rs.getString("article_ids");  // Get the article_ids JSON field

                    if (articleIdsJson != null && !articleIdsJson.isEmpty()) {
                        JSONArray articleIdsArray = new JSONArray(articleIdsJson);  // Parse the JSON array
                        // Check if the article ID exists in the JSON array
                        for (int i = 0; i < articleIdsArray.length(); i++) {
                            if (articleIdsArray.getInt(i) == articleId) {
                                return true;  // If the article ID is found, return true
                            }
                        }
                    }
                }
            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();  // Log the exception
            throw e;  // Rethrow the exception for proper error handling
        }
        return false;  // Return false if the article ID is not found in the group
    }

    // Method to add an article to the group
    public void addArticleToGroup(int articleId, String groupName) throws SQLException, JSONException {
        // Step 1: Add the article ID to the specialaccess table
        String query = "SELECT article_ids FROM specialaccess WHERE groupname = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String jsonArrayString = rs.getString("article_ids");
                JSONArray jsonArray = (jsonArrayString != null && !jsonArrayString.isEmpty()) ? new JSONArray(jsonArrayString) : new JSONArray();

                // Add the article ID to the JSON array if not already present
                if (!jsonArray.toList().contains(articleId)) {
                    jsonArray.put(articleId);

                    // Update the specialaccess table with the modified JSON array
                    String updateQuery = "UPDATE specialaccess SET article_ids = ? WHERE groupname = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, jsonArray.toString());
                        updateStmt.setString(2, groupName);
                        updateStmt.executeUpdate();
                    }
                }
            }
        }

        // Step 2: Update the articles table with the group name
        String queryArticles = "SELECT specialaccessgroups FROM articles WHERE id = ?";
        try (PreparedStatement stmtArticles = connection.prepareStatement(queryArticles)) {
            stmtArticles.setInt(1, articleId);
            ResultSet rsArticles = stmtArticles.executeQuery();

            if (rsArticles.next()) {
                String jsonArrayString = rsArticles.getString("specialaccessgroups");
                JSONArray jsonArray = (jsonArrayString != null && !jsonArrayString.isEmpty()) ? new JSONArray(jsonArrayString) : new JSONArray();

                // Add the group name to the JSON array if not already present
                if (!jsonArray.toList().contains(groupName)) {
                    jsonArray.put(groupName);

                    // Update the articles table with the modified JSON array
                    String updateQuery = "UPDATE articles SET specialaccessgroups = ? WHERE id = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, jsonArray.toString());
                        updateStmt.setInt(2, articleId);
                        updateStmt.executeUpdate();
                    }
                }
            }
        }
    }
    
    // Method to check if a user has access to a special group associated with the given article
    public boolean isUserInAccessGroups(String username, long articleId) throws SQLException, JSONException {
        String query = "SELECT specialaccessgroups FROM articles WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Set parameters for the query
            stmt.setLong(1, articleId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String specialAccessGroupsJson = rs.getString("specialaccessgroups");

                    // If no special access groups, return false
                    if (specialAccessGroupsJson == null || specialAccessGroupsJson.isEmpty()) {
                        return true;
                    }

                    // Parse the special access groups into a JSON array
                    JSONArray specialAccessGroupsArray = new JSONArray(specialAccessGroupsJson);

                    // Check each special access group
                    for (int i = 0; i < specialAccessGroupsArray.length(); i++) {
                        String specialGroup = specialAccessGroupsArray.getString(i).trim();

                        // Use the helper method to check if the user has access to this group
                        if (hasUserAccessToGroups(specialGroup, username)) {
                            return true; // User has access to at least one group
                        }
                    }
                }
            }
        } catch (SQLException | JSONException e) {
            System.err.println("Error checking user access to special access groups: " + e.getMessage());
            throw e;
        }
        return false; // User does not have access to any of the groups
    }

    // Method to print a table of special access groups
    public void printSpecialAccessTable() {
    	String query = "SELECT * FROM specialaccess";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            // Print table headers
            System.out.println("ID | Group Name | Instructors With View Access | Instructors With Admin Access | Article IDs | Students With View Access");

            // Iterate through the result set and print each row
            while (rs.next()) {
                int id = rs.getInt("id");
                String groupName = rs.getString("groupname");
                String instructorsWithViewAccess = rs.getString("instructors_with_view_access");
                String instructorsWithAdminAccess = rs.getString("instructors_with_admin_access");
                String articleIds = rs.getString("article_ids");
                String studentsWithViewAccess = rs.getString("students_with_view_access");

                System.out.printf("%d | %s | %s | %s | %s | %s\n",
                        id, groupName, instructorsWithViewAccess, instructorsWithAdminAccess, articleIds, studentsWithViewAccess);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    // Method to remove articles from a specific special access group
    public boolean removeArticlesFromGroup(String groupName, List<Long> articleIdsToRemove) {
        // Convert the articleIds list to a string for easier manipulation
        String articleIdsToRemoveStr = articleIdsToRemove.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        
        String getGroupSQL = "SELECT id, article_ids FROM specialaccess WHERE groupname = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(getGroupSQL)) {
            stmt.setString(1, groupName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Get the current article_ids from the database
                    String currentArticleIds = rs.getString("article_ids");
                    
                    if (currentArticleIds != null && !currentArticleIds.isEmpty()) {
                        // Remove the articles that are no longer part of the group
                        String[] currentArticleIdArray = currentArticleIds.split(",");
                        Set<String> currentArticleIdSet = new HashSet<>(Arrays.asList(currentArticleIdArray));
                        Set<String> articleIdsToRemoveSet = new HashSet<>(Arrays.asList(articleIdsToRemoveStr.split(",")));
                        
                        // Remove the articles from the set
                        currentArticleIdSet.removeAll(articleIdsToRemoveSet);
                        
                        // Create the new article_ids string
                        String updatedArticleIds = String.join(",", currentArticleIdSet);
                        
                        // Update the database with the new article_ids
                        String updateSQL = "UPDATE specialaccess SET article_ids = ? WHERE groupname = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateSQL)) {
                            updateStmt.setString(1, updatedArticleIds);
                            updateStmt.setString(2, groupName);
                            int rowsUpdated = updateStmt.executeUpdate();
                            
                            if (rowsUpdated > 0) {
                                System.out.println("Articles removed successfully from the group.");
                                return true;
                            } else {
                                System.err.println("Error removing articles from the group.");
                                return false;
                            }
                        }
                    } else {
                        System.err.println("No articles found in this group to remove.");
                        return false;
                    }
                } else {
                    System.err.println("Group not found with the name: " + groupName);
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error removing articles from group: " + e.getMessage());
            return false;
        }
    }
    
    //Search Functions for Phase III
    
    // Method to retrieve a list of articles from the group
    public List<Long> getSpecialAccessByGroups(String groupName) throws SQLException {
   	 List<Long> articleIds = new ArrayList<>(); // List to store article IDs

   	    // SQL query to fetch article_ids where groupname matches the given value
   	    String query = "SELECT article_ids FROM specialaccess WHERE groupname = ?";

   	    System.out.println("Preparing to execute query for group: " + groupName);

   	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
   	        // Set the groupName parameter in the query
   	        stmt.setString(1, groupName);
   	        System.out.println("Query prepared: " + query);

   	        try (ResultSet rs = stmt.executeQuery()) {
   	            System.out.println("Query executed. Checking results...");

   	            while (rs.next()) {
   	                System.out.println("Row found for group: " + groupName);

   	                String articleIdsJson = rs.getString("article_ids"); // Get the JSON array
   	                System.out.println("Retrieved article_ids JSON: " + articleIdsJson);

   	                if (articleIdsJson != null && !articleIdsJson.isEmpty()) {
   	                    try {
   	                        // Parse the JSON array and add each ID to the list
   	                        JSONArray articleIdsArray = new JSONArray(articleIdsJson);
   	                        System.out.println("Parsing article_ids JSON array...");
   	                        
   	                        for (int i = 0; i < articleIdsArray.length(); i++) {
   	                            long articleId = articleIdsArray.getLong(i);
   	                            articleIds.add(articleId);
   	                            System.out.println("Article ID added: " + articleId);
   	                        }
   	                    } catch (JSONException e) {
   	                        System.err.println("Error parsing article_ids JSON for group '" + groupName + "': " + e.getMessage());
   	                    }
   	                } else {
   	                    System.out.println("No article_ids JSON found for group '" + groupName + "'.");
   	                }
   	            }
   	        }
   	        
   	        System.out.println("Finished processing results. Total article IDs found: " + articleIds.size());
   	    } catch (SQLException e) {
   	        System.err.println("Failed to retrieve article IDs for group '" + groupName + "': " + e.getMessage());
   	        throw e;
   	    }
   	    
   	    if (articleIds.isEmpty()) {
   	        System.out.println("No article IDs found for group '" + groupName + "'.");
   	    } else {
   	        System.out.println("Article IDs retrieved successfully for group '" + groupName + "'.");
   	    }
   	    return articleIds; // Return the list of article IDs
   }
    
    // Method to retrieve a list of article ids accessible to a user based on difficulty level and group access 
    public List<Long> getArticlesByDifficulty(String username, boolean beginner, boolean intermediate, boolean advanced, boolean expert) throws SQLException, JSONException {
        List<Long> accessibleArticleIds = new ArrayList<>();

        // SQL query to retrieve articles (no need to include difficulty or groups)
        String query = "SELECT a.id, a.beginner, a.intermediate, a.advanced, a.expert, a.specialaccessgroups FROM articles a";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                long articleId = rs.getLong("id");
                String specialAccessGroupsJson = rs.getString("specialaccessgroups");

                // Check for at least one required difficulty (any difficulty that matches)
                boolean hasRequiredDifficulty = (beginner && rs.getInt("beginner") == 1) ||
                                                (intermediate && rs.getInt("intermediate") == 1) ||
                                                (advanced && rs.getInt("advanced") == 1) ||
                                                (expert && rs.getInt("expert") == 1);

                boolean hasAccess = false;

                // Check access through 'specialaccessgroups' field using hasUserAccessToGroups
                if (specialAccessGroupsJson != null && !specialAccessGroupsJson.isEmpty()) {
                    JSONArray specialAccessGroupsArray = new JSONArray(specialAccessGroupsJson);

                    // Loop through each special access group to check if user has access
                    for (int i = 0; i < specialAccessGroupsArray.length(); i++) {
                        String specialGroup = specialAccessGroupsArray.getString(i).trim();
                        // Check if user has access to this group using hasUserAccessToGroups
                        if (hasUserAccessToGroups(specialGroup, username)) {
                            hasAccess = true;
                            break; // Stop checking once access is confirmed
                        }
                    }
                }
                else {
                	hasAccess = true;
                }

                // Add article if it matches at least one difficulty and has access
                if (hasRequiredDifficulty && hasAccess) {
                    accessibleArticleIds.add(articleId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving accessible articles: " + e.getMessage());
            throw e;
        }
        return accessibleArticleIds;
    }

    // Method to retrieve articles accessible to a user based on group access
    public List<Long> searchArticlesByGroups(String username, String groupNames) throws SQLException, JSONException {
        List<Long> accessibleArticleIds = new ArrayList<>();

        // Safely handle groupNames, split by comma, and use a HashSet for efficient lookups
        Set<String> groupSet = new HashSet<>();
        if (groupNames != null && !groupNames.isEmpty()) {
            for (String group : groupNames.split(",")) {
                groupSet.add(group.trim());
            }
        }

        String query = "SELECT a.id, a.groups, a.specialaccessgroups FROM articles a WHERE a.groups IS NOT NULL OR a.specialaccessgroups IS NOT NULL";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                long articleId = rs.getLong("id");
                String groupsJson = rs.getString("groups");
                String specialAccessGroupsJson = rs.getString("specialaccessgroups");

                boolean groupMatchFound = false;
                boolean specialGroupMatchFound = false;
                boolean hasSpecialAccess = false;

                // Step 1: Check if at least one of the groupNames matches the article's regular groups
                if (groupsJson != null && !groupsJson.isEmpty()) {
                    JSONArray groupsArray = new JSONArray(groupsJson);
                    for (int i = 0; i < groupsArray.length(); i++) {
                        String articleGroup = groupsArray.getString(i).trim();
                        if (groupSet.contains(articleGroup)) {
                            groupMatchFound = true;
                            break; // Stop checking if regular group matches
                        }
                    }
                }

                // Step 2: Check if at least one of the groupNames matches the article's regular groups
                if (specialAccessGroupsJson != null && !specialAccessGroupsJson.isEmpty()) {
                    JSONArray specialAccessGroupsArray = new JSONArray(specialAccessGroupsJson);
                    for (int i = 0; i < specialAccessGroupsArray.length(); i++) {
                        String specialGroup = specialAccessGroupsArray.getString(i).trim();
                        if (groupSet.contains(specialGroup)) {
                            specialGroupMatchFound = true;
                            break; // Stop checking if special access group matches
                        }
                    }
                }

                // Step 3: Check if user has access to any special access groups, if present
                if (specialAccessGroupsJson != null && !specialAccessGroupsJson.isEmpty()) {
                    JSONArray specialAccessGroupsArray = new JSONArray(specialAccessGroupsJson);
                    for (int i = 0; i < specialAccessGroupsArray.length(); i++) {
                        String specialGroup = specialAccessGroupsArray.getString(i).trim();

                        // If user has access to any special access group, mark as having access
                        if (hasUserAccessToGroups(specialGroup, username)) {
                            hasSpecialAccess = true;
                            break; // Stop checking once access is confirmed
                        }
                    }
                }

                // Step 4: Add article if there is a regular group or special access group match and the user has access to special access groups if present
                if ((groupMatchFound || specialGroupMatchFound) && (specialAccessGroupsJson == null || hasSpecialAccess)) {
                    accessibleArticleIds.add(articleId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving articles by group(s): " + e.getMessage());
            throw e;
        }
        return accessibleArticleIds;
    }

    // Method to retrieve articles based on keyword search in title or abstract given the user has group access
    public List<Long> searchArticlesByKeywordWithAccess(String username, String searchQuery) throws SQLException, JSONException {
        List<Long> accessibleArticleIds = new ArrayList<>();
        String query = "SELECT id, title, abstract, specialaccessgroups FROM articles WHERE LOWER(title) LIKE ? OR LOWER(abstract) LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String keywordPattern = "%" + searchQuery.toLowerCase().trim() + "%";

            // Set parameters for the query
            stmt.setString(1, keywordPattern); // For title
            stmt.setString(2, keywordPattern); // For abstract

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    long articleId = rs.getLong("id");
                    String specialAccessGroupsJson = rs.getString("specialaccessgroups");

                    // Check access via specialaccessgroups only
                    boolean hasAccess = false;
                    if (specialAccessGroupsJson != null && !specialAccessGroupsJson.isEmpty()) {
                        JSONArray specialAccessGroupsArray = new JSONArray(specialAccessGroupsJson);
                        for (int i = 0; i < specialAccessGroupsArray.length(); i++) {
                            String specialGroup = specialAccessGroupsArray.getString(i).trim();

                            // If user has access to any special access group, mark as having access
                            if (hasUserAccessToGroups(specialGroup, username)) {
                                hasAccess = true;
                                break; // Stop checking once access is confirmed
                            }
                        }
                    }
                    else {
                    	hasAccess = true;
                    }

                    // Add article if the user has access
                    if (hasAccess) {
                        accessibleArticleIds.add(articleId);
                    }
                }
            }
        } catch (SQLException | JSONException e) {
            System.err.println("Error during article search: " + e.getMessage());
            throw e;
        }
        return accessibleArticleIds;
    }
    
    // Method to check if student exist in the database for deleting
    public boolean doesStudentExist(String studentIdentifier) throws SQLException {
        String query = "SELECT 1 FROM cse360users WHERE username = ? OR email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, studentIdentifier);
            stmt.setString(2, studentIdentifier);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
    
    // Method to remove a student from the database
    public boolean deleteStudent(String studentIdentifier) throws SQLException {
        String query = "DELETE FROM cse360users WHERE username = ? OR email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, studentIdentifier);
            stmt.setString(2, studentIdentifier);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Helper method to check if user has access to the special access groups
    private boolean hasUserAccessToGroups(String group, String username) throws SQLException, JSONException {
    	return (isUserInGroup(group, username) || isStudentInGroup(group, username));
    }
    
    // Method to access list of all users in provided group
    public List<User> getAllUsersInGroup(String groupName) throws SQLException, JSONException {
        String query = "SELECT instructors_with_view_access, instructors_with_admin_access, students_with_view_access FROM specialaccess WHERE groupname = ?";
        List<User> users = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, groupName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Parse and add instructors with view access
                    String viewAccessJson = resultSet.getString("instructors_with_view_access");
                    if (viewAccessJson != null && !viewAccessJson.isEmpty()) {
                        JSONArray viewAccessArray = new JSONArray(viewAccessJson);
                        for (int i = 0; i < viewAccessArray.length(); i++) {
                            String username = viewAccessArray.getString(i);
                            User user = getUserByUsername(username); // Get user data (you may need to define this method)
                            if (user != null) {
                                users.add(user);  // Add User object to the list
                            }
                        }
                    }

                    // Parse and add instructors with admin access
                    String adminAccessJson = resultSet.getString("instructors_with_admin_access");
                    if (adminAccessJson != null && !adminAccessJson.isEmpty()) {
                        JSONArray adminAccessArray = new JSONArray(adminAccessJson);
                        for (int i = 0; i < adminAccessArray.length(); i++) {
                            String username = adminAccessArray.getString(i);
                            User user = getUserByUsername(username); // Get user data
                            if (user != null) {
                                users.add(user);  // Add User object to the list
                            }
                        }
                    }

                    // Parse and add students with view access
                    String studentAccessJson = resultSet.getString("students_with_view_access");
                    if (studentAccessJson != null && !studentAccessJson.isEmpty()) {
                        JSONArray studentAccessArray = new JSONArray(studentAccessJson);
                        for (int i = 0; i < studentAccessArray.length(); i++) {
                            String username = studentAccessArray.getString(i);
                            User user = getUserByUsername(username); // Get user data
                            if (user != null) {
                                users.add(user);  // Add User object to the list
                            }
                        }
                    }
                }
            }
        }
        return users;
    }
    
    // Method to access list of students in provided group
    public List<User> getStudentsInGroup(String groupName) throws SQLException, JSONException {
        String query = "SELECT students_with_view_access FROM specialaccess WHERE groupname = ?";
        List<User> students = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, groupName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Parse and add students with view access
                    String studentAccessJson = resultSet.getString("students_with_view_access");
                    if (studentAccessJson != null && !studentAccessJson.isEmpty()) {
                        JSONArray studentAccessArray = new JSONArray(studentAccessJson);
                        for (int i = 0; i < studentAccessArray.length(); i++) {
                            String username = studentAccessArray.getString(i);
                            User user = getUserByUsername(username); // Get user data
                            if (user != null) {
                                students.add(user);  // Add User object to the list
                            }
                        }
                    }
                }
            }
        }
        return students;
    }
    
    // Method to access list of students
    public List<User> getAllStudents() throws SQLException {
        String query = "SELECT * FROM cse360users WHERE student = 1";
        List<User> students = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                // Extract data from the result set
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String firstname = resultSet.getString("firstname");
                String middlename = resultSet.getString("middlename");
                String lastname = resultSet.getString("lastname");
                String preferred = resultSet.getString("preferred");
                boolean isAdmin = resultSet.getInt("admin") == 1;
                boolean isInstructor = resultSet.getInt("instructor") == 1;
                boolean isStudent = resultSet.getInt("student") == 1;
                boolean isFlagged = resultSet.getInt("flag") == 1;

                // Create a new User object
                User student = new User(
                    username,
                    email,
                    firstname,
                    middlename,
                    lastname,
                    preferred,
                    isAdmin,
                    isInstructor,
                    isStudent,
                    isFlagged
                );

                // Add the user to the list
                students.add(student);
            }
        }
        return students;
    }
    
    // Method to access list of students
    public List<User> getStudent(String username) throws SQLException {
        String query = "SELECT * FROM cse360users WHERE student = 1 AND username = ?";
        List<User> students = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Bind the username parameter
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Extract data from the result set
                    String uname = resultSet.getString("username");
                    String email = resultSet.getString("email");
                    String firstname = resultSet.getString("firstname");
                    String middlename = resultSet.getString("middlename");
                    String lastname = resultSet.getString("lastname");
                    String preferred = resultSet.getString("preferred");
                    boolean isAdmin = resultSet.getInt("admin") == 1;
                    boolean isInstructor = resultSet.getInt("instructor") == 1;
                    boolean isStudent = resultSet.getInt("student") == 1;
                    boolean isFlagged = resultSet.getInt("flag") == 1;

                    // Create a new User object
                    User student = new User(
                        uname,
                        email,
                        firstname,
                        middlename,
                        lastname,
                        preferred,
                        isAdmin,
                        isInstructor,
                        isStudent,
                        isFlagged
                    );

                    // Add the user to the list
                    students.add(student);
                }
            }
        }
        return students;
    }
    
    // Checks if a user is the last admin in a group
    boolean isLastAdminInGroup(String groupName, String username) throws SQLException, JSONException {
        System.out.println("Checking if user " + username + " is the last admin in group " + groupName);
        String query = "SELECT instructors_with_admin_access FROM specialaccess WHERE groupname = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String adminAccessJson = rs.getString("instructors_with_admin_access");
                JSONArray adminAccessArray = new JSONArray(adminAccessJson != null ? adminAccessJson : "[]");
                System.out.println("Admin access list for group " + groupName + ": " + adminAccessArray);

                // Check if the array contains only one user, and it is the specified user
                return adminAccessArray.length() == 1 && adminAccessArray.getString(0).equals(username);
            }
        }
        System.out.println("No admins or multiple admins exist in the group.");
        return false;
    }

    // Deletes a user (admin or student) from a group
    public boolean deleteUserFromGroup(String groupName, String username) throws SQLException, JSONException {
        System.out.println("Attempting to delete user " + username + " from group " + groupName);

        // Prevent deletion if the user is the last admin
        if (isLastAdminInGroup(groupName, username)) {
            System.out.println("Cannot delete user " + username + " as they are the last admin in the group.");
            return false;
        }

        String query = "SELECT instructors_with_admin_access, instructors_with_view_access, students_with_view_access FROM specialaccess WHERE groupname = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Handle admin access array
                String adminAccessJson = rs.getString("instructors_with_admin_access");
                JSONArray adminAccessArray = new JSONArray(adminAccessJson != null ? adminAccessJson : "[]");
                System.out.println("Original admin access list: " + adminAccessArray);
                removeUserFromArray(adminAccessArray, username);
                System.out.println("Updated admin access list: " + adminAccessArray);

                // Handle instructor view access array
                String viewAccessJson = rs.getString("instructors_with_view_access");
                JSONArray viewAccessArray = new JSONArray(viewAccessJson != null ? viewAccessJson : "[]");
                System.out.println("Original view access list: " + viewAccessArray);
                removeUserFromArray(viewAccessArray, username);
                System.out.println("Updated view access list: " + viewAccessArray);

                // Handle student view access array
                String studentAccessJson = rs.getString("students_with_view_access");
                JSONArray studentAccessArray = new JSONArray(studentAccessJson != null ? studentAccessJson : "[]");
                System.out.println("Original student access list: " + studentAccessArray);
                removeUserFromArray(studentAccessArray, username);
                System.out.println("Updated student access list: " + studentAccessArray);

                // Update the database
                String updateQuery = "UPDATE specialaccess SET "
                        + "instructors_with_admin_access = ?, "
                        + "instructors_with_view_access = ?, "
                        + "students_with_view_access = ? "
                        + "WHERE groupname = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, adminAccessArray.toString());
                    updateStmt.setString(2, viewAccessArray.toString());
                    updateStmt.setString(3, studentAccessArray.toString());
                    updateStmt.setString(4, groupName);
                    updateStmt.executeUpdate();
                }
            }
        }
        System.out.println("User " + username + " deleted successfully from group " + groupName);
        return true;
    }

    // Helper method to remove a user from a JSON array
    private void removeUserFromArray(JSONArray jsonArray, String username) {
        System.out.println("Removing user " + username + " from array: " + jsonArray);
        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.getString(i).equals(username)) {
                jsonArray.remove(i);
                System.out.println("User " + username + " removed.");
                break;
            }
        }
    }

    // Deletes an article from a special access group
    public boolean deleteArticleFromSpecialAccessGroup(String groupName, int articleId) throws SQLException, JSONException {
        System.out.println("Attempting to delete article " + articleId + " from group " + groupName);

        if (!doesSpecialGroupExist(groupName)) {
            System.out.println("Group " + groupName + " does not exist.");
            return false;
        }

        String query = "SELECT article_ids FROM specialaccess WHERE groupname = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String articleIdsJson = rs.getString("article_ids");
                JSONArray articleIdsArray = (articleIdsJson != null && !articleIdsJson.isEmpty()) 
                                              ? new JSONArray(articleIdsJson) 
                                              : new JSONArray();
                System.out.println("Original article IDs for group " + groupName + ": " + articleIdsArray);

                boolean removed = removeArticleFromArray(articleIdsArray, articleId);
                if (!removed) {
                    System.out.println("Article ID " + articleId + " not found in group " + groupName);
                    return false;
                }

                System.out.println("Updated article IDs for group " + groupName + ": " + articleIdsArray);

                String updateQuery = "UPDATE specialaccess SET article_ids = ? WHERE groupname = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, articleIdsArray.toString());
                    updateStmt.setString(2, groupName);
                    updateStmt.executeUpdate();
                }

                String articleQuery = "SELECT specialaccessgroups FROM articles WHERE id = ?";
                try (PreparedStatement articleStmt = connection.prepareStatement(articleQuery)) {
                    articleStmt.setInt(1, articleId);
                    ResultSet articleRs = articleStmt.executeQuery();

                    if (articleRs.next()) {
                        String groupNamesJson = articleRs.getString("specialaccessgroups");
                        JSONArray groupNamesArray = (groupNamesJson != null && !groupNamesJson.isEmpty()) 
                                                      ? new JSONArray(groupNamesJson) 
                                                      : new JSONArray();
                        System.out.println("Original group names for article " + articleId + ": " + groupNamesArray);

                        boolean groupRemoved = removeGroupFromArray(groupNamesArray, groupName);
                        if (groupRemoved) {
                            System.out.println("Updated group names for article " + articleId + ": " + groupNamesArray);
                            String updateArticleQuery = "UPDATE articles SET specialaccessgroups = ? WHERE id = ?";
                            try (PreparedStatement updateArticleStmt = connection.prepareStatement(updateArticleQuery)) {
                                updateArticleStmt.setString(1, groupNamesArray.toString());
                                updateArticleStmt.setInt(2, articleId);
                                updateArticleStmt.executeUpdate();
                            }
                        }
                    }
                }

                System.out.println("Article " + articleId + " successfully removed from group " + groupName);
                return true;
            }
        } catch (SQLException | JSONException e) {
            System.err.println("Error while deleting article from group: " + e.getMessage());
            throw e;
        }
        return false;
    }

    // Helper method to remove an article ID from a JSON array
    private boolean removeArticleFromArray(JSONArray jsonArray, int articleId) {
        System.out.println("Removing article ID " + articleId + " from array: " + jsonArray);
        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.getInt(i) == articleId) {
                jsonArray.remove(i);
                System.out.println("Article ID " + articleId + " removed.");
                return true;
            }
        }
        System.out.println("Article ID " + articleId + " not found.");
        return false;
    }

    // Helper method to remove a group name from a JSON array
    private boolean removeGroupFromArray(JSONArray jsonArray, String groupName) {
        System.out.println("Removing group name " + groupName + " from array: " + jsonArray);
        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.getString(i).equals(groupName)) {
                jsonArray.remove(i);
                System.out.println("Group name " + groupName + " removed.");
                return true;
            }
        }
        System.out.println("Group name " + groupName + " not found.");
        return false;
    }
    
    // M<ethod to delete a special access group
    public void deleteSpecialAccessGroup(String groupName) throws SQLException {
    	if (!doesSpecialGroupExist(groupName)) {
            throw new SQLException("Invalid group name.");
        }
    	String query = "DELETE FROM specialaccess WHERE groupname = ?";
    	try (PreparedStatement stmt = connection.prepareStatement(query)) {
    		stmt.setString(1, groupName);
    		stmt.executeUpdate();
    		System.out.println(groupName + "deleted successfully.");
    	} catch (SQLException e) {
    		System.err.println("Error deleting special access group:" + e.getMessage());
    	}	
    }
    
    // Method to delete group
    public void deleteGroup(String groupName) throws SQLException {
        String query = "SELECT id, groups FROM articles";
        String updateQuery = "UPDATE articles SET groups = ? WHERE id = ?";
        
        try (Statement selectStmt = connection.createStatement();
             ResultSet rs = selectStmt.executeQuery(query)) {

            System.out.println("Executing query to fetch articles...");

            while (rs.next()) {
                long articleId = rs.getLong("id");
                String groupsJson = rs.getString("groups");

                System.out.println("Processing article ID: " + articleId);
                if (groupsJson != null && !groupsJson.isEmpty()) {
                    System.out.println("Original groups JSON: " + groupsJson);

                    JSONArray groupsArray = new JSONArray(groupsJson);
                    JSONArray updatedGroupsArray = new JSONArray();

                    // Keep all groups except the one to remove
                    for (int i = 0; i < groupsArray.length(); i++) {
                        String group = groupsArray.getString(i);
                        if (!group.equalsIgnoreCase(groupName)) {
                            updatedGroupsArray.put(group);
                        } else {
                            System.out.println("Removing group: " + group);
                        }
                    }

                    System.out.println("Updated groups JSON for article ID " + articleId + ": " + updatedGroupsArray);

                    // Update the database with the new array
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, updatedGroupsArray.toString());
                        updateStmt.setLong(2, articleId);
                        int rowsUpdated = updateStmt.executeUpdate();
                        System.out.println("Article ID " + articleId + " updated. Rows affected: " + rowsUpdated);
                    }
                } else {
                    System.out.println("No groups found for article ID: " + articleId);
                }
            }
        } catch (JSONException e) {
            System.err.println("Error processing groups: " + e.getMessage());
        }
    }
    
    // Method to check if a group exists in the Articles table
    public boolean doesGroupExist(String groupName) throws SQLException {
        // Query to check if the group name exists in the 'groups' JSON array
        String query = "SELECT COUNT(*) FROM articles, json_each(groups) WHERE json_each.value = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, groupName); // Directly bind the group name for comparison

            System.out.println("Executing query to check if group exists: " + groupName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    System.out.println("Group existence count: " + count);
                    return count > 0; // Return true if the count is greater than 0
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking group existence: " + e.getMessage());
            throw e; // Re-throw the exception for further handling
        }
        return false; // Return false if no matches are found
    }

    // Method to add a specific message from the user to the requests table
    public void addSpecificMessage(String username, String specificText, String specificNeed) throws SQLException {
        String insertRequest = "INSERT INTO requests (username, request) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertRequest)) {
            // Combine the inputs into a single message
            String combinedMessage = "Not Found: " + specificText + "; Needed: " + specificNeed;

            // Set parameters for the query
            pstmt.setString(1, username); // The username of the student
            pstmt.setString(2, combinedMessage); // The combined message

            // Execute the query
            pstmt.executeUpdate();
            System.out.println("Specific message added to the database for user: " + username);
        } catch (SQLException e) {
            System.err.println("Error while adding specific message: " + e.getMessage());
            throw e; // Rethrow for higher-level handling
        }
    }
    
    // Method to add a generic help message from the user to the requests table
    public void addGenericMessage(String username) throws SQLException {
        String insertRequest = "INSERT INTO requests (username, request) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertRequest)) {
            // Combine the inputs into a single message
            String Message = "Please Help!";

            // Set parameters for the query
            pstmt.setString(1, username); // The username of the student
            pstmt.setString(2, Message); // The combined message

            // Execute the query
            pstmt.executeUpdate();
            System.out.println("Generic message added to the database for user: " + username);
        } catch (SQLException e) {
            System.err.println("Error while adding generic message: " + e.getMessage());
            throw e; // Rethrow for higher-level handling
        }
    }
    
    /**
     * Closes the database connection and associated statement.
     * Should be called when the application is shutting down to release resources.
     */
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
