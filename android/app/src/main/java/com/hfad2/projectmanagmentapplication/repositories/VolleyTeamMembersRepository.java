package com.hfad2.projectmanagmentapplication.repositories;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hfad2.projectmanagmentapplication.config.APIConfig;
import com.hfad2.projectmanagmentapplication.models.Employee;
import com.hfad2.projectmanagmentapplication.models.Task;
import com.hfad2.projectmanagmentapplication.models.TaskPriority;
import com.hfad2.projectmanagmentapplication.models.User;
import com.hfad2.projectmanagmentapplication.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolleyTeamMembersRepository implements TeamMembersRepository {
    private final RequestQueue queue;
    private final Context context;

    public VolleyTeamMembersRepository(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    /**
     * Retrieves all team members associated with a specific project.
     *
     * @param projectId The unique identifier of the project
     * @param callback Callback to handle the operation result:
     *                 - onSuccess returns List<Employee> containing all team members
     *                 - onError returns error message if the operation fails
     * @throws VolleyError if network request fails
     * @throws JSONException if response parsing fails
     */
    @Override
    public void getAllMembers(String projectId, OperationCallback<List<Employee>> callback) {
        String url = APIConfig.GET_MEMBERS + "?" + APIConfig.PARAM_PROJECT_ID + "=" + projectId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> parseEmployeeList(response, callback),
                error -> handleVolleyError(error, callback));
        queue.add(stringRequest);
    }

    /**
     * Adds a new member to a project's team.
     *
     * @param projectId The unique identifier of the project
     * @param employeeId The unique identifier of the employee to add
     * @param callback Callback to handle the operation result:
     *                 - onSuccess returns true if member added successfully
     *                 - onError returns error message if addition fails
     * @throws VolleyError if network request fails
     * @throws JSONException if response parsing fails
     */
    @Override
    public void addMember(String projectId, String employeeId, OperationCallback<Boolean> callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.ADD_MEMBER,
                response -> parseBasicResponse(response, callback),
                error -> handleVolleyError(error, callback)) {
            // Add the parameters that will be sent to the endpoint
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(APIConfig.PARAM_PROJECT_ID, projectId);
                params.put(APIConfig.PARAM_EMPLOYEE_ID, employeeId);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    /**
     * Removes a member from a project's team.
     *
     * @param projectId The unique identifier of the project
     * @param employeeId The unique identifier of the employee to remove
     * @param callback Callback to handle the operation result:
     *                 - onSuccess returns true if member removed successfully
     *                 - onError returns error message if removal fails
     * @throws VolleyError if network request fails
     * @throws JSONException if response parsing fails
     */
    @Override
    public void removeMember(String projectId, String employeeId, OperationCallback<Boolean> callback) {
        Log.d("TeamMembersRepo", "Remove member params - projectId: " + projectId + ", employeeId: " + employeeId);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.REMOVE_MEMBER,
                response -> parseBasicResponse(response, callback),
                error -> handleVolleyError(error, callback)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(APIConfig.PARAM_PROJECT_ID, projectId);
                params.put(APIConfig.PARAM_EMPLOYEE_ID, employeeId);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    /**
     * Searches for team members in a project based on a search query.
     * The search is performed on both full name and username fields.
     *
     * @param projectId The unique identifier of the project
     * @param query The search string to match against member names/usernames
     * @param callback Callback to handle the operation result:
     *                 - onSuccess returns List<Employee> of matching members
     *                 - onError returns error message if search fails
     * @throws VolleyError if network request fails
     * @throws JSONException if response parsing fails
     */
    @Override
    public void searchMembers(String projectId, String query, OperationCallback<List<Employee>> callback) {
        String url = APIConfig.SEARCH_MEMBERS + "?" + APIConfig.PARAM_PROJECT_ID + "=" + projectId
                + "&" + APIConfig.PARAM_QUERY + "=" + Uri.encode(query);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> parseEmployeeList(response, callback),
                error -> handleVolleyError(error, callback));
        queue.add(stringRequest);
    }

    /**
     * Filters team members by their assigned role within a project.
     *
     * @param projectId The unique identifier of the project
     * @param role The role to filter by (e.g., "Developer", "Designer")
     * @param callback Callback to handle the operation result:
     *                 - onSuccess returns List<Employee> filtered by role
     *                 - onError returns error message if filtering fails
     * @throws VolleyError if network request fails
     * @throws JSONException if response parsing fails
     */
    @Override
    public void filterMembersByRole(String projectId, String role, OperationCallback<List<Employee>> callback) {
        String url = APIConfig.FILTER_MEMBERS + "?" + APIConfig.PARAM_PROJECT_ID + "=" + projectId
                + "&" + APIConfig.PARAM_ROLE + "=" + Uri.encode(role);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> parseEmployeeList(response, callback),
                error -> handleVolleyError(error, callback));
        queue.add(stringRequest);
    }

    /**
     * Retrieves the currently assigned task for a team member in a project.
     * Returns null if no active task is assigned.
     *
     * @param projectId The unique identifier of the project
     * @param employeeId The unique identifier of the employee
     * @param callback Callback to handle the operation result:
     *                 - onSuccess returns Task object if found, null otherwise
     *                 - onError returns error message if lookup fails
     * @throws VolleyError if network request fails
     * @throws JSONException if response parsing fails
     */
    @Override
    public void getAssignedTask(String projectId, String employeeId, OperationCallback<Task> callback) {
        String url = String.format("%s?%s=%s&%s=%s",
                APIConfig.GET_ASSIGNED_TASK,
                APIConfig.PARAM_PROJECT_ID, Uri.encode(projectId),
                APIConfig.PARAM_EMPLOYEE_ID, Uri.encode(employeeId));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> parseTask(response, callback),
                error -> handleVolleyError(error, callback));

        queue.add(stringRequest);
    }

    private void parseTask(String response, OperationCallback<Task> callback) {
        try {
            // Handle empty or null response
            if (response == null || response.trim().isEmpty() || response.equals("null")) {
                callback.onSuccess(null);
                return;
            }

            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.has("error") && jsonResponse.getBoolean("error")) {
                callback.onError(jsonResponse.getString("message"));
                return;
            }

            JSONObject data = jsonResponse.optJSONObject("data");
            if (data == null) {
                callback.onSuccess(null);
                return;
            }

            // Create Task object from data
            Task task = new Task(
                    data.getString("title"),
                    data.getString("description"),
                    null,  // Project will be set by the caller
                    TaskPriority.valueOf(data.getString("priority")),
                    new Date(data.getLong("due_date") * 1000)  // Convert Unix timestamp to Java Date
            );

            // Set task ID
            if (data.has("task_id")) {
                task.setTaskId(data.getString("task_id"));
            }

            callback.onSuccess(task);

        } catch (JSONException e) {
            callback.onError("Error parsing task data: " + e.getMessage());
        }
    }

    /**
     * Parses JSON response containing multiple employees into a List.
     * Handles array parsing and employee object creation.
     *
     * @param response JSON string containing array of employee data
     * @param callback Callback to return parsed List<Employee> or error
     * @throws JSONException if response format is invalid
     */

    private void parseEmployeeList(String response, OperationCallback<List<Employee>> callback) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            List<Employee> employees = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject employeeJson = jsonArray.getJSONObject(i);

                // Skip if this is the current user
                if (!employeeJson.getString("user_id").equals(SessionManager.getCurrentUserId())) {
                    employees.add(parseEmployeeFromJson(employeeJson));
                }
            }
            callback.onSuccess(employees);
        } catch (JSONException e) {
            callback.onError(APIConfig.ERROR_PARSE + ": " + e.getMessage());
        }
    }


    /**
     * Parses JSON response containing a single employee.
     * Handles both successful employee data and error responses.
     *
     * @param response JSON string containing employee data or error message
     * @param callback Callback to return parsed Employee or error
     * @throws JSONException if response format is invalid
     */
    private void parseSingleEmployee(String response, OperationCallback<Employee> callback) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("error")) {
                callback.onError(jsonObject.getString("message"));
                return;
            }
            callback.onSuccess(parseEmployeeFromJson(jsonObject));
        } catch (JSONException e) {
            callback.onError(APIConfig.ERROR_PARSE + ": " + e.getMessage());
        }
    }

    /**
     * Converts JSON employee data into Employee object.
     * Creates User object with basic profile information.
     *
     * @param obj JSONObject containing employee/user data
     * @return Employee instance populated with JSON data
     * @throws JSONException if required fields are missing
     */
    // TODO: The username is now passed twice because email no loner exists
    private Employee parseEmployeeFromJson(JSONObject obj) throws JSONException {
        User user = new User(
                obj.getString("user_id"),
                obj.getString("username"),
                obj.getString("full_name"),
                obj.getString("username"),
                "default_profile"
        );

        // Role now comes from employees table
        String role = obj.has("role") ? obj.getString("role") : "Employee";

        Employee employee = new Employee(user, role);

        // Add status if available
        if (obj.has("status")) {
            employee.setStatus(obj.getString("status"));
        }

        return employee;
    }

    /**
     * Parses basic boolean response from server.
     * Expects JSON with "error" boolean field.
     *
     * @param response JSON string containing operation result
     * @param callback Callback to return success/failure status
     * @throws JSONException if response format is invalid
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
     * Handles Volley network errors with specific error messages.
     * Categorizes errors as:
     * - Network errors (general connectivity issues)
     * - Timeout errors (request exceeded time limit)
     * - Authentication errors (invalid credentials)
     * - Server errors (5xx responses)
     *
     * @param error VolleyError instance containing error details
     * @param callback Callback to return appropriate error message
     */
    private void handleVolleyError(VolleyError error, OperationCallback<?> callback) {
        String message = APIConfig.ERROR_NETWORK;
        if (error instanceof TimeoutError) {
            message = APIConfig.ERROR_TIMEOUT;
        } else if (error instanceof AuthFailureError) {
            message = "Authentication failed";
        } else if (error instanceof ServerError) {
            message = "Server error";
        }
        callback.onError(message + ": " + error.getMessage());
    }

    @Override
    public void searchAvailableUsers(String projectId, String query, OperationCallback<List<Employee>> callback) {
        // Build the URL with query parameters
        String url = APIConfig.SEARCH_USERS + "?"
                + APIConfig.PARAM_PROJECT_ID + "=" + projectId
                + "&" + APIConfig.PARAM_QUERY + "=" + Uri.encode(query);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        // Parse the JSON response
                        JSONObject jsonResponse = new JSONObject(response);

                        // Check for errors first
                        if (jsonResponse.getBoolean("error")) {
                            callback.onError(jsonResponse.getString("message"));
                            return;
                        }

                        // Get the users array
                        JSONArray usersArray = jsonResponse.getJSONArray("users");
                        List<Employee> employees = new ArrayList<>();

                        // Parse each user into an Employee object
                        for (int i = 0; i < usersArray.length(); i++) {
                            JSONObject obj = usersArray.getJSONObject(i);

                            // Create User object first
                            User user = new User(
                                    obj.getString("user_id"),
                                    obj.getString("username"),
                                    obj.getString("full_name"),
                                    obj.getString("username"),  // Using username since email was removed
                                    "default_profile"
                            );
                            user.setUserType(obj.getString("user_type"));

                            // Create Employee with role from employees table
                            String role = obj.getString("role");
                            Employee employee = new Employee(user, role);

                            // Set employee status if available
                            if (obj.has("status")) {
                                employee.setStatus(obj.getString("status"));
                            }

                            employees.add(employee);
                        }

                        callback.onSuccess(employees);

                    } catch (JSONException e) {
                        callback.onError(APIConfig.ERROR_PARSE + ": " + e.getMessage());
                    }
                },
                error -> handleVolleyError(error, callback));

        // Add the request to the RequestQueue
        queue.add(stringRequest);
    }
}