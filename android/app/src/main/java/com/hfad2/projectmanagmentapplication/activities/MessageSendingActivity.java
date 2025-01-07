package com.hfad2.projectmanagmentapplication.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.mock.MockMessageRepository;
import com.hfad2.projectmanagmentapplication.repositories.MessageRepository;
import com.hfad2.projectmanagmentapplication.repositories.OperationCallback;
import com.hfad2.projectmanagmentapplication.repositories.VolleyMessageRepository;

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

        editTo = findViewById(R.id.edit_to);
        editFrom = findViewById(R.id.edit_from);
        editSubject = findViewById(R.id.edit_subject);
        editContent = findViewById(R.id.edit_content);

        // Set click listeners
        btnAttach.setOnClickListener(v -> pickAttachment());
        btnSend.setOnClickListener(v -> sendMessage());
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
        String to = editTo.getText().toString().trim();
        String subject = editSubject.getText().toString().trim();
        String content = editContent.getText().toString().trim();

        if (to.isEmpty() || subject.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSend.setEnabled(false);

        repository.sendMessage(currentUserId, projectId, subject, content,
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