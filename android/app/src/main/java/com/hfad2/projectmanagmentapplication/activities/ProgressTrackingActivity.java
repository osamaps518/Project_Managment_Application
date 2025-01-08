package com.hfad2.projectmanagmentapplication.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.models.CardData;
import com.hfad2.projectmanagmentapplication.models.Task;
import com.hfad2.projectmanagmentapplication.models.TaskComment;
import com.hfad2.projectmanagmentapplication.models.TaskPriority;
import com.hfad2.projectmanagmentapplication.models.TaskStatus;
import com.hfad2.projectmanagmentapplication.repositories.OperationCallback;
import com.hfad2.projectmanagmentapplication.repositories.ProgressTrackingRepository;
import com.hfad2.projectmanagmentapplication.repositories.VolleyProgressTrackingRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


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
public class ProgressTrackingActivity extends BaseProjectActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private MaterialToolbar toolbar;
    private ImageButton btnMenu, btnSearch, btnFilter;
    private TextView toolbarTitle;
    private ProgressTrackingRepository repository;
    private CardAdapter adapter;
    private List<CardData> taskCards;
    private String projectId;
    private SearchView searchView;
    private boolean isSearchActive = false;
    private Spinner statusFilterSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectId = getIntent().getStringExtra("project_id");
        if (projectId == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_track_progress);

        initialize();
        setupRecyclerView();
        loadTasks();
        setupSearch();
        setupStatusFilter();
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

        searchView = new SearchView(this);
        searchView.setQueryHint("Search tasks...");
        searchView.setVisibility(View.GONE);

        statusFilterSpinner = new Spinner(this);
        ArrayAdapter<TaskStatus> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, TaskStatus.values());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusFilterSpinner.setAdapter(spinnerAdapter);
        statusFilterSpinner.setVisibility(View.GONE);
        fabAdd.setOnClickListener(v -> showAddTaskDialog());
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
            intent.putExtra("project_id", projectId);
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
        repository.getAllTasks(projectId, new OperationCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                taskCards.clear();
                for (Task task : tasks) {
                    CardData card = new CardData();
                    card.setLine1(task.getTitle());
                    card.setLine2(task.getAssignedEmployee().getFullName());
                    card.setLine3(task.getProject().getProjectId());
                    card.setImage(getStatusIcon(task.getStatus()));
                    card.setData(task);
                    taskCards.add(card);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ProgressTrackingActivity.this,
                        "Error loading tasks: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
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
                toolbarTitle.setVisibility(View.GONE);
                searchView.setVisibility(View.VISIBLE);
                searchView.setIconified(false);
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
            searchView.setVisibility(View.GONE);
            toolbarTitle.setVisibility(View.VISIBLE);
            isSearchActive = false;
            loadTasks();
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

        repository.searchTasks(projectId, query, new OperationCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                taskCards.clear();
                for (Task task : tasks) {
                    CardData card = new CardData();
                    card.setLine1(task.getTitle());
                    card.setLine2(task.getAssignedEmployee().getFullName());
                    card.setLine3(task.getProject().getProjectId());
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
            } else {
                statusFilterSpinner.setVisibility(View.VISIBLE);
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
        repository.filterTasksByStatus(projectId, status, new OperationCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                taskCards.clear();
                for (Task task : tasks) {
                    CardData card = new CardData();
                    card.setLine1(task.getTitle());
                    card.setLine2(task.getAssignedEmployee().getFullName());
                    card.setLine3(task.getProject().getProjectId());
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

    /**
     * Shows dialog for adding new task.
     * Dialog includes:
     * - Title input
     * - Description input
     * - Priority selector
     * - Due date picker
     * Creates new task on confirmation
     */
    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);

        EditText titleInput = dialogView.findViewById(R.id.task_title_input);
        EditText descriptionInput = dialogView.findViewById(R.id.task_description_input);
        Spinner prioritySpinner = dialogView.findViewById(R.id.priority_spinner);
        DatePicker dueDatePicker = dialogView.findViewById(R.id.due_date_picker);

        ArrayAdapter<TaskPriority> priorityAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, TaskPriority.values());
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(priorityAdapter);

        builder.setView(dialogView)
                .setTitle("Add New Task")
                .setPositiveButton("Add", (dialog, which) -> {
                    String title = titleInput.getText().toString();
                    String description = descriptionInput.getText().toString();
                    TaskPriority priority = (TaskPriority) prioritySpinner.getSelectedItem();

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(dueDatePicker.getYear(),
                            dueDatePicker.getMonth(),
                            dueDatePicker.getDayOfMonth());

                    Task newTask = repository.createTask(projectId, title, description,
                            priority, calendar.getTime());
                    loadTasks();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
