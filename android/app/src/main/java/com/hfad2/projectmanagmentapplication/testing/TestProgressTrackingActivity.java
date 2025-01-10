package com.hfad2.projectmanagmentapplication.testing;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hfad2.projectmanagmentapplication.activities.ProgressTrackingActivity;

public class TestProgressTrackingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, ProgressTrackingActivity.class);
        intent.putExtra("project_manager_id", "user002");
        startActivity(intent);
        finish();
    }
}