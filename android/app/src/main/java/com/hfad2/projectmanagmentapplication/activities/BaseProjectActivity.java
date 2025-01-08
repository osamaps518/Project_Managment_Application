package com.hfad2.projectmanagmentapplication.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hfad2.projectmanagmentapplication.R;

public class BaseProjectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Remove this line since we're using different layouts for each activity
        // setContentView(R.layout.activity_base_project);
        // Also remove the WindowInsets setup since it's causing the crash
    }
}