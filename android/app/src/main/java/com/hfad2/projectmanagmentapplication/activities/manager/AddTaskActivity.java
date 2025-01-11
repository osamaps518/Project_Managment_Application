package com.hfad2.projectmanagmentapplication.activities.manager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.activities.ProgressTrackingActivity;
import com.hfad2.projectmanagmentapplication.config.APIConfig;
import com.hfad2.projectmanagmentapplication.models.Project;
import com.hfad2.projectmanagmentapplication.models.TaskPriority;
import com.hfad2.projectmanagmentapplication.models.User;
import com.hfad2.projectmanagmentapplication.repositories.MessageRepository;
import com.hfad2.projectmanagmentapplication.repositories.OperationCallback;
import com.hfad2.projectmanagmentapplication.repositories.ProgressTrackingRepository;
import com.hfad2.projectmanagmentapplication.repositories.VolleyMessageRepository;
import com.hfad2.projectmanagmentapplication.repositories.VolleyProgressTrackingRepository;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class AddTaskActivity extends AppCompatActivity {
    private String managerId;
    private String projectId;
    private int viewMode;
    private Spinner projectSpinner;
    private Spinner userSpinner;
    private List<Project> userProjects;
    private List<User> projectMembers;
    private TextInputEditText titleInput;
    private TextInputEditText descriptionInput;
    private Spinner prioritySpinner;
    private DatePicker dueDatePicker;
    private MessageRepository messageRepository; // We'll reuse this for user loading
    private ProgressTrackingRepository taskRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Get manager ID and view mode
        managerId = getIntent().getStringExtra(APIConfig.PARAM_MANAGER_ID);
        viewMode = getIntent().getIntExtra(ProgressTrackingActivity.EXTRA_VIEW_MODE,
                ProgressTrackingActivity.VIEW_MODE_ALL_TASKS);

        // If in project view mode, get project ID
        if (viewMode == ProgressTrackingActivity.VIEW_MODE_PROJECT_TASKS) {
            projectId = getIntent().getStringExtra(APIConfig.PARAM_PROJECT_ID);
        }

        if (managerId == null) {
            Toast.makeText(this, "Manager ID required", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initialize();

        // If we have a specific project, hide project spinner and load its members
        if (projectId != null) {
            projectSpinner.setVisibility(View.GONE);
            loadProjectMembers(projectId);
        } else {
            loadUserProjects();
        }
    }

    private void initialize() {
        messageRepository = new VolleyMessageRepository(this);
        taskRepository = new VolleyProgressTrackingRepository(this);

        // Initialize views
        titleInput = findViewById(R.id.task_title_input);
        descriptionInput = findViewById(R.id.task_description_input);
        prioritySpinner = findViewById(R.id.priority_spinner);
        projectSpinner = findViewById(R.id.project_spinner);
        userSpinner = findViewById(R.id.user_spinner);

        // Set up priority spinner
        ArrayAdapter<TaskPriority> priorityAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, TaskPriority.values());
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(priorityAdapter);

        // Reuse project selection code from MessageSendingActivity
        projectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Project selectedProject = userProjects.get(position);
                projectId = selectedProject.getProjectId();
                loadProjectMembers(projectId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                userSpinner.setAdapter(null);
                userSpinner.setEnabled(false);
            }
        });

        findViewById(R.id.btn_create_task).setOnClickListener(v -> createTask());
    }

    // We can reuse these methods directly from MessageSendingActivity
    private void loadUserProjects() {
        messageRepository.getUserProjects(managerId, new OperationCallback<List<Project>>() {
            @Override
            public void onSuccess(List<Project> projects) {
                userProjects = projects;
                ArrayAdapter<Project> adapter = new ArrayAdapter<>(
                        AddTaskActivity.this,
                        android.R.layout.simple_spinner_item,
                        projects
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                runOnUiThread(() -> {
                    projectSpinner.setAdapter(adapter);
                    projectSpinner.setEnabled(!projects.isEmpty());
                    if (!projects.isEmpty()) {
                        projectSpinner.setSelection(0);
                        loadProjectMembers(projects.get(0).getProjectId());
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(AddTaskActivity.this,
                            "Error loading projects: " + error, Toast.LENGTH_SHORT).show();
                    projectSpinner.setEnabled(false);
                });
            }
        });
    }

    private void loadProjectMembers(String projectId) {
        messageRepository.getProjectMembers(projectId, new OperationCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> members) {
                // Filter out any managers from the list - we only want to assign to employees
                List<User> employeeMembers = members.stream()
                        .filter(user -> !user.getUserType().equals("MANAGER"))
                        .collect(Collectors.toList());

                projectMembers = employeeMembers;

                ArrayAdapter<User> adapter = new ArrayAdapter<>(
                        AddTaskActivity.this,
                        android.R.layout.simple_spinner_item,
                        employeeMembers
                );

                adapter.setDropDownViewResource(
                        android.R.layout.simple_spinner_dropdown_item
                );

                runOnUiThread(() -> {
                    userSpinner.setAdapter(adapter);
                    userSpinner.setEnabled(!employeeMembers.isEmpty());

                    if (!employeeMembers.isEmpty()) {
                        userSpinner.setSelection(0);
                    } else {
                        Toast.makeText(AddTaskActivity.this,
                                "No employees available in this project",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(AddTaskActivity.this,
                            "Error loading project members: " + error,
                            Toast.LENGTH_SHORT).show();
                    userSpinner.setEnabled(false);
                });
            }
        });
    }

    private void createTask() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        User selectedUser = (User) userSpinner.getSelectedItem();
        TaskPriority priority = (TaskPriority) prioritySpinner.getSelectedItem();

        if (title.isEmpty() || description.isEmpty() || selectedUser == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(dueDatePicker.getYear(), dueDatePicker.getMonth(),
                dueDatePicker.getDayOfMonth());

        taskRepository.createTask(projectId, title, description, priority,
                calendar.getTime(), selectedUser.getUserId(),
                new OperationCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        runOnUiThread(() -> {
                            Toast.makeText(AddTaskActivity.this,
                                    "Task created successfully", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Toast.makeText(AddTaskActivity.this,
                                    "Error creating task: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }
}