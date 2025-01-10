package com.hfad2.projectmanagmentapplication.activities;

import static com.hfad2.projectmanagmentapplication.utils.DateUtils.formatTimestamp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.models.Notification;
import com.hfad2.projectmanagmentapplication.repositories.MessageRepository;
import com.hfad2.projectmanagmentapplication.repositories.OperationCallback;
import com.hfad2.projectmanagmentapplication.repositories.VolleyMessageRepository;

/**
 * Activity for displaying full email content from notifications.
 * Provides options to reply, archive, and navigate email details.
 */
public class EmailDetailActivity extends BaseProjectActivity {
    private String notificationId;
    private Notification emailNotification;
    private MaterialToolbar toolbar;
    private TextView txtSender, txtSubject, txtContent, txtTimestamp;
    private ImageButton btnBack;
    private MessageRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_detail);

        notificationId = getIntent().getStringExtra("notification_id");
        if (notificationId == null) {
            finish();
            return;
        }

        initializeViews();
        loadEmailContent();
    }


    private void initializeViews() {
        repository = new VolleyMessageRepository(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove the default back button from ActionBar since there's custom one
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        txtSender = findViewById(R.id.text_sender);
        txtSubject = findViewById(R.id.text_subject);
        txtContent = findViewById(R.id.text_content);
        txtTimestamp = findViewById(R.id.text_timestamp);

        // Set up the back button
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Properly finish the activity and return to previous screen
        finish();
    }

    private void loadEmailContent() {
        repository.getEmailContent(notificationId, new OperationCallback<Notification>() {
            @Override
            public void onSuccess(Notification notification) {
                emailNotification = notification;
                updateUI();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(EmailDetailActivity.this,
                        "Error loading email: " + error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateUI() {
        if (emailNotification == null) {
            Log.e("EmailDetail", "Attempting to update UI with null notification");
            return;
        }

        txtSender.setText(emailNotification.getSenderName());
        txtSubject.setText(emailNotification.getTitle());
        txtTimestamp.setText(formatTimestamp(emailNotification.getTimestamp()));
        txtContent.setText(emailNotification.getContent());
    }
}