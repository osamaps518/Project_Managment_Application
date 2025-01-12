package com.hfad2.projectmanagmentapplication.activities.employee;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.activities.NavigationManager;
import com.hfad2.projectmanagmentapplication.activities.TaskDetailActivity;
import com.hfad2.projectmanagmentapplication.project_members.muna.ProjectAdapter;
import com.hfad2.projectmanagmentapplication.config.APIConfig;
import com.hfad2.projectmanagmentapplication.models.Project;
import com.hfad2.projectmanagmentapplication.repositories.OperationCallback;
import com.hfad2.projectmanagmentapplication.repositories.VolleyTeamMembersRepository;

import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDashboardActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProjectAdapter projectAdapter;
    private List<Project> projectList;
    private String employeeId;
    private ImageButton btnMenu;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private NavigationManager navigationManager;
    private VolleyTeamMembersRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        employeeId = getIntent().getStringExtra(APIConfig.PARAM_EMPLOYEE_ID);
        repository = new VolleyTeamMembersRepository(this);

        setupViews();
        setupRecyclerView();
        setupNavigation();
        fetchProjects();
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.recyclerView);
        btnMenu = findViewById(R.id.btn_menu);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
    }

    private void setupRecyclerView() {
        projectList = new ArrayList<>();
        projectAdapter = new ProjectAdapter(projectList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(projectAdapter);

        projectAdapter.setOnItemClickListener(position -> {
            Project selectedProject = projectList.get(position);
            String url = APIConfig.GET_ASSIGNED_TASK + "?project_id=" + selectedProject.getProjectId() + "&employee_id=" + employeeId;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    response -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (!jsonResponse.getBoolean("error")) {
                                JSONObject taskData = jsonResponse.getJSONObject("data");
                                if (taskData != null && !taskData.isNull("task_id")) {
                                    // Navigate to task details if task exists
                                    Intent intent = new Intent(EmployeeDashboardActivity.this, TaskDetailActivity.class);
                                    intent.putExtra(APIConfig.PARAM_TASK_ID, taskData.getString("task_id"));
                                    intent.putExtra(APIConfig.PARAM_EMPLOYEE_ID, employeeId);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(EmployeeDashboardActivity.this,
                                            "No active tasks assigned in this project", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(EmployeeDashboardActivity.this,
                                        "Error: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(EmployeeDashboardActivity.this,
                                    "Error getting task details", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(EmployeeDashboardActivity.this,
                            "Network error occurred", Toast.LENGTH_SHORT).show()
            );

            Volley.newRequestQueue(this).add(stringRequest);
        });

        projectAdapter.setOnDeleteClickListener(project -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Leave Project")
                    .setMessage("Are you sure you want to leave project '" + project.getTitle() + "'?")
                    .setPositiveButton("Leave", (dialog, which) -> leaveProject(project))
                    .setNegativeButton("Cancel", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });
    }

    private void setupNavigation() {
        navigationManager = new NavigationManager(this, drawerLayout, navigationView);
        navigationManager.setupMenuButton(btnMenu);
    }

    private void leaveProject(Project project) {
        repository.removeMember(project.getProjectId(), employeeId, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Toast.makeText(EmployeeDashboardActivity.this,
                        "Successfully left the project", Toast.LENGTH_SHORT).show();
                fetchProjects();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(EmployeeDashboardActivity.this,
                        "Error leaving project: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchProjects();
    }

    private void fetchProjects() {
        String url = APIConfig.GET_PROJECTS + "?employee_id=" + employeeId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        projectList.clear();
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Project project = new Project(
                                    jsonObject.getString(APIConfig.PARAM_PROJECT_ID),
                                    jsonObject.getString(APIConfig.PARAM_TITLE),
                                    jsonObject.getString(APIConfig.PARAM_DESCRIPTION),
                                    new SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getString(APIConfig.PARAM_START_DATE)),
                                    new SimpleDateFormat("yyyy-MM-dd").parse(jsonObject.getString(APIConfig.PARAM_DUE_DATE))
                            );
                            projectList.add(project);
                        }
                        projectAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, APIConfig.ERROR_PARSE, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, APIConfig.ERROR_NETWORK, Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(stringRequest);
    }
}