package com.hfad2.projectmanagmentapplication.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.hfad2.projectmanagmentapplication.R;
import android.widget.ImageButton;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public abstract class BaseProjectActivity extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected NavigationManager navigationManager;
    protected ImageButton btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupNavigation() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnMenu = findViewById(R.id.btn_menu);

        if (drawerLayout != null && navigationView != null && btnMenu != null) {
            navigationManager = new NavigationManager(this, drawerLayout, navigationView);
            navigationManager.setupMenuButton(btnMenu);
        }
    }
}