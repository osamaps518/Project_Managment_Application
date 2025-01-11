package com.hfad2.projectmanagmentapplication.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.activities.manager.AddTaskActivity;
import com.hfad2.projectmanagmentapplication.config.APIConfig;
import com.hfad2.projectmanagmentapplication.models.CardData;
import com.hfad2.projectmanagmentapplication.models.Task;
import com.hfad2.projectmanagmentapplication.models.TaskComment;
import com.hfad2.projectmanagmentapplication.models.TaskStatus;
import com.hfad2.projectmanagmentapplication.repositories.OperationCallback;
import com.hfad2.projectmanagmentapplication.repositories.ProgressTrackingRepository;
import com.hfad2.projectmanagmentapplication.repositories.VolleyProgressTrackingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Activity for tracking and managing task progress within a project. This activity displays a list
 * of tasks using a RecyclerView and provides functionality for filtering, searching, and managing
 * tasks through various UI components.
 * <p>
 * Core features:
 * - Display tasks in card format using {@link CardAdapter}
 * - Task filtering by status
 * - Task searching by title
 * - Adding new tasks
 * - Managing task comments
 * - Removing tasks
 * <p>
 * Layout file: activity_track_progress.xml
 */
public class ProgressTrackingActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private MaterialToolbar toolbar;
    private ImageButton btnMenu, btnSearch, btnFilter;
    private TextView toolbarTitle;
    private ProgressTrackingRepository repository;
    private CardAdapter adapter;
    private List<CardData> taskCards;
    private String projectManagerId;
    private String projectId;
    private int viewMode;
    private SearchView searchView;
    private boolean isSearchActive = false;
    private Spinner statusFilterSpinner;

    // Decide which activity have opened this activity (from navigation or from project card) to show the appropriate tasks
    public static final String EXTRA_VIEW_MODE = "view_mode";
    public static final int VIEW_MODE_ALL_TASKS = 1;  // Opened from navigation
    public static final int VIEW_MODE_PROJECT_TASKS = 2;  // Opened from project card

    // TODO: Make sure that you actually need the projectId, otherwise remove it
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ProgressTrackingActivity", "onCreate started");
        setContentView(R.layout.activity_track_progress);


        try {
            viewMode = getIntent().getIntExtra(EXTRA_VIEW_MODE, VIEW_MODE_ALL_TASKS);
            projectManagerId = getIntent().getStringExtra(APIConfig.PARAM_MANAGER_ID);
            // Always need manager ID for authorization
            projectManagerId = getIntent().getStringExtra(APIConfig.PARAM_MANAGER_ID);
            if (projectManagerId == null) {
                Toast.makeText(this, "Manager ID required", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Only get project ID if we're in project-specific mode
            if (viewMode == VIEW_MODE_PROJECT_TASKS) {
                projectId = getIntent().getStringExtra(APIConfig.PARAM_PROJECT_ID);
                if (projectId == null) {
                    Toast.makeText(this, "Project ID required for project view", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            }

            initialize();
            setupRecyclerView();
            loadTasks();
            setupSearch();
            setupStatusFilter();
        } catch (Exception e) {
            Log.e("ProgressTrackingActivity", "Error during initialization", e);
        }
        Log.d("ProgressTrackingActivity", "onCreate completed");
    }


    /**
     * Initializes all UI components and sets up the repository.
     * - Sets up toolbar with search and filter buttons
     * - Initializes RecyclerView
     * - Sets up SearchView and StatusFilterSpinner (initially hidden)
     * - Configures FAB for adding new tasks
     */
    private void initialize() {
        repository = new VolleyProgressTrackingRepository(this);

        recyclerView = findViewById(R.id.recycler_tasks);
        fabAdd = findViewById(R.id.fab_add);

        toolbar = findViewById(R.id.toolbar);
        btnMenu = toolbar.findViewById(R.id.btn_menu);
        btnSearch = toolbar.findViewById(R.id.btn_search);
        btnFilter = toolbar.findViewById(R.id.btn_filter);
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle.setText(R.string.nav_progress);

        searchView = findViewById(R.id.search_view);
        searchView.setQueryHint("Search tasks...");
        searchView.setVisibility(View.GONE);
        // Set filter spinner for task status
        statusFilterSpinner = toolbar.findViewById(R.id.filter_spinner);
        ArrayAdapter<TaskStatus> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, TaskStatus.values());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusFilterSpinner.setAdapter(spinnerAdapter);
        // Hidden by default until filter button is clicked
        statusFilterSpinner.setVisibility(View.GONE);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTaskActivity.class);
            intent.putExtra(APIConfig.PARAM_MANAGER_ID, projectManagerId);
            intent.putExtra(EXTRA_VIEW_MODE, viewMode);
            if (viewMode == VIEW_MODE_PROJECT_TASKS) {
                intent.putExtra(APIConfig.PARAM_PROJECT_ID, projectId);
            }
            startActivity(intent);
        });
    }

    /**
     * Configures the RecyclerView with CardAdapter and sets up click listeners.
     * - Initializes empty task list
     * - Sets LinearLayoutManager
     * - Configures item click to open TaskDetailActivity
     * - Sets up more options menu for each task
     */
    private void setupRecyclerView() {
        taskCards = new ArrayList<>();
        adapter = new CardAdapter(this, taskCards);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(this, TaskDetailActivity.class);
            Task task = (Task) item.getData();
            intent.putExtra("task_id", task.getTaskId());
            intent.putExtra("project_manager_id", projectManagerId);
            startActivity(intent);
        });

        adapter.setOnMoreClickListener((item, view) -> showPopupMenu(view, item));
    }

    /**
     * Loads all tasks for the current project from the repository.
     * For each task, creates a CardData object containing:
     * - Title (line1)
     * - Assigned employee name (line2)
     * - Project ID (line3)
     * - Status icon
     */
    private void loadTasks() {
        repository.getAllTasks(projectManagerId, new OperationCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                if (viewMode == VIEW_MODE_PROJECT_TASKS) {
                    // Filter tasks for specific project
                    List<Task> projectTasks = tasks.stream()
                            .filter(task -> task.getProjectId().equals(projectId))
                            .collect(Collectors.toList());
                    updateTaskList(projectTasks);
                } else {
                    // Show all tasks
                    updateTaskList(tasks);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ProgressTrackingActivity.this,
                        "Error loading tasks: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateTaskList(List<Task> tasks){
        taskCards.clear();
        for (Task task : tasks) {
            CardData card = new CardData();
            card.setLine1(task.getTitle());
            card.setLine2(task.getAssignedEmployee().getFullName());
            Log.d("Name of the employee:","Name:" + task.getAssignedEmployee().getUserName());
            card.setLine3(task.getProject().getTitle());
            card.setImage(getStatusIcon(task.getStatus()));
            card.setData(task);
            taskCards.add(card);
        }
        adapter.notifyDataSetChanged();
    }
    /**
     * Returns appropriate drawable resource based on task status.
     *
     * @param status The TaskStatus to get an icon for
     * @return Drawable representing the task status
     */
    private Drawable getStatusIcon(TaskStatus status) {
        int iconRes;
        switch (status) {
            case IN_PROGRESS:
                iconRes = R.drawable.ic_incomplete_circle;
                break;
            case BLOCKED:
                iconRes = R.drawable.block;
                break;
            case COMPLETED:
                iconRes = R.drawable.ic_done;
                break;
            default:
                iconRes = R.drawable.ic_todo;
        }
        return ContextCompat.getDrawable(this, iconRes);
    }

    /**
    * Shows a popup menu for a task with options
     * to remove, view comments, or mark as complete.
     *
    * @param view The view to anchor the popup menu
    * @param item The CardData item representing the task
    */
    private void showPopupMenu(View view, CardData item) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.inflate(R.menu.menu_task);

        popup.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();
            Task task = (Task) item.getData();

            if (itemId == R.id.action_remove) {
                removeTask(task.getTaskId());
                return true;
            } else if (itemId == R.id.action_comments) {
                showComments(task);
                return true;
            } else if (itemId == R.id.action_complete) {
                markTaskComplete(task.getTaskId());
                return true;
            }
            return false;
        });

        popup.show();
    }



    /**
     * Marks a task as complete by updating its status in the repository.
     * On success, reloads the task list and shows a success message.
     * On error, shows an error message.
     *
     * @param taskId The ID of the task to mark as complete
     */
    private void markTaskComplete(String taskId) {
        repository.markTaskAsComplete(taskId, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadTasks();
                Toast.makeText(ProgressTrackingActivity.this,
                        "Task marked as complete", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ProgressTrackingActivity.this,
                        "Error marking task complete: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Removes task from project using repository.
     * Refreshes task list on success.
     *
     * @param taskId ID of task to remove
     */
    private void removeTask(String taskId) {
        repository.removeTask(taskId, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadTasks();
                Toast.makeText(ProgressTrackingActivity.this,
                        "Task removed successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ProgressTrackingActivity.this,
                        "Error removing task: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Shows bottom sheet dialog for task comments.
     * Features:
     * - RecyclerView displaying existing comments
     * - Input field for new comments
     * - Send button for adding comments
     *
     * @param task Task to show/add comments for
     */
    private void showComments(Task task) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        View commentView = getLayoutInflater().inflate(R.layout.bottom_sheet_comments, null);

        RecyclerView commentsRecycler = commentView.findViewById(R.id.recycler_comments);
        EditText commentInput = commentView.findViewById(R.id.comment_input);
        ImageButton sendButton = commentView.findViewById(R.id.btn_send_comment);

        commentsRecycler.setLayoutManager(new LinearLayoutManager(this));
        CommentAdapter adapter = new CommentAdapter(this, task.getComments());
        commentsRecycler.setAdapter(adapter);

        sendButton.setOnClickListener(v -> {
            String content = commentInput.getText().toString().trim();
            if (!content.isEmpty()) {
                TaskComment newComment = new TaskComment(task.getAssignedEmployee().getUserDetails(), task, content);
                task.getComments().add(newComment);
                adapter.addComment(newComment);
                commentInput.setText("");
                commentsRecycler.scrollToPosition(adapter.getItemCount() - 1);
            }
        });

        bottomSheet.setContentView(commentView);
        bottomSheet.show();
    }


    /**
     * Configures search functionality in toolbar.
     * - Shows/hides SearchView
     * - Handles search queries
     * - Manages toolbar title visibility
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
            loadTasks();  // Reset to show all tasks
            return true;
        });
    }

    /**
     * Performs search on tasks using repository.
     * Updates UI with filtered results.
     *
     * @param query Search query string
     */
    private void performSearch(String query) {
        if (query.isEmpty()) {
            loadTasks();
            return;
        }

        repository.searchTasks(projectManagerId, query, new OperationCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                taskCards.clear();
                for (Task task : tasks) {
                    CardData card = new CardData();
                    card.setLine1(task.getTitle());
                    card.setLine2(task.getAssignedEmployee().getUserName());
                    card.setLine3(task.getProject().getTitle());
                    card.setImage(getStatusIcon(task.getStatus()));
                    card.setData(task);
                    taskCards.add(card);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ProgressTrackingActivity.this,
                        "Search failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sets up status filter spinner in toolbar.
     * - Shows/hides spinner
     * - Handles status selection
     * - Updates task list based on selected status
     */
    private void setupStatusFilter() {
        btnFilter.setOnClickListener(v -> {
            if (statusFilterSpinner.getVisibility() == View.VISIBLE) {
                statusFilterSpinner.setVisibility(View.GONE);
                btnSearch.setVisibility(View.VISIBLE);
            } else {
                statusFilterSpinner.setVisibility(View.VISIBLE);
                btnSearch.setVisibility(View.GONE);
            }
        });

        statusFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TaskStatus selectedStatus = (TaskStatus) parent.getItemAtPosition(position);
                filterByStatus(selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadTasks();
            }
        });
    }

    /**
     * Filters tasks by status using repository.
     * Updates UI with filtered results.
     *
     * @param status TaskStatus to filter by
     */
    private void filterByStatus(TaskStatus status) {
        repository.filterTasksByStatus(projectManagerId, status, new OperationCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                taskCards.clear();
                for (Task task : tasks) {
                    CardData card = new CardData();
                    card.setLine1(task.getTitle());
                    card.setLine2(task.getAssignedEmployee().getUserName());
                    card.setLine3(task.getProject().getTitle());
                    card.setImage(getStatusIcon(task.getStatus()));
                    card.setData(task);
                    taskCards.add(card);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ProgressTrackingActivity.this,
                        "Filter failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
