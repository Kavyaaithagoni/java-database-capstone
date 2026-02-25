package com.yourpackage.dto;  // change package as needed

public class Login {

    private String identifier;  // email (Doctor/Patient) or username (Admin)
    private String password;

    // Default constructor
    public Login() {
    }

    // Getters
    public String getIdentifier() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}