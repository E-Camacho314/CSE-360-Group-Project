package cse360helpsystem;

public class User {
    private String email;
    private String admin;
    private String instructor;
    private String student;
    public User(String email, String admin, String instructor, String student) {
        this.email = email;
        this.admin = admin;
        this.instructor = instructor;
        this.student = student;
    }
    public String getEmail() {
        return email;
    }
    public String getAdmin() {
        return admin;
    }
    public String getInstructor() {
        return instructor;
    }
    public String getStudent() {
        return student;
    }
}