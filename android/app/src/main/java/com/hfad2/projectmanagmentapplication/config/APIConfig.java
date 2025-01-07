package com.hfad2.projectmanagmentapplication.config;

/**
 * Configuration constants for API endpoints and request parameters.
 * Contains base URL, endpoint paths, and standard parameter keys for the project management API.
 */
public class APIConfig {
    /** Base URL for API endpoints, configured for Android emulator localhost */
    public static final String BASE_URL = "http://10.0.2.2/project_api/";  // Android emulator localhost

    // Request parameters
    public static final String PARAM_PROJECT_ID = "project_id";
    public static final String PARAM_EMPLOYEE_ID = "employee_id";
    public static final String PARAM_QUERY = "query";
    public static final String PARAM_ROLE = "role";

    /** API endpoint paths */
    // Team member endpoints
    public static final String GET_MEMBERS = BASE_URL + "get_members.php";
    public static final String ADD_MEMBER = BASE_URL + "add_member.php";
    public static final String REMOVE_MEMBER = BASE_URL + "remove_member.php";
    public static final String SEARCH_MEMBERS = BASE_URL + "search_members.php";
    public static final String FILTER_MEMBERS = BASE_URL + "filter_members.php";
    public static final String FIND_EMPLOYEE = BASE_URL + "find_employee.php";
    public static final String GET_ASSIGNED_TASK = BASE_URL + "get_assigned_task.php";

    /** Standard error messages */
    // Error messages
    public static final String ERROR_NETWORK = "Network error occurred";
    public static final String ERROR_TIMEOUT = "Request timed out";
    public static final String ERROR_PARSE = "Error parsing server response";
}