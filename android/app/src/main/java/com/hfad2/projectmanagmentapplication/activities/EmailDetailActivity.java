package com.hfad2.projectmanagmentapplication.activities;

import static com.hfad2.projectmanagmentapplication.utils.DateUtils.formatTimestamp;

import android.content.Intent;
import android.os.Bundle;
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
    private ImageButton btnReply;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtSender = findViewById(R.id.text_sender);
        txtSubject = findViewById(R.id.text_subject);
        txtContent = findViewById(R.id.text_content);
        txtTimestamp = findViewById(R.id.text_timestamp);

        btnReply = toolbar.findViewById(R.id.btn_reply);

        btnReply.setOnClickListener(v -> openReplyActivity());
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
        txtSender.setText(emailNotification.getSenderName());
        txtSubject.setText(emailNotification.getTitle());
        txtTimestamp.setText(formatTimestamp(emailNotification.getTimestamp()));
        txtContent.setText(emailNotification.getContent());
    }


    private void openReplyActivity() {
        Intent intent = new Intent(this, MessageSendingActivity.class);
        intent.putExtra("reply_to", emailNotification.getSenderId());
        intent.putExtra("subject", "Re: " + emailNotification.getTitle());
        startActivity(intent);
    }
}