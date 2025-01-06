package com.hfad2.projectmanagmentapplication.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hfad2.projectmanagmentapplication.R;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        String userId = getIntent().getStringExtra("user_id");
        String projectId = getIntent().getStringExtra("project_id");

    }
}