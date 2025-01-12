package com.hfad2.projectmanagmentapplication.activities;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.activities.authentication.LoginActivity;
import com.hfad2.projectmanagmentapplication.activities.employee.EmployeeDashboardActivity;
import com.hfad2.projectmanagmentapplication.activities.manager.ManagerDashboardActivity;
import com.hfad2.projectmanagmentapplication.config.APIConfig;
import com.hfad2.projectmanagmentapplication.utils.SessionManager;

public class NavigationManager {
    private final DrawerLayout drawerLayout;
    private final NavigationView navigationView;
    private final Activity activity;

    public NavigationManager(Activity activity, DrawerLayout drawerLayout, NavigationView navigationView) {
        this.activity = activity;
        this.drawerLayout = drawerLayout;
        this.navigationView = navigationView;
        setupDrawerContent();
        updateNavigationHeader();
    }

    private void updateNavigationHeader() {
        // Update navigation header with user info
        View headerView = navigationView.getHeaderView(0);
        TextView userNameText = headerView.findViewById(R.id.nav_header_username);
        TextView userTypeText = headerView.findViewById(R.id.nav_header_usertype);

        userNameText.setText(SessionManager.getUserName());
        userTypeText.setText(SessionManager.getUserType());
    }

    public void setupMenuButton(ImageButton menuButton) {
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    private void setupDrawerContent() {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            selectDrawerItem(menuItem);
            return true;
        });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        Intent intent = null;

        if (menuItem.getItemId() == R.id.nav_dashboard) {
            // Use SessionManager to determine user type
            intent = new Intent(activity,
                    SessionManager.isManager() ? ManagerDashboardActivity.class : EmployeeDashboardActivity.class);
            // Add user ID from session
            intent.putExtra(APIConfig.PARAM_MANAGER_ID, SessionManager.getCurrentUserId());
        } else if (menuItem.getItemId() == R.id.nav_notifications) {
            intent = new Intent(activity, NotificationActivity.class);
            intent.putExtra(APIConfig.PARAM_USER_ID, SessionManager.getCurrentUserId());
        }
        else if (menuItem.getItemId() == R.id.nav_send_message) {
            intent = new Intent(activity, MessageSendingActivity.class);
        }
        else if (menuItem.getItemId() == R.id.nav_logout) {
            // Clear session data
            SessionManager.clearSession(activity);
            intent = new Intent(activity, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        if (intent != null) {
            // Preserve project_id if it exists
            String projectId = activity.getIntent().getStringExtra("project_id");
            if (projectId != null) {
                intent.putExtra("project_id", projectId);
            }
            activity.startActivity(intent);

            // Close the activity if logging out
            if (menuItem.getItemId() == R.id.nav_logout) {
                activity.finish();
            }
        }

        drawerLayout.closeDrawers();
    }
}