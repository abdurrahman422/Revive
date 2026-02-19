package com.revive.medicinereminder;

public class User {

    private String username;
    private String email;
    private String password;

    // Default constructor (Firebase Firestore requires an empty constructor)
    public User() {
    }

    // Constructor to initialize the user object
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getter and Setter methods for each field
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
