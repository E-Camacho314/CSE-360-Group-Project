package cse360helpsystem;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

/**
* <p>User Class</p>
* 
* <p>Description: This class represents a user in the CSE360HelpSystem application.
 * It contains properties related to the user's personal information and roles within the system.
 * JavaFX properties are used for easy binding with UI components. </p>
* 
* <p>Authors: Erik Camacho, Thienban Nguyen, Sarvesh Shanmugam, Ivan Mancillas, Tanis Peterson</p>
*/

public class User {
	// User properties
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty firstname = new SimpleStringProperty();
    private final StringProperty middlename = new SimpleStringProperty();
    private final StringProperty lastname = new SimpleStringProperty();
    private final StringProperty preferred = new SimpleStringProperty();
    private final BooleanProperty isAdmin = new SimpleBooleanProperty();
    private final BooleanProperty isInstructor = new SimpleBooleanProperty();
    private final BooleanProperty isStudent = new SimpleBooleanProperty();
    private final BooleanProperty flag = new SimpleBooleanProperty();

    /**
     * Constructor for the User class.
     * Initializes user properties based on provided values.
     * 
     * @param username     The username of the user.
     * @param email        The email address of the user.
     * @param firstname    The first name of the user.
     * @param middlename   The middle name of the user.
     * @param lastname     The last name of the user.
     * @param preferred    The preferred name of the user.
     * @param isAdmin      Whether the user is an admin.
     * @param isInstructor  Whether the user is an instructor.
     * @param isStudent     Whether the user is a student.
     * @param isFlagged     Whether the user is flagged.
     */
    public User(String username, String email, String firstname, String middlename, String lastname, String preferred,
                boolean isAdmin, boolean isInstructor, boolean isStudent, boolean isFlagged) {
    	// Initialize properties using the setter methods for proper binding functionality
        setUsername(username);
        setEmail(email);
        setFirstname(firstname);
        setMiddlename(middlename);
        setLastname(lastname);
        setPreferred(preferred);
        setIsAdmin(isAdmin);
        setIsInstructor(isInstructor);
        setIsStudent(isStudent);
        setFlag(isFlagged);
    }

	// Getters for retrieving property values
    public String getUsername() {
        return username.get();
    }

    public String getEmail() {
        return email.get();
    }

    public String getFirstname() {
        return firstname.get();
    }

    public String getMiddlename() {
        return middlename.get();
    }

    public String getLastname() {
        return lastname.get();
    }

    public String getPreferred() {
        return preferred.get();
    }

    public boolean isAdmin() {
        return isAdmin.get();
    }

    public boolean isInstructor() {
        return isInstructor.get();
    }

    public boolean isStudent() {
        return isStudent.get();
    }

    public boolean isFlagged() {
        return flag.get();
    }

    // Setters for updating property values
    public void setUsername(String value) {
        username.set(value);
    }

    public void setEmail(String value) {
        email.set(value);
    }

    public void setFirstname(String value) {
        firstname.set(value);
    }

    public void setMiddlename(String value) {
        middlename.set(value);
    }

    public void setLastname(String value) {
        lastname.set(value);
    }

    public void setPreferred(String value) {
        preferred.set(value);
    }

    public void setIsAdmin(boolean value) {
        isAdmin.set(value);
    }

    public void setIsInstructor(boolean value) {
        isInstructor.set(value);
    }

    public void setIsStudent(boolean value) {
        isStudent.set(value);
    }

    public void setFlag(boolean value) {
        flag.set(value);
    }

    // Properties for binding with UI components
    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty firstnameProperty() {
        return firstname;
    }

    public StringProperty middlenameProperty() {
        return middlename;
    }

    public StringProperty lastnameProperty() {
        return lastname;
    }

    public StringProperty preferredProperty() {
        return preferred;
    }

    public BooleanProperty isAdminProperty() {
        return isAdmin;
    }

    public BooleanProperty isInstructorProperty() {
        return isInstructor;
    }

    public BooleanProperty isStudentProperty() {
        return isStudent;
    }

    public BooleanProperty flagProperty() {
        return flag;
    }
}
