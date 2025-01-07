package com.hfad2.projectmanagmentapplication.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.hfad2.projectmanagmentapplication.R;

public class NavigationManager {
    private final Context context;
    private final String currentUserId;
    private final DrawerLayout drawerLayout;
    private final NavigationView navigationView;

    public NavigationManager(Activity activity, String userId) {
        this.context = activity;
        this.currentUserId = userId;
        this.drawerLayout = activity.findViewById(R.id.drawer_layout);
        this.navigationView = activity.findViewById(R.id.nav_view);
        setupNavigation();
    }

    private void setupNavigation() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = new Intent(context, NotificationActivity.class);
            intent.putExtra("user_id", currentUserId);

            if (itemId == R.id.nav_team_members) {
                intent = new Intent(context, TeamMembersActivity.class);
            } else if (itemId == R.id.nav_progress) {
                intent = new Intent(context, TrackProgressActivity.class);
            } else if (itemId == R.id.nav_notifications) {
                intent = new Intent(context, NotificationActivity.class);
            } else if (itemId == R.id.nav_messages) {
                intent = new Intent(context, SendMessagesActivity.class);
            }

            if (intent != null) {
                intent.putExtra("user_id", currentUserId);
                context.startActivity(intent);
                drawerLayout.closeDrawers();
            }
            return true;
        });
    }

    public void toggleDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}
