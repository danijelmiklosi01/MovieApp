package com.example.movieappproject;

public class User {
    private String fullName;
    private String email;
    private String username;


    public User(){}

    public User(String fullName, String email, String username) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
