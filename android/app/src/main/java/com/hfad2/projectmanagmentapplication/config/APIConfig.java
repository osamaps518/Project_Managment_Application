package com.hfad2.projectmanagmentapplication.config;

/**
 * Configuration constants for API endpoints and request parameters.
 * Contains base URL, endpoint paths, and standard parameter keys for the project management API.
 */
public class APIConfig {
    /** Base URL for API endpoints, configured for Android emulator localhost */
    public static final String BASE_URL = "http://10.0.2.2/project_api/";  // Android emulator localhost

    // Request parameters for team members
    public static final String PARAM_PROJECT_ID = "project_id";
    public static final String PARAM_EMPLOYEE_ID = "employee_id";
    public static final String PARAM_QUERY = "query";
    public static final String PARAM_ROLE = "role";

    // Request parameters for tasks
    public static final String PARAM_TASK_ID = "task_id";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_DESCRIPTION = "description";
    public static final String PARAM_PRIORITY = "priority";
    public static final String PARAM_DUE_DATE = "due_date";
    public static final String PARAM_STATUS = "status";

    // Request parameters for task comments
    public static final String PARAM_COMMENT_ID = "comment_id";
    public static final String PARAM_AUTHOR_ID = "author_id";
    public static final String PARAM_CONTENT = "content";

    // Request parameters for notifications
    public static final String PARAM_NOTIFICATION_ID = "notification_id";
    public static final String PARAM_START_DATE = "start_date";
    public static final String PARAM_END_DATE = "end_date";
    public static final String PARAM_SHOW_ARCHIVED = "show_archived";
    public static final String PARAM_TYPE = "type";

    /** API endpoint paths */
    // Team member endpoints
    public static final String GET_MEMBERS = BASE_URL + "get_members.php";
    public static final String ADD_MEMBER = BASE_URL + "add_member.php";
    public static final String REMOVE_MEMBER = BASE_URL + "remove_member.php";
    public static final String SEARCH_MEMBERS = BASE_URL + "search_members.php";
    public static final String FILTER_MEMBERS = BASE_URL + "filter_members.php";
    public static final String FIND_EMPLOYEE = BASE_URL + "find_employee.php";
    public static final String GET_ASSIGNED_TASK = BASE_URL + "get_assigned_task.php";

    // Task management endpoints
    public static final String GET_ALL_TASKS = BASE_URL + "get_tasks.php";
    public static final String ADD_TASK = BASE_URL + "add_task.php";
    public static final String REMOVE_TASK = BASE_URL + "remove_task.php";
    public static final String SEARCH_TASKS = BASE_URL + "search_tasks.php";
    public static final String FILTER_TASKS = BASE_URL + "filter_tasks.php";
    public static final String COMPLETE_TASK = BASE_URL + "complete_task.php";

    // Task comment endpoints
    public static final String GET_TASK_COMMENTS = BASE_URL + "get_task_comments.php";
    public static final String ADD_TASK_COMMENT = BASE_URL + "add_task_comment.php";

    // Notification endpoints
    public static final String GET_NOTIFICATIONS = BASE_URL + "get_notifications.php";
    public static final String SEARCH_NOTIFICATIONS = BASE_URL + "search_notifications.php";
    public static final String REMOVE_NOTIFICATION = BASE_URL + "remove_notification.php";
    public static final String ARCHIVE_NOTIFICATION = BASE_URL + "archive_notification.php";
    public static final String GET_EMAIL_CONTENT = BASE_URL + "get_email_content.php";
    public static final String FILTER_NOTIFICATIONS = BASE_URL + "filter_notifications.php";
    public static final String SEND_MESSAGE = BASE_URL + "send_message.php";


    /** Standard error messages */
    // Error messages
    public static final String ERROR_NETWORK = "Network error occurred";
    public static final String ERROR_TIMEOUT = "Request timed out";
    public static final String ERROR_PARSE = "Error parsing server response";
}