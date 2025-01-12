package com.hfad2.projectmanagmentapplication.activities.manager;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.activities.ProgressTrackingActivity;
import com.hfad2.projectmanagmentapplication.activities.TeamMembersActivity;
import com.hfad2.projectmanagmentapplication.config.APIConfig;
import com.hfad2.projectmanagmentapplication.models.Project;

import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ManagerDashboardActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProjectAdapter projectAdapter;
    private List<Project> projectList;
    private String managerId;
    private Button btnAddMember;

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

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ManagerDashboardActivity.this, CreateNewProject.class);
            intent.putExtra(APIConfig.PARAM_MANAGER_ID, managerId);
            startActivity(intent);
        });

        fetchProjects();
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
                            String projectId = jsonObject.getString("project_id");
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