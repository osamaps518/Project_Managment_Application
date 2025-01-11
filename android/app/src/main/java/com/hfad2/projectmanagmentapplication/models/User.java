package com.hfad2.projectmanagmentapplication.models;

import java.util.Date;
import java.util.UUID;

public class User {
    private String userId;
    public String username;
    private String email;
    // Using String for password temporarily - in practice, we'd use proper password hashing
    private String password;
    private String fullName;
    private String profileImage;
    private Date lastLogin;
    private String userType;
    private boolean isActive;

    // Constructor with essential fields
    public User(String email, String fullName) {
        this.userId = UUID.randomUUID().toString(); // Generate unique ID
        this.email = email;
        this.fullName = fullName;
        this.isActive = true;
        this.lastLogin = new Date();
    }
    // Add this constructor
    public User(String userId, String email, String fullName, String username, String profileImage) {
        this.userId = userId;  // Use provided ID instead of generating
        this.email = email;
        this.fullName = fullName;
        this.profileImage = profileImage;
        this.username = username;
        this.isActive = true;
        this.lastLogin = new Date();
    }

    public String getUsername() {
        return username;
    }

    // Full constructor
    public User(String userId, String email, String password, String fullName,
                String profileImage, Date lastLogin, boolean isActive) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.profileImage = profileImage;
        this.lastLogin = lastLogin;
        this.isActive = isActive;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Authentication
    public boolean authenticate(String providedPassword) {
        //TODO this need to use proper password hashing
        return this.password.equals(providedPassword);
    }

    // Profile update
    public void updateProfile(String fullName, String profileImage) {
        this.fullName = fullName;
        this.profileImage = profileImage;
    }

    public String getProfileImage() {
        return profileImage;
    }
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    @Override
    public String toString() {
        return this.getFullName() + " (" + this.getEmail() + ")";
    }

    public String getUserType() {
        return userType;
    }
}