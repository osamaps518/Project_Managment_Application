package com.hfad2.projectmanagmentapplication.mock;

import android.os.Handler;
import android.os.Looper;

import com.hfad2.projectmanagmentapplication.repositories.MessageRepository;
import com.hfad2.projectmanagmentapplication.repositories.OperationCallback;

public class MockMessageRepository implements MessageRepository {
    private final Handler mainHandler;

    public MockMessageRepository() {
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void sendMessage(String senderId, String projectId, String subject, String content,
                            OperationCallback<Boolean> callback) {
        mainHandler.postDelayed(() -> {
            try {
                // Simulate message sending
                callback.onSuccess(true);
            } catch (Exception e) {
                callback.onError("Failed to send message: " + e.getMessage());
            }
        }, 1000);
    }
}