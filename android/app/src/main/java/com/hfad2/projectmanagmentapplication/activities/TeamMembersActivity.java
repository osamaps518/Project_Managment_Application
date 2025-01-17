package com.hfad2.projectmanagmentapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.config.APIConfig;
import com.hfad2.projectmanagmentapplication.models.CardData;
import com.hfad2.projectmanagmentapplication.models.Employee;
import com.hfad2.projectmanagmentapplication.models.Task;
import com.hfad2.projectmanagmentapplication.repositories.OperationCallback;
import com.hfad2.projectmanagmentapplication.repositories.TeamMembersRepository;
import com.hfad2.projectmanagmentapplication.repositories.VolleyTeamMembersRepository;
import com.hfad2.projectmanagmentapplication.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Activity for managing team members within a project.
 * Displays a list of team members using a card-based interface with search and filter capabilities.
 * Supports operations like viewing member details, removing members, and managing member tasks.
 */
public class TeamMembersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private MaterialToolbar toolbar;
    private ImageButton btnMenu, btnSearch, btnFilter;
    private TextView toolbarTitle;
    private TeamMembersRepository repository;
    private CardAdapter adapter;
    private List<CardData> memberCards;
    private String projectId;
    private SearchView searchView;
    private boolean isSearchActive = false;
    private Spinner roleFilterSpinner;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private NavigationManager navigationManager;



    /**
     * Initializes the activity's state and UI components.
     * Sets up the repository, views, toolbar, and search/filter functionality.
     * Exits the activity if no project ID is provided.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectId = getIntent().getStringExtra("project_id");
        if (projectId == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_team_members);

        initialize();
        setupRecyclerView();
        loadTeamMembers();
        setupSearch();
        setupRoleFilter();
    }

    /**
     * Initializes all view components and repositories.
     * Sets up the toolbar with its components (search, filter, notifications buttons).
     * Configures the SearchView and role filter spinner with default states.
     */
    private void initialize() {
        // Initialize repository
        repository = new VolleyTeamMembersRepository(this);

        // Initialize views
        recyclerView = findViewById(R.id.recycler_members);
        fabAdd = findViewById(R.id.fab_add);

        // Initialize toolbar and its components
        toolbar = findViewById(R.id.toolbar);
        btnMenu = toolbar.findViewById(R.id.btn_menu);
        btnSearch = toolbar.findViewById(R.id.btn_search);
        btnFilter = toolbar.findViewById(R.id.btn_filter);
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);

        // Set toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle.setText(R.string.nav_team_members);

        // Setup search functionality
        searchView = findViewById(R.id.search_view);
        searchView.setQueryHint("Search by name...");
        searchView.setVisibility(View.GONE);  // Hidden by default

        // Setup role filter spinner
        // TODO: Remember to rename this one to filter_spinner to make it more generic
        roleFilterSpinner = toolbar.findViewById(R.id.filter_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.role_filters, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleFilterSpinner.setAdapter(spinnerAdapter);
        roleFilterSpinner.setVisibility(View.GONE);  // Hidden by default

        fabAdd.setOnClickListener(v -> showAddMemberDialog());

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationManager = new NavigationManager(this, drawerLayout, navigationView);
        navigationManager.setupMenuButton(btnMenu);
    }

    /**
     * Configures the RecyclerView with its adapter and layout manager.
     * Sets up click listeners for both card items and more options button.
     * Card clicks navigate to user profile, more options show a popup menu.
     */
    private void setupRecyclerView() {
        memberCards = new ArrayList<>();
        adapter = new CardAdapter(this, memberCards);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Handle card clicks
        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(this, UserProfileActivity.class);
            Employee employee = (Employee) item.getData();
            intent.putExtra("user_id", employee.getUserId());
            intent.putExtra("project_id", projectId);
            startActivity(intent);
        });

        // Handle three dots button click menu
        adapter.setOnMoreClickListener((item, view) -> {
            showPopupMenu(view, item);
        });
    }

    /**
     * Loads team members from the repository and updates the UI.
     * Converts Employee objects to CardData format for display.
     * Shows error toast if loading fails.
     */
    private void loadTeamMembers() {
        repository.getAllMembers(projectId, new OperationCallback<List<Employee>>() {
            @Override
            public void onSuccess(List<Employee> employees) {
                memberCards.clear();
                for (Employee employee : employees) {
                    CardData card = new CardData();
                    card.setLine1(employee.getUserName());
                    card.setLine2(employee.getRole());
                    card.setLine3("");
                    card.setImage(ContextCompat.getDrawable(TeamMembersActivity.this, getProfileImageResource(employee.getProfileImage())));

                    card.setData(employee);

                    memberCards.add(card);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TeamMembersActivity.this,
                        "Error loading members: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Returns the resource ID for the profile image based on the image name.
     * Currently uses a switch statement to map image names to drawable resources.
     * Default icon is used if the image name is not recognized.
     *
     * @param imageName Name of the profile image file
     * @return Resource ID of the profile image
     */
    private int getProfileImageResource(String imageName) {
        switch (imageName) {
            case "profile1.jpg": return R.drawable.ic_person;
            case "profile2.jpg": return R.drawable.ic_group;
            case "profile3.jpg": return R.drawable.ic_person;
            case "profile4.jpg": return R.drawable.ic_person;
            default: return R.drawable.ic_person; // Default icon
        }
    }
    /**
     * Displays a popup menu for member-specific actions.
     * Menu includes options to remove member, view assigned task, and mark as inactive.
     * Dynamically shows/hides "View Task" option based on task assignment status.
     *
     * @param view The view (more options button) that triggered the popup
     * @param item CardData object containing the member's information
     */
    private void showPopupMenu(View view, CardData item) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.inflate(R.menu.menu_team_member);

        popup.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.action_remove) {
                removeMember(((Employee) item.getData()).getUserId());
                return true;
            }
            return false;
        });
        popup.show();
    }

    /**
     * Removes a team member from the project.
     * Refreshes the member list on successful removal.
     * Shows success/error toast messages based on operation result.
     *
     * @param employeeId ID of the employee to remove
     */
    private void removeMember(String employeeId) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Team Member")
                .setMessage("Are you sure you want to remove this team member from the project?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    Log.d("TeamMembers", "Attempting to remove member: " + employeeId + " from project: " + projectId);
                    repository.removeMember(projectId, employeeId, new OperationCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            Log.d("TeamMembers", "Remove member result: " + result);
                            loadTeamMembers(); // Refresh list
                            Toast.makeText(TeamMembersActivity.this,
                                    "Member removed successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            Log.e("TeamMembers", "Error removing member: " + error);
                            Toast.makeText(TeamMembersActivity.this,
                                    "Error removing member: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Configures search functionality in the toolbar.
     * Manages SearchView visibility and toolbar title states.
     * Sets up listeners for search query changes and search view closure.
     */
    private void setupSearch() {
        btnSearch.setOnClickListener(v -> {
            if (!isSearchActive) {
                // Hide title, show search view
                toolbarTitle.setVisibility(View.GONE);
                btnSearch.setVisibility(View.GONE);
                btnFilter.setVisibility(View.GONE);
                btnMenu.setVisibility(View.GONE);
                searchView.setVisibility(View.VISIBLE);
                searchView.setIconified(false);  // Automatically show keyboard
                isSearchActive = true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform real-time search as user types
                performSearch(newText);
                return true;
            }
        });

        searchView.setOnCloseListener(() -> {
            // Restore original toolbar state
            searchView.setVisibility(View.GONE);
            toolbarTitle.setVisibility(View.VISIBLE);
            btnMenu.setVisibility(View.VISIBLE);
            btnSearch.setVisibility(View.VISIBLE);
            btnFilter.setVisibility(View.VISIBLE);
            isSearchActive = false;
            loadTeamMembers();  // Reset to show all members
            return true;
        });
    }

    /**
     * Executes member search based on query string.
     * Shows all members if query is empty.
     * Updates UI with filtered results matching the query.
     *
     * @param query Search string to filter members by
     */
    private void performSearch(String query) {
        if (query.isEmpty()) {
            loadTeamMembers();  // Show all if query is empty
            return;
        }

        repository.searchMembers(projectId, query, new OperationCallback<List<Employee>>() {
            @Override
            public void onSuccess(List<Employee> employees) {
                memberCards.clear();
                for (Employee employee : employees) {
                    CardData card = new CardData();
                    card.setLine1(employee.getFullName());
                    card.setLine2(employee.getUserName());
                    card.setData(employee);
                    memberCards.add(card);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TeamMembersActivity.this,
                        "Search failed: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sets up role filtering functionality.
     * Configures spinner with role options and visibility toggle.
     * Updates member list based on selected role filter.
     */
    private void setupRoleFilter() {
        btnFilter.setOnClickListener(v -> {
            // Show/hide role filter spinner
            if (roleFilterSpinner.getVisibility() == View.VISIBLE) {
                roleFilterSpinner.setVisibility(View.GONE);
                btnSearch.setVisibility(View.VISIBLE);
            } else {
                roleFilterSpinner.setVisibility(View.VISIBLE);
                btnSearch.setVisibility(View.GONE);
            }
        });

        roleFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRole = parent.getItemAtPosition(position).toString();
                if (selectedRole.equals("All Roles")) {
                    loadTeamMembers();  // Reset to show all
                } else {
                    filterByRole(selectedRole);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadTeamMembers();  // Reset to show all
            }
        });
    }

    /**
     * Filters team members by selected role.
     * Updates UI with filtered member list.
     * Shows error toast if filtering fails.
     *
     * @param role Role to filter members by
     */
    private void filterByRole(String role) {
        repository.filterMembersByRole(projectId, role, new OperationCallback<List<Employee>>() {
            @Override
            public void onSuccess(List<Employee> employees) {
                memberCards.clear();
                for (Employee employee : employees) {
                    CardData card = new CardData();
                    card.setLine1(employee.getFullName());
                    card.setLine2(employee.getUserName());
                    card.setData(employee);
                    memberCards.add(card);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(TeamMembersActivity.this,
                        "Filter failed: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddMemberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_member, null);


        SearchView searchView = dialogView.findViewById(R.id.search_view);
        Spinner userSpinner = dialogView.findViewById(R.id.user_spinner);
        TextView userInfoView = dialogView.findViewById(R.id.user_info);

        // This will hold our currently selected employee
        final Employee[] selectedEmployee = new Employee[1];
        searchAvailableUsers("", userSpinner, userInfoView);


        // Configure the SearchView
        searchView.setQueryHint("Type to search users...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAvailableUsers(query, userSpinner, userInfoView);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 2) { // Start search after 2 characters
                    searchAvailableUsers(newText, userSpinner, userInfoView);
                }
                return true;
            }
        });

        // Handle spinner selection
        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedEmployee[0] = (Employee) parent.getItemAtPosition(position);
                userInfoView.setText("Selected: " + selectedEmployee[0].getFullName());
                userInfoView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedEmployee[0] = null;
                userInfoView.setVisibility(View.GONE);
            }
        });

        // Create and show the dialog
        builder.setView(dialogView)
                .setTitle("Add Team Member")
                .setPositiveButton("Add", (dialog, which) -> {
                    if (selectedEmployee[0] != null) {
                        addMemberToProject(selectedEmployee[0]);
                    } else {
                        Toast.makeText(this, "Please select a user first", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void searchAvailableUsers(String query, Spinner spinner, TextView infoView) {
        repository.searchAvailableUsers(projectId, query, new OperationCallback<List<Employee>>() {
            @Override
            public void onSuccess(List<Employee> employees) {
                if (employees.isEmpty()) {
                    runOnUiThread(() -> {
                        infoView.setText("No users found");
                        infoView.setVisibility(View.VISIBLE);
                        spinner.setAdapter(null);
                    });
                    return;
                }

                ArrayAdapter<Employee> adapter = new ArrayAdapter<>(
                        TeamMembersActivity.this,
                        android.R.layout.simple_spinner_item,
                        employees
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                runOnUiThread(() -> {
                    spinner.setAdapter(adapter);
                    infoView.setVisibility(View.GONE);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    infoView.setText("Error: " + error);
                    infoView.setVisibility(View.VISIBLE);
                    spinner.setAdapter(null);
                });
            }
        });
    }

    private void addMemberToProject(Employee employee) {
        repository.addMember(projectId, employee.getUserId(),
                new OperationCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        loadTeamMembers();
                        Toast.makeText(TeamMembersActivity.this,
                                "Member added successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(TeamMembersActivity.this,
                                "Failed to add member: " + error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
