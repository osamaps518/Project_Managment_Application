package com.hfad2.projectmanagmentapplication.activities;

import android.os.Bundle;

import com.hfad2.projectmanagmentapplication.R;

public class UserProfileActivity extends BaseProjectActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        String userId = getIntent().getStringExtra("user_id");
        String projectId = getIntent().getStringExtra("project_id");

    }
}