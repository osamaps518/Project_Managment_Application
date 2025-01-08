package com.hfad2.projectmanagmentapplication.testing;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.activities.TeamMembersActivity;

public class TestProgressTrackingActivityy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, TeamMembersActivity.class);
        intent.putExtra("project_id", "proj001");
        startActivity(intent);
        finish();
    }
}