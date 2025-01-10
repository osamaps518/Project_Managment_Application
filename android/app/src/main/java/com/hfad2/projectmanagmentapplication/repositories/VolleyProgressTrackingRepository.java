package com.hfad2.projectmanagmentapplication.repositories;

import android.content.Context;
import android.net.Uri;

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
     * @param projectId Project identifier
     * @param callback Returns List<Task> on success, error message on failure
     */
    @Override
    public void getAllTasks(String projectId, OperationCallback<List<Task>> callback) {
        String url = APIConfig.GET_ALL_TASKS + "?" + APIConfig.PARAM_PROJECT_MANAGER_ID + "=" + projectId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> parseTaskList(response, callback),
                error -> handleVolleyError(error, callback));

        queue.add(request);
    }

    /**
     * Searches tasks by title or description.
     * Endpoint: search_tasks.php
     *
     * @param projectManagerId Project identifier
     * @param query Search text to match against task title/description
     * @param callback Returns filtered List<Task> on success, error message on failure
     */
    @Override
    public void searchTasks(String projectManagerId, String query, OperationCallback<List<Task>> callback) {
        String url = APIConfig.SEARCH_TASKS + "?" +
                APIConfig.PARAM_PROJECT_MANAGER_ID + "=" + projectManagerId + "&" +
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
     * @param projectManagerId Project identifier
     * @param status TaskStatus to filter by
     * @param callback Returns filtered List<Task> on success, error message on failure
     */
    @Override
    public void filterTasksByStatus(String projectManagerId, TaskStatus status,
                                    OperationCallback<List<Task>> callback) {
        String url = APIConfig.FILTER_TASKS + "?" +
                APIConfig.PARAM_PROJECT_MANAGER_ID + "=" + projectManagerId + "&" +
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

    // TODO: This method uses projectID where there's no project id in the first place, also, this method probably would be deleted in the future
    /**
     * Creates a new task in the project.
     * Endpoint: add_task.php
     * Note: Returns a temporary Task object while the server request is processed
     *
     * @param projectId Project identifier
     * @param title Task title
     * @param description Task description
     * @param priority Task priority level
     * @param dueDate Task due date
     * @return Temporary Task object
     */
    @Override
    public Task createTask(String projectId, String title, String description,
                           TaskPriority priority, Date dueDate) {
        StringRequest request = new StringRequest(Request.Method.POST, APIConfig.ADD_TASK,
                response -> parseBasicResponse(response, new OperationCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        // Success is handled via the returned Task object
                    }

                    @Override
                    public void onError(String error) {
                        // Errors are handled via standard error handling
                    }
                }),
                error -> handleVolleyError(error, new OperationCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {}

                    @Override
                    public void onError(String error) {}
                })) {
            // Add task parameters to the Post request
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(APIConfig.PARAM_PROJECT_ID, projectId);
                params.put(APIConfig.PARAM_TITLE, title);
                params.put(APIConfig.PARAM_DESCRIPTION, description);
                params.put(APIConfig.PARAM_PRIORITY, priority.name());
                params.put(APIConfig.PARAM_DUE_DATE, dateFormat.format(dueDate));
                return params;
            }
        };
        queue.add(request);

        // Return a temporary Task object
        return new Task(title, description, null, priority, dueDate);
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
            JSONArray jsonArray = new JSONArray(response);
            List<Task> tasks = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                tasks.add(parseTaskFromJson(obj));
            }

            callback.onSuccess(tasks);
        } catch (JSONException | ParseException e) {
            callback.onError(APIConfig.ERROR_PARSE + ": " + e.getMessage());
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
    // TODO: adjust the endpoint to return the role
    private Task parseTaskFromJson(JSONObject obj) throws JSONException, ParseException {
        // Create a temporary User for the assigned employee
        User assignedUser = new User(
                obj.getString("assigned_email"),
                obj.getString("assigned_name")
        );
        Employee assignedEmployee = new Employee(assignedUser, "Unknown");

        // Create a temporary Project
        Project project = new Project();
        project.setTitle(obj.getString("project_title"));

        // Parse the task
        Task task = new Task(
                obj.getString("title"),
                obj.getString("description"),
                project,
                TaskPriority.valueOf(obj.getString("priority")),
                dateFormat.parse(obj.getString("due_date"))
        );

        task.setStatus(TaskStatus.valueOf(obj.getString("status")));
        task.setAssignedEmployee(assignedEmployee);

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