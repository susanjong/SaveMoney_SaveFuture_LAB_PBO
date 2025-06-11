package com.finance.finance_lab_pbo.model;

import java.sql.Timestamp;

/**
 * User model class representing a user in the system
 */
public class User {
    private int id;
    private String username;
    private String email;
    private String fullName;
    private String bio;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Default constructor
    public User() {}
    
    // Constructor for database retrieval
    public User(int id, String username, String email, String fullName, String bio, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.bio = bio;
        this.createdAt = createdAt;
    }
    
    // Constructor for new user creation
    public User(String username, String email, String fullName) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.bio = "";
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
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
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Utility methods
    public String getDisplayName() {
        if (fullName != null && !fullName.trim().isEmpty()) {
            return fullName;
        }
        return username;
    }
    
    public boolean hasBio() {
        return bio != null && !bio.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', email='%s', fullName='%s', hasBio=%b}", 
                           id, username, email, fullName, hasBio());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        User user = (User) obj;
        return id == user.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}