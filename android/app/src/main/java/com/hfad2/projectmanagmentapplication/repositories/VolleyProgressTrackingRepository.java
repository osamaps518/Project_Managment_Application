package com.hfad2.projectmanagmentapplication.repositories;

import android.content.Context;
import android.net.Uri;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hfad2.projectmanagmentapplication.config.APIConfig;
import com.hfad2.projectmanagmentapplication.models.Employee;
import com.hfad2.projectmanagmentapplication.models.Project;
import com.hfad2.projectmanagmentapplication.models.Task;
import com.hfad2.projectmanagmentapplication.models.TaskPriority;
import com.hfad2.projectmanagmentapplication.models.TaskStatus;
import com.hfad2.projectmanagmentapplication.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of ProgressTrackingRepository using Volley for network requests.
 * Handles all task-related operations with the backend PHP/MySQL server.
 *
 * Key features:
 * - CRUD operations for tasks
 * - Task filtering and search
 * - Status updates
 * - Integration with PHP endpoints
 */
public class VolleyProgressTrackingRepository implements ProgressTrackingRepository {
    private final RequestQueue queue;
    private final SimpleDateFormat dateFormat;

    /**
     * Initializes the repository with a Volley RequestQueue and date formatter.
     * @param context Application context for Volley initialization
     */
    public VolleyProgressTrackingRepository(Context context) {
        this.queue = Volley.newRequestQueue(context);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * Retrieves all tasks for a specific project.
     * Endpoint: get_tasks.php
     *
     * @param managerId Project identifier
     * @param callback Returns List<Task> on success, error message on failure
     */
    @Override
    public void getAllTasks(String managerId, OperationCallback<List<Task>> callback) {
        String url = APIConfig.GET_ALL_TASKS + "?" + APIConfig.PARAM_MANAGER_ID + "=" + managerId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> parseTaskList(response, callback),
                error -> handleVolleyError(error, callback));

        queue.add(request);
    }

    /**
     * Searches tasks by title or description.
     * Endpoint: search_tasks.php
     *
     * @param managerId Project identifier
     * @param query Search text to match against task title/description
     * @param callback Returns filtered List<Task> on success, error message on failure
     */
    @Override
    public void searchTasks(String managerId, String query, OperationCallback<List<Task>> callback) {
        String url = APIConfig.SEARCH_TASKS + "?" +
                APIConfig.PARAM_MANAGER_ID + "=" + managerId + "&" +
                APIConfig.PARAM_QUERY + "=" + Uri.encode(query);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> parseTaskList(response, callback),
                error -> handleVolleyError(error, callback));

        queue.add(request);
    }

    /**
     * Filters tasks by their current status.
     * Endpoint: filter_tasks.php
     *
     * @param managerId Project identifier
     * @param status TaskStatus to filter by
     * @param callback Returns filtered List<Task> on success, error message on failure
     */
    @Override
    public void filterTasksByStatus(String managerId, TaskStatus status,
                                    OperationCallback<List<Task>> callback) {
        String url = APIConfig.FILTER_TASKS + "?" +
                APIConfig.PARAM_MANAGER_ID + "=" + managerId + "&" +
                APIConfig.PARAM_STATUS + "=" + status.name();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> parseTaskList(response, callback),
                error -> handleVolleyError(error, callback));

        queue.add(request);
    }

    /**
     * Marks a task as complete.
     * Endpoint: complete_task.php
     *
     * @param taskId Task identifier
     * @param callback Returns true on success, error message on failure
     */
    @Override
    public void markTaskAsComplete(String taskId, OperationCallback<Boolean> callback) {
        StringRequest request = new StringRequest(Request.Method.POST, APIConfig.COMPLETE_TASK,
                response -> parseBasicResponse(response, callback),
                error -> handleVolleyError(error, callback)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(APIConfig.PARAM_TASK_ID, taskId);
                return params;
            }
        };
        queue.add(request);
    }

    /**
     * Removes a task from the project.
     * Endpoint: remove_task.php
     *
     * @param taskId Task identifier
     * @param callback Returns true on success, error message on failure
     */
    @Override
    public void removeTask(String taskId, OperationCallback<Boolean> callback) {
        StringRequest request = new StringRequest(Request.Method.POST, APIConfig.REMOVE_TASK,
                response -> parseBasicResponse(response, callback),
                error -> handleVolleyError(error, callback)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(APIConfig.PARAM_TASK_ID, taskId);
                return params;
            }
        };
        queue.add(request);
    }

    /**
     * Creates a new task in the project and assigns it to a user.
     * The backend will validate that the assigned user belongs to the project
     * before creating the task.
     *
     * @param projectId Project identifier
     * @param title Task title
     * @param description Task description
     * @param priority Task priority level
     * @param dueDate Task due date
     * @param assignedTo User ID of the employee assigned to this task
     * @param callback Callback to handle success/failure of task creation
     */
    @Override
    public void createTask(String projectId, String title, String description,
                           TaskPriority priority, Date dueDate, String assignedTo,
                           OperationCallback<Boolean> callback) {

        // First check if the employee already has a task in this project
        getAllTasks(projectId, new OperationCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                boolean hasExistingTask = tasks.stream()
                        .anyMatch(task ->
                                task.getProjectId().equals(projectId) &&
                                        task.getAssignedEmployee().getUserId().equals(assignedTo) &&
                                        task.getStatus() != TaskStatus.COMPLETED
                        );

                if (hasExistingTask) {
                    callback.onError("This employee already has an active task in this project");
                    return;
                }

                // If no existing task, proceed with task creation
                StringRequest request = new StringRequest(Request.Method.POST, APIConfig.ADD_TASK,
                        response -> {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean isSuccess = !jsonResponse.getBoolean("error");
                                if (isSuccess) {
                                    callback.onSuccess(true);
                                } else {
                                    callback.onError(jsonResponse.getString("message"));
                                }
                            } catch (JSONException e) {
                                callback.onError("Error parsing server response: " + e.getMessage());
                            }
                        },
                        error -> handleVolleyError(error, callback)) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put(APIConfig.PARAM_PROJECT_ID, projectId);
                        params.put(APIConfig.PARAM_TITLE, title);
                        params.put(APIConfig.PARAM_DESCRIPTION, description);
                        params.put(APIConfig.PARAM_PRIORITY, priority.name());
                        params.put(APIConfig.PARAM_DUE_DATE, dateFormat.format(dueDate));
                        params.put(APIConfig.PARAM_ASSIGNED_TO, assignedTo);
                        return params;
                    }
                };

                request.setRetryPolicy(new DefaultRetryPolicy(
                        30000,  // 30 seconds timeout
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                ));

                queue.add(request);
            }

            @Override
            public void onError(String error) {
                callback.onError("Error checking existing tasks: " + error);
            }
        });
    }
       /**
     * Parses JSON response containing task list.
     * Creates Task objects with temporary User and Project references.
     *
     * @param response JSON array of task data
     * @param callback Returns parsed List<Task>
     */
       private void parseTaskList(String response, OperationCallback<List<Task>> callback) {
           try {
               JSONObject jsonResponse = new JSONObject(response);

               // Check if there's an error
               if (jsonResponse.has("error") && jsonResponse.getBoolean("error")) {
                   callback.onError(jsonResponse.getString("message"));
                   return;
               }

               // Get the data array from the response
               JSONArray jsonArray;
               if (jsonResponse.has("data")) {
                   jsonArray = jsonResponse.getJSONArray("data");
               } else {
                   // For backward compatibility, try parsing the response directly as array
                   jsonArray = new JSONArray(response);
               }

               List<Task> tasks = new ArrayList<>();
               for (int i = 0; i < jsonArray.length(); i++) {
                   JSONObject obj = jsonArray.getJSONObject(i);
                   tasks.add(parseTaskFromJson(obj));
               }

               callback.onSuccess(tasks);
           } catch (JSONException | ParseException e) {
               callback.onError("Error parsing server response: " + e.getMessage());
           }
       }

    /**
     * Creates a Task object from JSON data.
     * Creates temporary User/Employee objects for assigned team member.
     * Project reference is left null for Activity to populate.
     *
     * @param obj JSON object containing task data
     * @return Parsed Task object
     */
    private Task parseTaskFromJson(JSONObject obj) throws JSONException, ParseException {
        // Parse user data
        User assignedUser = new User(
                obj.getString("user_id"),
                obj.getString("username"),
                obj.getString("full_name"),
                obj.getString("username"),
                "default_profile"
        );

        // Get role from employees table
        String role = obj.has("role") ? obj.getString("role") : "Employee";
        Employee assignedEmployee = new Employee(assignedUser, role);

        // Create Project object with both ID and title
        Project project = new Project();
        project.setProjectId(obj.getString("project_id")); // Add this line to set project ID
        project.setTitle(obj.getString("project_title"));

        // Parse task data
        Task task = new Task(
                obj.getString("title"),
                obj.getString("description"),
                project,
                TaskPriority.valueOf(obj.getString("priority")),
                dateFormat.parse(obj.getString("due_date"))
        );

        task.setTaskId(obj.getString("task_id"));
        task.setStatus(TaskStatus.valueOf(obj.getString("status")));
        task.setAssignedEmployee(assignedEmployee);
        task.setProjectId(obj.getString("project_id")); // Set project ID in task as well

        return task;
    }

    /**
     * Parses basic boolean response from server.
     * Expects JSON with "error" field.
     *
     * @param response JSON response string
     * @param callback Returns true if no error, false otherwise
     */
    private void parseBasicResponse(String response, OperationCallback<Boolean> callback) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            callback.onSuccess(!jsonResponse.getBoolean("error"));
        } catch (JSONException e) {
            callback.onError(APIConfig.ERROR_PARSE + ": " + e.getMessage());
        }
    }

    /**
     * Handles Volley network errors with standard error message.
     *
     * @param error VolleyError from failed request
     * @param callback Returns error message
     */
    private void handleVolleyError(VolleyError error, OperationCallback<?> callback) {
        callback.onError(APIConfig.ERROR_NETWORK + ": " + error.getMessage());
    }
}