
package com.hfad2.projectmanagmentapplication.activities;


import static com.hfad2.projectmanagmentapplication.config.APIConfig.PARAM_USER_ID;
import static com.hfad2.projectmanagmentapplication.utils.DateUtils.formatTimestamp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.models.CardData;
import com.hfad2.projectmanagmentapplication.models.Notification;
import com.hfad2.projectmanagmentapplication.models.NotificationType;
import com.hfad2.projectmanagmentapplication.repositories.NotificationRepository;
import com.hfad2.projectmanagmentapplication.repositories.OperationCallback;
import com.hfad2.projectmanagmentapplication.repositories.VolleyNotificationRepository;

import java.util.ArrayList;
import java.util.List;


/**
 * An activity that manages and displays user notifications within the project management application.
 * This activity handles two types of notifications:
 * - Comment notifications: Alerts about new comments on tasks
 * - Email notifications: System-generated email messages
 * <p>
 * The activity provides functionality for:
 * - Displaying notifications in a scrollable list using RecyclerView
 * - Filtering notifications by type (Comments/Emails)
 * - Searching notifications by title or sender
 * - Managing notification lifecycle (viewing, deleting)
 * - Navigation to related screens (task details, email details, user profiles)
 * <p>
 * Each notification is displayed as a card showing:
 * - Notification title
 * - Sender name
 * - Timestamp
 * - Type indicator icon (comment/email)
 *
 * @see Notification
 * @see NotificationRepository
 * @see CardAdapter
 */
public class NotificationActivity extends BaseProjectActivity {
    private RecyclerView recyclerView;
    private MaterialToolbar toolbar;
    private ImageButton btnMenu, btnSearch, btnFilter;
    private TextView toolbarTitle;
    private NotificationRepository repository;
    private CardAdapter adapter;
    private List<CardData> notificationCards;
    private SearchView searchView;
    private boolean isSearchActive = false;
    private boolean showArchived = false;
    private Spinner filterSpinner;
    private String userId;

    /**
     * Creates and initializes the activity's layout and components.
     * Validates user ID, initializes views and repository, and sets up
     * the notification list. Exits activity if no valid user ID provided.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId =  getIntent().getStringExtra(PARAM_USER_ID);
        if (userId == null) {
            Toast.makeText(this, "User ID required", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setContentView(R.layout.activity_notifications);
        initialize();
        setupRecyclerView();
        loadNotifications();
        setupSearch();
        setupSpinnerFilter();
    }

    /**
     * Initializes all UI components and repositories.
     * Sets up toolbar, search view, filter spinner and their respective listeners.
     */
    private void initialize() {
        repository = new VolleyNotificationRepository(this);

        recyclerView = findViewById(R.id.recycler_notifications);

        toolbar = findViewById(R.id.toolbar);
        btnMenu = toolbar.findViewById(R.id.btn_menu);
        btnSearch = toolbar.findViewById(R.id.btn_search);
        btnFilter = toolbar.findViewById(R.id.btn_filter);
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        filterSpinner = findViewById(R.id.filter_spinner);
        filterSpinner.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle.setText(R.string.nav_notifications);

        searchView = findViewById(R.id.search_view);
        searchView.setQueryHint("Search notifications...");
        searchView.setVisibility(View.GONE);


        // Create adapter with filter options
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.notification_filters, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);
        filterSpinner.setVisibility(View.GONE);
    }


    /**
     * Configures RecyclerView with adapter and click handlers.
     * Sets up click behavior for notification cards:
     * - Comment notifications open task details
     * - Email notifications open email view
     */
    private void setupRecyclerView() {
        notificationCards = new ArrayList<>();
        adapter = new CardAdapter(this, notificationCards);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Handle card clicks based on notification type
        adapter.setOnItemClickListener(item -> {
            Notification notification = (Notification) item.getData();
            if (notification.getType() == NotificationType.COMMENT) {
                // Open task details with comments
                openTaskComments(notification);
            } else {
                // Open email in full screen
                openEmail(notification);
            }
        });

        adapter.setOnMoreClickListener((item, view) -> showPopupMenu(view, item));
    }


    /**
     * Loads notifications from the repository and updates the UI.
     * This method retrieves notifications, creates card data for each notification,
     * and updates the RecyclerView adapter with the new data.
     * If an error occurs during loading, an error message is displayed.
     */
    private void loadNotifications() {
        repository.loadNotifications(userId, showArchived, new OperationCallback<List<Notification>>() {
            @Override
            public void onSuccess(List<Notification> notifications) {
                notificationCards.clear();
                for (Notification notification : notifications) {
                    CardData card = new CardData();
                    card.setLine1(notification.getTitle());
                    card.setLine2(notification.getSenderName());
                    card.setLine3(formatTimestamp(notification.getTimestamp()));
                    card.setImage(getNotificationTypeIcon(notification.getType()));
                    card.setData(notification);
                    notificationCards.add(card);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(NotificationActivity.this,
                        "Error loading notifications: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Displays popup menu for notification management options.
     * Options vary based on notification type (comment/email).
     *
     * @param view Anchor view for popup menu
     * @param item CardData containing notification information
     */
    private void showPopupMenu(View view, CardData item) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.inflate(R.menu.menu_notification);
        Notification notification = (Notification) item.getData();

        popup.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.action_delete) {
                if (notification.getType() == NotificationType.COMMENT) {
                    deleteNotification(notification.getId());
                } else {
                    archiveNotification(notification.getId());
                }
                return true;
            } else if (itemId == R.id.action_view_profile) {
                openSenderProfile(notification.getSenderId());
                return true;
            } else if (itemId == R.id.action_view_task &&
                    notification.getType() == NotificationType.COMMENT) {
                openTaskDetails(notification.getTaskId());
                return true;
            }
            return false;
        });

        popup.getMenu().findItem(R.id.action_view_task).setVisible(
                notification.getType() == NotificationType.COMMENT
        );

        popup.show();
    }

/**
 * Archives a notification by updating its status in the repository.
 * On success, reloads the notification list and shows a success message.
 * On error, shows an error message.
 *
 * @param notificationId The ID of the notification to archive
 */
private void archiveNotification(String notificationId) {
    repository.archiveNotification(notificationId, new OperationCallback<Boolean>() {
        @Override
        public void onSuccess(Boolean result) {
            loadNotifications();
            Toast.makeText(NotificationActivity.this,
                    "Notification archived", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(String error) {
            Toast.makeText(NotificationActivity.this,
                    "Error archiving notification: " + error, Toast.LENGTH_SHORT).show();
        }
    });
}
    /**
     * Opens user profile activity for notification sender.
     *
     * @param senderId ID of notification sender
     */
    private void openSenderProfile(String senderId) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("user_id", senderId);
        startActivity(intent);
    }

    /**
     * Opens task details activity for comment notifications.
     *
     * @param taskId ID of task associated with notification
     */
    private void openTaskDetails(String taskId) {
        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra("task_id", taskId);
        startActivity(intent);
    }

    /**
     * Returns appropriate icon drawable based on notification type.
     *
     * @param type NotificationType (COMMENT or EMAIL)
     * @return Drawable resource for notification icon
     */
    private Drawable getNotificationTypeIcon(NotificationType type) {
        return ContextCompat.getDrawable(this,
                type == NotificationType.COMMENT ?
                        R.drawable.ic_comment : R.drawable.ic_email
        );
    }


    /**
     * Opens task details with comments section for comment notifications.
     *
     * @param notification Comment notification to display
     */
    private void openTaskComments(Notification notification) {
        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra("task_id", notification.getTaskId());
        intent.putExtra("show_comments", true);
        startActivity(intent);
    }

    /**
     * Opens email detail view for email notifications.
     *
     * @param notification Email notification to display
     */
    private void openEmail(Notification notification) {
        Intent intent = new Intent(this, EmailDetailActivity.class);
        intent.putExtra("notification_id", notification.getId());
        startActivity(intent);
    }

    /**
     * Deletes notification and updates UI.
     *
     * @param notificationId ID of notification to delete
     */
    private void deleteNotification(String notificationId) {
        repository.removeNotification(notificationId, new OperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadNotifications(); // Refresh list
                Toast.makeText(NotificationActivity.this,
                        "Notification removed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(NotificationActivity.this,
                        "Error removing notification: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Sets up spinner for filtering notifications by type.
     * Handles filter selection changes and updates notification list.
     */
    private void setupSpinnerFilter() {
        // Handle filter button clicks
        btnFilter.setOnClickListener(v -> {
            if (filterSpinner.getVisibility() == View.VISIBLE) {
                filterSpinner.setVisibility(View.GONE);
                btnSearch.setVisibility(View.VISIBLE);
            } else {
                filterSpinner.setVisibility(View.VISIBLE);
                btnSearch.setVisibility(View.GONE);
            }
        });

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // All Notifications
                        showArchived = false;
                        loadNotifications();
                        break;
                    case 1: // Comments Only
                        filterByType(NotificationType.COMMENT);
                        break;
                    case 2: // Emails Only
                        filterByType(NotificationType.EMAIL);
                        break;
                    case 3: // Show Archived Emails
                        showArchived = true;
                        loadNotifications();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showArchived = false;
                loadNotifications();
            }
        });
    }

    /**
     * Filters notifications by specified type (comment/email).
     *
     * @param type NotificationType to filter by
     */
    private void filterByType(NotificationType type) {
        repository.loadNotifications(userId, showArchived, new OperationCallback<List<Notification>>() {
            @Override
            public void onSuccess(List<Notification> notifications) {
                notificationCards.clear();

                for (Notification notification : notifications) {
                    if (notification.getType() == type) {
                        CardData card = createNotificationCard(notification);
                        notificationCards.add(card);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(NotificationActivity.this,
                        "Error filtering notifications: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Creates CardData object from Notification.
     *
     * @param notification Source notification
     * @return CardData configured with notification details
     */
    private CardData createNotificationCard(Notification notification) {
        CardData card = new CardData();
        card.setLine1(notification.getTitle());
        card.setLine2(notification.getSenderName());
        card.setLine3(formatTimestamp(notification.getTimestamp()));
        card.setImage(getNotificationTypeIcon(notification.getType()));
        card.setData(notification);
        return card;
    }

    /**
     * Configures search functionality in toolbar.
     * Handles search view visibility and query submissions.
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
            loadNotifications();  // Reset to show all tasks
            return true;
        });
    }


    /**
     * Executes search query against notifications.
     *
     * @param query Search text to filter notifications
     */
    private void performSearch(String query) {
        if (query.isEmpty()) {
            loadNotifications();
            return;
        }

        repository.searchNotifications(userId, query,
                new OperationCallback<List<Notification>>() {
                    @Override
                    public void onSuccess(List<Notification> notifications) {
                        updateNotificationCards(notifications);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(NotificationActivity.this,
                                "Search failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Updates RecyclerView with new notification data.
     *
     * @param notifications List of notifications to display
     */
    private void updateNotificationCards(List<Notification> notifications) {
        notificationCards.clear();
        for (Notification notification : notifications) {
            CardData card = createNotificationCard(notification);
            notificationCards.add(card);
        }
        adapter.notifyDataSetChanged();
    }
}