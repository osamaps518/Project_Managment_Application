package com.hfad2.projectmanagmentapplication.config;


/**
 * Configuration constants for API endpoints and request parameters.
 * Contains base URL, endpoint paths, and standard parameter keys for the project management API.
 */
public class APIConfig {
    /** Base URL for API endpoints, configured for Android emulator localhost */
    // Android emulator localhost
    public static final String BASE_URL = "http://10.0.2.2/project_management/";
    // Physical device localhost
//    public static final String BASE_URL = "http://192.168.122.255/project_management/";

    // Request parameters for team members
    public static final String PARAM_MANAGER_ID = "manager_id";
    public static final String PARAM_PROJECT_ID = "project_id";
    public static final String PARAM_EMPLOYEE_ID = "employee_id";
    public static final String PARAM_QUERY = "query";
    public static final String PARAM_ROLE = "role";

    // Request parameters for tasks and projects
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
    public static final String PARAM_USER_ID = "user_id";
    public static final String PARAM_NOTIFICATION_ID = "notification_id";
    public static final String PARAM_START_DATE = "start_date";
    public static final String PARAM_END_DATE = "end_date";
    public static final String PARAM_SHOW_ARCHIVED = "show_archived";
    public static final String PARAM_TYPE = "type";

    // Login parameters
    public static final String PARAM_USERNAME = "Name";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_USER_TYPE = "type";

    // add project parameters and response key
//    public static final String PARAM_MANAGER_ID = "manager_id";
    public static final String RESPONSE_USER_ID = "user_id";

    // Response keys for login
    public static final String RESPONSE_STATUS = "status2";
    public static final String RESPONSE_TYPE = "type";
    public static final String STATUS_SUCCESS = "success";
    public static final String USER_TYPE_MANAGER = "manager";
    public static final String USER_TYPE_EMPLOYEE = "employee";

    // Register endpoint response keys
    public static final String RESPONSE_SUCCESS = "success";
    public static final String RESPONSE_MESSAGE = "message";

    /** API endpoint paths */
    // Login and registration endpoints
    public static final String LOGIN = BASE_URL + "authentication/login.php";
    public static final String REGISTER = BASE_URL + "authentication/register.php";

    // Project management endpoints
    public static final String GET_PROJECTS = BASE_URL + "projects/get_projects.php";
    public static final String ADD_PROJECT = BASE_URL + "projects/add_project.php";
    public static final String DELETE_PROJECT = BASE_URL + "projects/delete_project.php";

    // Task management endpoints
    public static final String GET_TASK_DETAILS = BASE_URL + "tasks/get_task_details.php";
    public static final String UPDATE_TASK_STATUS = BASE_URL + "tasks/update_task_status.php";


    // Team member endpoints
    public static final String GET_MEMBERS = BASE_URL + "team_members/get_members.php";
    public static final String ADD_MEMBER = BASE_URL + "team_members/add_member.php";
    public static final String REMOVE_MEMBER = BASE_URL + "team_members/remove_member.php";
    public static final String SEARCH_MEMBERS = BASE_URL + "team_members/search_members.php";
    public static final String FILTER_MEMBERS = BASE_URL + "team_members/filter_members.php";
    public static final String FIND_EMPLOYEE = BASE_URL + "team_members/find_employee.php";
    public static final String GET_ASSIGNED_TASK = BASE_URL + "team_members/get_assigned_task.php";
    public static final String SEARCH_USERS = BASE_URL + "team_members/search_users.php";


    // Task management endpoints
    public static final String GET_ALL_TASKS = BASE_URL + "tasks/get_tasks.php";
    public static final String ADD_TASK = BASE_URL + "tasks/add_task.php";
    public static final String REMOVE_TASK = BASE_URL + "tasks/remove_task.php";
    public static final String SEARCH_TASKS = BASE_URL + "tasks/search_tasks.php";
    public static final String FILTER_TASKS = BASE_URL + "tasks/filter_tasks.php";
    public static final String COMPLETE_TASK = BASE_URL + "tasks/complete_task.php";

    // Task comment endpoints
    public static final String GET_TASK_COMMENTS = BASE_URL + "tasks/get_task_comments.php";
    public static final String ADD_TASK_COMMENT = BASE_URL + "tasks/add_task_comment.php";

    // Notification and send message endpoints
    public static final String GET_NOTIFICATIONS = BASE_URL + "notifications/get_notifications.php";
    public static final String SEARCH_NOTIFICATIONS = BASE_URL + "notifications/search_notifications.php";
    public static final String REMOVE_NOTIFICATION = BASE_URL + "notifications/remove_notification.php";
    public static final String ARCHIVE_NOTIFICATION = BASE_URL + "notifications/archive_notification.php";
    public static final String FILTER_NOTIFICATIONS = BASE_URL + "notifications/filter_notifications.php";

    // Email sending and receiving endpoints
    public static final String SEND_MESSAGE = BASE_URL + "emails/send_message.php";
    public static final String GET_USER_PROJECTS = BASE_URL + "emails/get_user_projects.php";
    public static final String GET_PROJECT_MEMBERS = BASE_URL + "emails/get_project_members.php";
    public static final String GET_EMAIL_CONTENT = BASE_URL + "emails/get_email_content.php";

    /** Standard error messages */
    // Error messages
    public static final String ERROR_NETWORK = "Network error occurred";
    public static final String ERROR_TIMEOUT = "Request timed out";
    public static final String ERROR_PARSE = "Error parsing server response";
    public static final String PARAM_ASSIGNED_TO = "assigned_to";
}