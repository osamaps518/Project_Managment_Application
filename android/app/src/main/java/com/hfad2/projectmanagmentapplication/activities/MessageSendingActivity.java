package com.hfad2.projectmanagmentapplication.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.models.Project;
import com.hfad2.projectmanagmentapplication.models.User;
import com.hfad2.projectmanagmentapplication.repositories.MessageRepository;
import com.hfad2.projectmanagmentapplication.repositories.OperationCallback;
import com.hfad2.projectmanagmentapplication.repositories.VolleyMessageRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Activity for composing and sending messages to project team members.
 * Handles message composition, attachments, and integration with MessageRepository.
 */
public class MessageSendingActivity extends BaseProjectActivity {
    private static final int PICK_ATTACHMENT_REQUEST = 1;

    private MaterialToolbar toolbar;
    private ImageButton btnMenu, btnAttach, btnSend;
    private TextInputEditText editTo, editFrom, editSubject, editContent;
    private MessageRepository repository;
    private Uri attachmentUri;
    private String currentUserId;
    private String projectId;

    // Spinner for selecting project and recipient
    private Spinner projectSpinner;
    private Spinner recipientSpinner;
    private List<Project> userProjects;
    private List<User> projectMembers;

    /**
     * Initializes activity state and UI components. Validates required user and project IDs.
     * Sets up views, repository, and handles any incoming reply intents.
     *
     * @param savedInstanceState Bundle containing activity's previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_messages);

        currentUserId = getIntent().getStringExtra("user_id");
        projectId = getIntent().getStringExtra("project_id");

        if (currentUserId == null || projectId == null) {
            Toast.makeText(this, "Missing required information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initialize();
        setupToolbar();
        handleIntent();
    }


    /**
     * Initializes all view components and sets up click listeners.
     * Creates MessageRepository instance and binds UI elements.
     */
    private void initialize() {
        repository = new VolleyMessageRepository(this);

        // Initialize views
        toolbar = findViewById(R.id.message_toolbar);
        btnMenu = toolbar.findViewById(R.id.btn_menu);
        btnAttach = toolbar.findViewById(R.id.btn_attach);
        btnSend = toolbar.findViewById(R.id.btn_send);

//        editTo = findViewById(R.id.edit_to);
//        editFrom = findViewById(R.id.edit_from);
        TextView textFrom = findViewById(R.id.text_from);

        editSubject = findViewById(R.id.edit_subject);
        editContent = findViewById(R.id.edit_content);

        // Set click listeners
        btnAttach.setOnClickListener(v -> pickAttachment());
        btnSend.setOnClickListener(v -> sendMessage());

        // Bind project and recipient spinners
        projectSpinner = findViewById(R.id.project_spinner);
        recipientSpinner = findViewById(R.id.recipient_spinner);

        loadUserProjects();

        projectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Project selectedProject = userProjects.get(position);
                Log.d("MessageSending", "Selected project: " + selectedProject.getTitle());

                // Clear current recipient selection
                recipientSpinner.setAdapter(null);

                // Show loading state
                Toast.makeText(MessageSendingActivity.this,
                        "Loading team members...", Toast.LENGTH_SHORT).show();

                // Load members for selected project
                projectId = selectedProject.getProjectId();
                loadProjectMembers(projectId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("MessageSending", "No project selected");
                recipientSpinner.setAdapter(null);
                recipientSpinner.setEnabled(false);
            }
        });
    }

    /**
     * Loads the projects associated with the current user.
     * Fetches the list of projects from the repository and populates the project spinner.
     * Displays a toast message in case of an error.
     */
    private void loadUserProjects() {
        repository.getUserProjects(currentUserId, new OperationCallback<List<Project>>() {
            @Override
            public void onSuccess(List<Project> projects) {
                Log.d("MessageSending", "Loaded " + projects.size() + " projects");
                userProjects = projects;

                // Create adapter with spinner-specific layout
                ArrayAdapter<Project> adapter = new ArrayAdapter<>(
                        MessageSendingActivity.this,
                        android.R.layout.simple_spinner_item,
                        projects
                );

                // Specify the dropdown layout
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Set adapter and default selection on main thread
                runOnUiThread(() -> {
                    projectSpinner.setAdapter(adapter);

                    // Add some visual feedback
                    projectSpinner.setPrompt("Select Project");

                    // If we have projects, select the first one
                    if (!projects.isEmpty()) {
                        projectSpinner.setSelection(0);
                        // Load members for the first project
                        loadProjectMembers(projects.get(0).getProjectId());
                    }

                    // Make spinner enabled only if we have items
                    projectSpinner.setEnabled(!projects.isEmpty());
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(MessageSendingActivity.this,
                            "Error loading projects: " + error, Toast.LENGTH_SHORT).show();
                    projectSpinner.setEnabled(false);
                });
            }
        });
    }

    /**
     * Loads the members of the specified project.
     * Fetches the list of project members from the repository and populates the recipient spinner.
     * Displays a toast message in case of an error.
     *
     * @param projectId The ID of the project whose members are to be loaded
     */
    private void loadProjectMembers(String projectId) {
        Log.d("MessageSending", "Loading members for project: " + projectId);
        repository.getProjectMembers(projectId, new OperationCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> members) {
                // Find the current user's information first
                User currentUser = members.stream()
                        .filter(user -> user.getUserId().equals(currentUserId))
                        .findFirst()
                        .orElse(null);

                // Update the "From" field if we found the current user
                if (currentUser != null) {
                    TextView textFrom = findViewById(R.id.text_from);
                    runOnUiThread(() -> {
                        textFrom.setText(currentUser.getFullName() + " <" + currentUser.getEmail() + ">");
                    });
                }

                // Then filter out current user for recipients list
                List<User> filteredMembers = members.stream()
                        .filter(user -> !user.getUserId().equals(currentUserId))
                        .collect(Collectors.toList());

                Log.d("MessageSending", "Received " + filteredMembers.size() + " members (after filtering)");
                projectMembers = filteredMembers;

                // setup recipient spinner to show members who are not the current user
                ArrayAdapter<User> adapter = new ArrayAdapter<>(
                        MessageSendingActivity.this,
                        android.R.layout.simple_spinner_item,
                        filteredMembers
                );

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                runOnUiThread(() -> {
                    recipientSpinner.setAdapter(adapter);
                    recipientSpinner.setEnabled(!filteredMembers.isEmpty());

                    if (!filteredMembers.isEmpty()) {
                        recipientSpinner.setSelection(0);
                    }
                });
            }


            @Override
            public void onError(String error) {
                Log.e("MessageSending", "Error loading members: " + error);
                runOnUiThread(() -> {
                    Toast.makeText(MessageSendingActivity.this,
                            "Error loading members: " + error, Toast.LENGTH_SHORT).show();
                    recipientSpinner.setEnabled(false);
                });
            }
        });
    }
    /**
     * Configures toolbar with custom title and visibility settings.
     * Sets up action bar without default title.
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.nav_messages);
    }

    /**
     * Processes incoming intent for reply scenarios.
     * Populates recipient and subject fields if replying to existing message.
     */
    private void handleIntent() {
        String replyTo = getIntent().getStringExtra("reply_to");
        String subject = getIntent().getStringExtra("subject");

        if (replyTo != null) {
            editTo.setText(replyTo);
        }
        if (subject != null) {
            editSubject.setText(subject);
        }
    }

    /**
     * Launches file picker intent for message attachments.
     * Allows selection of any file type.
     */
    private void pickAttachment() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_ATTACHMENT_REQUEST);
    }

    /**
     * Handles attachment selection result.
     * Stores selected file URI and shows confirmation toast.
     *
     * @param requestCode The request code passed to startActivityForResult()
     * @param resultCode  The result code returned by the child activity
     * @param data        An Intent containing the result data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_ATTACHMENT_REQUEST && resultCode == RESULT_OK) {
            attachmentUri = data.getData();
            Toast.makeText(this, "Attachment added", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Validates and sends message using MessageRepository.
     * Handles success/failure scenarios with appropriate user feedback.
     * Disables send button during transmission to prevent duplicate sends.
     */
    private void sendMessage() {
        // Get selected recipient from spinner
        User selectedRecipient = (User) recipientSpinner.getSelectedItem();
        String subject = editSubject.getText().toString().trim();
        String content = editContent.getText().toString().trim();

        if (selectedRecipient == null || subject.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSend.setEnabled(false);

        repository.sendMessage(currentUserId, selectedRecipient.getUserId(),projectId, subject, content,
                new OperationCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        runOnUiThread(() -> {
                            Toast.makeText(MessageSendingActivity.this,
                                    "Message sent successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            btnSend.setEnabled(true);
                            Toast.makeText(MessageSendingActivity.this,
                                    "Failed to send message: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }
}