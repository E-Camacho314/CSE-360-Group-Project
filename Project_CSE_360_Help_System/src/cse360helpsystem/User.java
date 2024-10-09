package cse360helpsystem;

public class User {
	private String username;
    private String email;
    private boolean isAdmin;
    private boolean isInstructor;
    private boolean isStudent;
    
    public User(String username, String email, boolean isAdmin, boolean isInstructor, boolean isStudent) {
    	this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;
        this.isInstructor = isInstructor;
        this.isStudent = isStudent;
    }
    
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public boolean isAdmin() {
        return isAdmin;
    }
    public boolean isInstructor() {
        return isInstructor;
    }
    public boolean isStudent() {
        return isStudent;
    }
}