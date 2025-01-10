package com.hfad2.projectmanagmentapplication.testing;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.activities.ProgressTrackingActivity;
import com.hfad2.projectmanagmentapplication.activities.TeamMembersActivity;

public class TestTeamMembersActivity extends AppCompatActivity {

    // onCreate for launching the TeamMembersActivity
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Intent intent = new Intent(this, TeamMembersActivity.class);
//        intent.putExtra("project_id", "proj001");
//        startActivity(intent);
//        finish();
//    }


    // onCreate for launching the ProgressTrackingActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, ProgressTrackingActivity.class);
        intent.putExtra("project_manager_id", "user002");
        startActivity(intent);
        finish();
    }
}