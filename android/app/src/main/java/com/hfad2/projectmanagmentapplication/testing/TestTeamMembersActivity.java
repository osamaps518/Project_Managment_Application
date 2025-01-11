package com.hfad2.projectmanagmentapplication.testing;

import static com.hfad2.projectmanagmentapplication.config.APIConfig.PARAM_PROJECT_ID;
import static com.hfad2.projectmanagmentapplication.config.APIConfig.PARAM_USER_ID;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.activities.MessageSendingActivity;
import com.hfad2.projectmanagmentapplication.activities.NotificationActivity;
import com.hfad2.projectmanagmentapplication.activities.ProgressTrackingActivity;
import com.hfad2.projectmanagmentapplication.activities.TeamMembersActivity;
import com.hfad2.projectmanagmentapplication.config.APIConfig;

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
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Intent intent = new Intent(this, ProgressTrackingActivity.class);
//        intent.putExtra("project_manager_id", "user002");
//        startActivity(intent);
//        finish();
//    }

    // onCreate for launching the NotificationActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra(PARAM_USER_ID, "user001");
        startActivity(intent);
        finish();
    }

    // onCreate for launching the sendMessageActivity
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Intent intent = new Intent(this, MessageSendingActivity.class);
//        intent.putExtra(PARAM_USER_ID, "user002");  // Sarah Manager
//        intent.putExtra(PARAM_PROJECT_ID, "proj001"); // Mobile App Development project
//        startActivity(intent);
//        finish();
//    }



    // TODO: this is the code for launching the ProgressTrackingActivity to view all tasks for a project manager
    // For all tasks view (from navigation)
    //Intent intent = new Intent(context, ProgressTrackingActivity.class);
    //intent.putExtra(APIConfig.PARAM_MANAGER_ID, managerId);
    //intent.putExtra(EXTRA_VIEW_MODE, VIEW_MODE_ALL_TASKS);
    //startActivity(intent);

    // TODO: this is the code for launching the ProgressTrackingActivity to view all tasks for a specific project
    //Intent intent = new Intent(context, ProgressTrackingActivity.class);
    //intent.putExtra(APIConfig.PARAM_MANAGER_ID, managerId);
    //intent.putExtra(APIConfig.PARAM_PROJECT_ID, projectId);
    //intent.putExtra(EXTRA_VIEW_MODE, VIEW_MODE_PROJECT_TASKS);
    //intent.putExtra("project_name", projectName);  // Optional, for toolbar title
    //startActivity(intent);
}