package com.hfad2.projectmanagmentapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages user session data across the application.
 * Uses both static fields for immediate access and SharedPreferences for persistence.
 * This hybrid approach ensures data availability both in memory and after app restarts.
 */
public class SessionManager {
    // Static fields for immediate access during app lifecycle
    private static String currentUserId;
    private static String userType;
    private static String userName;

    // SharedPreferences constants
    private static final String PREF_NAME = "ProjectManagementSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_TYPE = "userType";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Initializes the session with user data.
     * Should be called when user successfully logs in.
     */
    public static void initializeSession(Context context, String userId, String type, String name) {
        // Set static fields
        currentUserId = userId;
        userType = type;
        userName = name;

        // Save to SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_TYPE, type);
        editor.putString(KEY_USER_NAME, name);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Loads session data from SharedPreferences into static fields.
     * Should be called when application starts.
     */
    public static void loadSession(Context context) {
        SharedPreferences prefs = getSharedPreferences(context);
        currentUserId = prefs.getString(KEY_USER_ID, null);
        userType = prefs.getString(KEY_USER_TYPE, null);
        userName = prefs.getString(KEY_USER_NAME, null);
    }

    /**
     * Clears all session data.
     * Should be called when user logs out.
     */
    public static void clearSession(Context context) {
        // Clear static fields
        currentUserId = null;
        userType = null;
        userName = null;

        // Clear SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }

    // Getters for session data
    public static String getCurrentUserId() {
        return currentUserId;
    }

    public static String getUserType() {
        return userType;
    }

    public static String getUserName() {
        return userName;
    }

    public static boolean isLoggedIn(Context context) {
        return getSharedPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public static boolean isManager() {
        return "MANAGER".equalsIgnoreCase(userType);
    }
}
