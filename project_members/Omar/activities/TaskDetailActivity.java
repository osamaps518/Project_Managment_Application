package com.hfad2.projectmanagmentapplication.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.config.APIConfig;
import com.hfad2.projectmanagmentapplication.models.Task;
import com.hfad2.projectmanagmentapplication.models.TaskStatus;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TaskDetailActivity extends AppCompatActivity {
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView dueDateTextView;
    private Spinner statusSpinner;
    private String taskId;
    private Task currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        taskId = getIntent().getStringExtra("task_id");

        initializeViews();
        setupStatusSpinner();
        fetchTaskDetails();
    }

    private void initializeViews() {
        titleTextView = findViewById(R.id.task_title);
        descriptionTextView = findViewById(R.id.task_description);
        dueDateTextView = findViewById(R.id.task_due_date);
        statusSpinner = findViewById(R.id.status_spinner);
    }

    private void setupStatusSpinner() {
        ArrayAdapter<TaskStatus> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                TaskStatus.values()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
    }

    private void fetchTaskDetails() {
        String url = APIConfig.GET_TASK_DETAILS + "?task_id=" + taskId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        currentTask = new Task(
                                jsonObject.getString("task_id"),
                                jsonObject.getString("title"),
                                jsonObject.getString("description"),
                                TaskStatus.valueOf(jsonObject.getString("status")),
                                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        .parse(jsonObject.getString("due_date"))
                        );

                        updateUI(currentTask);
                    } catch (Exception e) {
                        Log.e("TaskDetails", "Error parsing task details", e);
                        Toast.makeText(this, "Error loading task details",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error occurred",
                        Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void updateUI(Task task) {
        titleTextView.setText(task.getTitle());
        descriptionTextView.setText(task.getDescription());
        dueDateTextView.setText(new SimpleDateFormat("MMM dd, yyyy",
                Locale.getDefault()).format(task.getDueDate()));

        // Set spinner selection
        int spinnerPosition = ((ArrayAdapter<TaskStatus>) statusSpinner.getAdapter())
                .getPosition(task.getStatus());
        statusSpinner.setSelection(spinnerPosition);

        // Add listener after setting initial value to avoid unnecessary update
        statusSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view,
                                       int position, long id) {
                TaskStatus newStatus = (TaskStatus) parent.getItemAtPosition(position);
                if (currentTask != null && newStatus != currentTask.getStatus()) {
                    updateTaskStatus(newStatus);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void updateTaskStatus(TaskStatus newStatus) {
        String url = APIConfig.UPDATE_TASK_STATUS;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (!jsonResponse.getBoolean("error")) {
                            currentTask.setStatus(newStatus);
                            Toast.makeText(TaskDetailActivity.this,
                                    "Task status updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TaskDetailActivity.this,
                                    "Error: " + jsonResponse.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                            // Revert spinner selection on error
                            int spinnerPosition = ((ArrayAdapter<TaskStatus>)
                                    statusSpinner.getAdapter())
                                    .getPosition(currentTask.getStatus());
                            statusSpinner.setSelection(spinnerPosition);
                        }
                    } catch (Exception e) {
                        Log.e("TaskDetails", "Error updating task status", e);
                        Toast.makeText(TaskDetailActivity.this,
                                "Error updating task status", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(TaskDetailActivity.this,
                            "Network error occurred", Toast.LENGTH_SHORT).show();
                    // Revert spinner selection on error
                    int spinnerPosition = ((ArrayAdapter<TaskStatus>)
                            statusSpinner.getAdapter())
                            .getPosition(currentTask.getStatus());
                    statusSpinner.setSelection(spinnerPosition);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("task_id", taskId);
                params.put("status", newStatus.name());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}