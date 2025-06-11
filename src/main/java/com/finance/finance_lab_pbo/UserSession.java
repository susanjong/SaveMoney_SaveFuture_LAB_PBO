package com.finance.finance_lab_pbo;

/**
 * UserSession class to manage the currently logged-in user
 * This class maintains the session state across different controllers
 */
public class UserSession {
    private static int currentUserId = -1;
    private static String currentUsername = "";
    private static String currentEmail = "";
    private static String currentFullName = "";
    
    /**
     * Set the current user session
     * @param userId User ID
     * @param username Username
     * @param email User email
     * @param fullName User's full name
     */
    public static void setCurrentUser(int userId, String username, String email, String fullName) {
        currentUserId = userId;
        currentUsername = username;
        currentEmail = email;
        currentFullName = fullName;
        
        System.out.println("User session set for: " + username + " (ID: " + userId + ")");
    }
    
    /**
     * Set the current user session with minimal info
     * @param userId User ID
     * @param username Username
     * @param email User email
     */
    public static void setCurrentUser(int userId, String username, String email) {
        setCurrentUser(userId, username, email, "");
    }
    
    /**
     * Get current user ID
     * @return Current user ID, -1 if no user is logged in
     */
    public static int getCurrentUserId() {
        return currentUserId;
    }
    
    /**
     * Get current username
     * @return Current username, empty string if no user is logged in
     */
    public static String getCurrentUsername() {
        return currentUsername;
    }
    
    /**
     * Get current user email
     * @return Current user email, empty string if no user is logged in
     */
    public static String getCurrentEmail() {
        return currentEmail;
    }
    
    /**
     * Get current user full name
     * @return Current user full name, empty string if not set
     */
    public static String getCurrentFullName() {
        return currentFullName;
    }
    
    /**
     * Check if a user is currently logged in
     * @return true if user is logged in, false otherwise
     */
    public static boolean isUserLoggedIn() {
        return currentUserId != -1 && !currentUsername.isEmpty();
    }
    
    /**
     * Clear the current user session (logout)
     */
    public static void clearSession() {
        currentUserId = -1;
        currentUsername = "";
        currentEmail = "";
        currentFullName = "";
        
        System.out.println("User session cleared");
    }
    
    /**
     * Update current user information
     * @param email New email
     * @param fullName New full name
     */
    public static void updateUserInfo(String email, String fullName) {
        if (isUserLoggedIn()) {
            currentEmail = email;
            currentFullName = fullName;
            System.out.println("User info updated for: " + currentUsername);
        }
    }
    
    /**
     * Get session info as string (for debugging)
     * @return Session information
     */
    public static String getSessionInfo() {
        return String.format("UserSession[ID: %d, Username: %s, Email: %s, FullName: %s]", 
                           currentUserId, currentUsername, currentEmail, currentFullName);
    }
}