package com.hfad2.projectmanagmentapplication.activities;

import android.os.Bundle;

import com.hfad2.projectmanagmentapplication.R;

public class TaskDetailActivity extends BaseProjectActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        String employeeId = getIntent().getStringExtra("employee_id");
        String projectId = getIntent().getStringExtra("project_id");
    }
}