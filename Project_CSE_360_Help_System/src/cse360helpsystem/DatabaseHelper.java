package cse360helpsystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.util.Base64;

import org.bouncycastle.util.Arrays;

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
                + "groups TEXT, "
                + "access TEXT, "
                + "abstract TEXT, "
                + "keywords TEXT, "
                + "body TEXT NOT NULL, "
                + "ref_list TEXT"
                + ");";
        statement.execute(articleTable);
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
                + "groups TEXT, "
                + "access TEXT, "
                + "abstract TEXT, "
                + "keywords TEXT, "
                + "body TEXT NOT NULL, "
                + "ref_list TEXT"
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
         String query = "SELECT title, headers, groups, access, abstract, keywords, body, ref_list FROM articles";
         
         // creates a filewriter and buffered writer
         try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
              ResultSet resultSet = statement.executeQuery(query)) {

         	// sets the information next to their corresponding subtitle
             while (resultSet.next()) {
                 writer.write("Title: " + resultSet.getString("title") + "\n");
                 writer.write("Headers: " + resultSet.getString("headers") + "\n");
                 writer.write("Groups: " + resultSet.getString("groups") + "\n");
                 writer.write("Access: " + resultSet.getString("access") + "\n");
                 writer.write("Abstract: " + resultSet.getString("abstract") + "\n");
                 writer.write("Keywords: " + resultSet.getString("keywords") + "\n");
                 writer.write("Body: " + resultSet.getString("body") + "\n");
                 writer.write("References: " + resultSet.getString("ref_list") + "\n");
                 writer.write("\n");  // Blank line between articles
             }
             
             System.out.println("Articles backup completed successfully.");
             return true;
             
         } catch (SQLException | IOException e) {
             System.err.println("Error while backing up articles: " + e.getMessage());
             return false;
         }
     }

     // places information from the user specified file into the articles table
     public boolean restore(String filePath) throws Exception {
         // Recreate the articles table if it doesn't exist
         String createArticlesTableSQL = "CREATE TABLE IF NOT EXISTS articles ("
                 + "id INTEGER PRIMARY KEY AUTOINCREMENT, "  // Ensures id is a unique long integer
                 + "title TEXT NOT NULL, "
                 + "headers TEXT, "  // Added commas between columns
                 + "groups TEXT, "
                 + "access TEXT, "
                 + "abstract TEXT, "
                 + "keywords TEXT, "
                 + "body TEXT NOT NULL, "
                 + "ref_list TEXT"
                 + ");";
         
         try {
             statement.execute(createArticlesTableSQL);
         } catch (SQLException e) {
             System.err.println("Error creating articles table: " + e.getMessage());
             return false;
         }
         // insert the information into the table
         String insertSQL = "INSERT INTO articles (title, headers, groups, access, abstract, keywords, body, ref_list) VALUES (?, ?, ?, ?, ?, ?)";

         try (BufferedReader br = new BufferedReader(new FileReader(filePath));
              PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

             String line;
             String title = "", authors = "", abstractText = "", keywords = "", body = "", references = "";
             // parses off the subtitles and grabs the necessary information
             while ((line = br.readLine()) != null) {
                 if (line.startsWith("Title: ")) {
                     title = line.substring(7).trim();
                 } else if (line.startsWith("Authors: ")) {
                     authors = line.substring(9).trim();
                 } else if (line.startsWith("Abstract: ")) {
                     abstractText = line.substring(10).trim();
                 } else if (line.startsWith("Keywords: ")) {
                     keywords = line.substring(10).trim();
                 } else if (line.startsWith("Body: ")) {
                     body = line.substring(6).trim();
                 } else if (line.startsWith("References: ")) {
                     references = line.substring(11).trim();
                 } else if (line.isEmpty()) {
                     // Blank line indicates end of one article, insert into database
                     preparedStatement.setString(1, title);
                     preparedStatement.setString(2, authors);
                     preparedStatement.setString(3, abstractText);
                     preparedStatement.setString(4, keywords);
                     preparedStatement.setString(5, body);
                     preparedStatement.setString(6, references);
                     preparedStatement.addBatch();  // Add to batch for performance
                     
                     // Clear the variables for the next article
                     title = "";
                     authors = "";
                     abstractText = "";
                     keywords = "";
                     body = "";
                     references = "";
                 }
             }

             // Execute the batch insertion
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
     
     // Method to retrieve a limited list of articles (title)
     public void getAllArticlesLimited() throws SQLException {
    	    String query = "SELECT * FROM articles";
    	    try (Statement stmt = connection.createStatement();
    	         ResultSet rs = stmt.executeQuery(query)) {
    	        while (rs.next()) {
    	            long id = rs.getLong("id"); // Retrieve the article id
    	            String title = rs.getString("title"); // Retrieve the article title
    	            String abstractText = rs.getString("abstract"); // Retrieve the article abstract
    	            // Format the output to include id, title, and abstract
    	            System.out.println("ID: " + id + ", Title: " + title + ", Abstract: " + abstractText);
    	        }
    	    }
    	}

 	
     // Method to retrieve detailed information about a specific article
     public String getArticleDetailsById(long id) throws Exception {
         String query = "SELECT * FROM articles WHERE id = ?";
         StringBuilder articleDetails = new StringBuilder();
         
         try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
             preparedStatement.setLong(1, id);
             
             try (ResultSet rs = preparedStatement.executeQuery()) {
                 if (rs.next()) {
                     // Assuming you have the corresponding decrypted fields ready to use
                     String title = rs.getString("title");
                     String headers = rs.getString("headers");
                     String groups = rs.getString("groups");
                     String access = rs.getString("access");
                     String abstractText = rs.getString("abstract");
                     String keywords = rs.getString("keywords");
                     String body = rs.getString("body");
                     String references = rs.getString("ref_list");
                     
                     // Build the detailed information string
                     articleDetails.append("Article Details:\n")
                         .append("Title: ").append(title).append("\n")
                         .append("Headers: ").append(new String(headers)).append("\n")
                         .append("Groups: ").append(new String(groups)).append("\n")
                         .append("Access: ").append(new String(access)).append("\n")
                         .append("Abstract: ").append(new String(abstractText)).append("\n")
                         .append("Keywords: ").append(new String(keywords)).append("\n")
                         .append("Body: ").append(new String(body)).append("\n")
                         .append("References: ").append(new String(references)).append("\n");
                     
                 } else {
                     articleDetails.append("No article found with ID: ").append(id);
                 }
             }
         } catch (SQLException e) {
             System.err.println("Failed to retrieve article details: " + e.getMessage());
             return "Error retrieving article details: " + e.getMessage();
         }
         
         return articleDetails.toString();
     }

     public boolean insertArticle(String title, String headers, String groups, boolean admin, boolean instructor, boolean student, String abstractText, String keywords, String body, String references) throws Exception {
    	    // Create the articles table if it does not exist already
    	    String createArticlesTableSQL = "CREATE TABLE IF NOT EXISTS articles ("
    	            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
    	            + "title TEXT NOT NULL, "
    	            + "headers TEXT, "
    	            + "groups TEXT, "
    	            + "access TEXT, "
    	            + "abstract TEXT, "
    	            + "keywords TEXT, "
    	            + "body TEXT NOT NULL, "
    	            + "ref_list TEXT"
    	            + ");";
    	    try {
    	        statement.execute(createArticlesTableSQL);
    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	    }

    	    // Print original values for debugging purposes
    	    System.out.println("Original title: " + title);
    	    System.out.println("Original headers: " + headers);
    	    System.out.println("Original groups: " + groups);
    	    System.out.println("Original keywords: " + keywords);
    	    System.out.println("Original body: " + body);
    	    System.out.println("Original references: " + references);

    	    // Convert boolean values into an access string
    	    String access = "admin:" + (admin ? "1" : "0") + ","
    	                  + "instructor:" + (instructor ? "1" : "0") + ","
    	                  + "student:" + (student ? "1" : "0");

    	    // Print access string for debugging purposes
    	    System.out.println("Access string: " + access);

    	    // SQL query to insert the article into the database
    	    String insertSQL = "INSERT INTO articles (title, headers, groups, access, abstract, keywords, body, ref_list) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    	    try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
    	        // Set parameters for insertion
    	        preparedStatement.setString(1, title);
    	        preparedStatement.setString(2, headers);
    	        preparedStatement.setString(3, groups);
    	        preparedStatement.setString(4, access);
    	        preparedStatement.setString(5, abstractText);
    	        preparedStatement.setString(6, keywords);
    	        preparedStatement.setString(7, body);
    	        preparedStatement.setString(8, references);

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
