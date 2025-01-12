package com.hfad2.projectmanagmentapplication.activities.manager;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.activities.NavigationManager;
import com.hfad2.projectmanagmentapplication.activities.ProgressTrackingActivity;
import com.hfad2.projectmanagmentapplication.activities.TeamMembersActivity;
import com.hfad2.projectmanagmentapplication.config.APIConfig;
import com.hfad2.projectmanagmentapplication.models.Project;

import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagerDashboardActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProjectAdapter projectAdapter;
    private List<Project> projectList;
    private String managerId;
    private ImageButton btnMenu;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private NavigationManager navigationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        managerId = getIntent().getStringExtra(APIConfig.PARAM_MANAGER_ID);
        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);

        projectList = new ArrayList<>();
        projectAdapter = new ProjectAdapter(projectList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(projectAdapter);

        projectAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(ManagerDashboardActivity.this, ProgressTrackingActivity.class);
            intent.putExtra(APIConfig.PARAM_MANAGER_ID, managerId);
            intent.putExtra(APIConfig.PARAM_PROJECT_ID, projectList.get(position).getProjectId());
            intent.putExtra(ProgressTrackingActivity.EXTRA_VIEW_MODE, ProgressTrackingActivity.VIEW_MODE_PROJECT_TASKS);
            intent.putExtra("project_name", projectList.get(position).getTitle());
            startActivity(intent);
        });

        projectAdapter.setOnAddMemberClickListener(project -> {
            Intent intent = new Intent(ManagerDashboardActivity.this, TeamMembersActivity.class);
            intent.putExtra("project_id", project.getProjectId());
            startActivity(intent);
        });

        projectAdapter.setOnDeleteClickListener(project -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Delete Project")
                    .setMessage("Are you sure you want to delete project '" + project.getTitle() + "'? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> deleteProject(project))
                    .setNegativeButton("Cancel", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ManagerDashboardActivity.this, CreateNewProject.class);
            intent.putExtra(APIConfig.PARAM_MANAGER_ID, managerId);
            startActivity(intent);
        });

        btnMenu = findViewById(R.id.btn_menu);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationManager = new NavigationManager(this, drawerLayout, navigationView);
        navigationManager.setupMenuButton(btnMenu);
        fetchProjects();
    }


    private void deleteProject(Project project) {
        String url = APIConfig.DELETE_PROJECT;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (!jsonResponse.getBoolean("error")) {
                            Toast.makeText(this, "Project deleted successfully", Toast.LENGTH_SHORT).show();
                            // Refresh the projects list
                            fetchProjects();
                        } else {
                            Toast.makeText(this, "Error: " + jsonResponse.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error occurred", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("project_id", project.getProjectId());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchProjects();
    }

    private void fetchProjects() {
        String url = APIConfig.GET_PROJECTS + "?" + APIConfig.PARAM_MANAGER_ID + "=" + managerId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        projectList.clear();
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            // Capture the project_id from the response
                            String projectId = jsonObject.getString(APIConfig.PARAM_PROJECT_ID);
                            String title = jsonObject.getString(APIConfig.PARAM_TITLE);
                            String description = jsonObject.getString(APIConfig.PARAM_DESCRIPTION);
                            String startDate = jsonObject.getString(APIConfig.PARAM_START_DATE);
                            String dueDate = jsonObject.getString(APIConfig.PARAM_DUE_DATE);

                            Project project = new Project(
                                    projectId,  // Add projectId to constructor
                                    title,
                                    description,
                                    new SimpleDateFormat("yyyy-MM-dd").parse(startDate),
                                    new SimpleDateFormat("yyyy-MM-dd").parse(dueDate)
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