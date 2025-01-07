package com.hfad2.projectmanagmentapplication.repositories;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hfad2.projectmanagmentapplication.models.Employee;
import com.hfad2.projectmanagmentapplication.models.Task;
import com.hfad2.projectmanagmentapplication.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolleyTeamMembersRepository implements TeamMembersRepository {
    private final String baseUrl = "http://10.0.2.2/project_api/";  // Android emulator localhost
    private final RequestQueue queue;
    private final Context context;

    public VolleyTeamMembersRepository(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    @Override
    public void getAllMembers(String projectId, OperationCallback<List<Employee>> callback) {
        String url = baseUrl + "get_members.php?project_id=" + projectId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        List<Employee> employees = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            // Convert JSON to Employee object
                            User user = new User(obj.getString("email"),
                                    obj.getString("full_name"));
                            user.username = obj.getString("username");
                            Employee employee = new Employee(user);
                            employees.add(employee);
                        }
                        callback.onSuccess(employees);
                    } catch (JSONException e) {
                        callback.onError("JSON parsing error: " + e.getMessage());
                    }
                },
                error -> callback.onError("Network error: " + error.getMessage())
        );

        queue.add(stringRequest);
    }

    @Override
    public void addMember(String projectId, String employeeId, OperationCallback<Boolean> callback) {
        String url = baseUrl + "add_member.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        callback.onSuccess(!jsonResponse.getBoolean("error"));
                    } catch (JSONException e) {
                        callback.onError("JSON parsing error: " + e.getMessage());
                    }
                },
                error -> callback.onError("Network error: " + error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("project_id", projectId);
                params.put("employee_id", employeeId);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    @Override
    public void removeMember(String projectId, String employeeId, OperationCallback<Boolean> callback) {
        String url = baseUrl + "remove_member.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        callback.onSuccess(!jsonResponse.getBoolean("error"));
                    } catch (JSONException e) {
                        callback.onError("JSON parsing error: " + e.getMessage());
                    }
                },
                error -> callback.onError("Network error: " + error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("project_id", projectId);
                params.put("employee_id", employeeId);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    @Override
    public void searchMembers(String projectId, String query, OperationCallback<List<Employee>> callback) {
        String url = baseUrl + "search_members.php?project_id=" + projectId + "&query=" + query;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        List<Employee> employees = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            User user = new User(obj.getString("email"), obj.getString("full_name"));
                            user.username = obj.getString("username");
                            Employee employee = new Employee(user);
                            employees.add(employee);
                        }
                        callback.onSuccess(employees);
                    } catch (JSONException e) {
                        callback.onError("JSON parsing error: " + e.getMessage());
                    }
                },
                error -> callback.onError("Network error: " + error.getMessage())
        );

        queue.add(stringRequest);
    }

    @Override
    public void filterMembersByRole(String projectId, String role, OperationCallback<List<Employee>> callback) {
        String url = baseUrl + "filter_members.php?project_id=" + projectId + "&role=" + role;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        List<Employee> employees = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            User user = new User(obj.getString("email"), obj.getString("full_name"));
                            user.username = obj.getString("username");
                            Employee employee = new Employee(user);
                            employees.add(employee);
                        }
                        callback.onSuccess(employees);
                    } catch (JSONException e) {
                        callback.onError("JSON parsing error: " + e.getMessage());
                    }
                },
                error -> callback.onError("Network error: " + error.getMessage())
        );

        queue.add(stringRequest);
    }

    @Override
    public void assignTask(String projectId, String employeeId, Task task, OperationCallback<Boolean> callback) {
        String url = baseUrl + "assign_task.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        callback.onSuccess(!jsonResponse.getBoolean("error"));
                    } catch (JSONException e) {
                        callback.onError("JSON parsing error: " + e.getMessage());
                    }
                },
                error -> callback.onError("Network error: " + error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("project_id", projectId);
                params.put("employee_id", employeeId);
                params.put("task_title", task.getTitle());
                // Add other task parameters
                return params;
            }
        };

        queue.add(stringRequest);
    }

    @Override
    public void findEmployee(String userId, OperationCallback<Employee> callback) {
        String url = baseUrl + "find_employee.php?user_id=" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        User user = new User(obj.getString("email"), obj.getString("full_name"));
                        user.username = obj.getString("username");
                        Employee employee = new Employee(user);
                        callback.onSuccess(employee);
                    } catch (JSONException e) {
                        callback.onError("JSON parsing error: " + e.getMessage());
                    }
                },
                error -> callback.onError("Network error: " + error.getMessage())
        );

        queue.add(stringRequest);
    }
}