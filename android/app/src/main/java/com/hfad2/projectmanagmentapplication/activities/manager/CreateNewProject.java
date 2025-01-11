package com.hfad2.projectmanagmentapplication.activities.manager;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.config.APIConfig;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateNewProject extends AppCompatActivity {
    private EditText projectName, projectDescription, projectStartDate, projectDueDate;
    private Button saveButton;
    private String managerId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_project);

        managerId = getIntent().getStringExtra(APIConfig.PARAM_MANAGER_ID);
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        projectName = findViewById(R.id.projectName);
        projectDescription = findViewById(R.id.projectDescription);
        projectStartDate = findViewById(R.id.projectStartDate);
        projectDueDate = findViewById(R.id.projectDueDate);
        saveButton = findViewById(R.id.btnsave);
    }

    private void setupClickListeners() {
        projectStartDate.setOnClickListener(v -> showDatePickerDialog(projectStartDate));
        projectDueDate.setOnClickListener(v -> showDatePickerDialog(projectDueDate));
        saveButton.setOnClickListener(v -> {
            if (validateInputs()) {
                createProject();
            } else {
                Toast.makeText(CreateNewProject.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        return !projectName.getText().toString().isEmpty() &&
                !projectDescription.getText().toString().isEmpty() &&
                !projectStartDate.getText().toString().isEmpty() &&
                !projectDueDate.getText().toString().isEmpty();
    }

    private void createProject() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIConfig.ADD_PROJECT,
                response -> {
                    Toast.makeText(this, "Project created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> Toast.makeText(this, APIConfig.ERROR_NETWORK, Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(APIConfig.PARAM_MANAGER_ID, managerId);
                params.put(APIConfig.PARAM_TITLE, projectName.getText().toString());
                params.put(APIConfig.PARAM_DESCRIPTION, projectDescription.getText().toString());
                params.put(APIConfig.PARAM_START_DATE, projectStartDate.getText().toString());
                params.put(APIConfig.PARAM_DUE_DATE, projectDueDate.getText().toString());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String date = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
            editText.setText(date);
        }, year, month, day);

        datePickerDialog.show();
    }
}