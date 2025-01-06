package com.hfad2.projectmanagmentapplication.mock;


import android.os.Handler;
import android.os.Looper;

import com.hfad2.projectmanagmentapplication.models.Notification;
import com.hfad2.projectmanagmentapplication.models.NotificationType;
import com.hfad2.projectmanagmentapplication.models.Task;
import com.hfad2.projectmanagmentapplication.models.TaskComment;
import com.hfad2.projectmanagmentapplication.repositories.NotificationRepository;
import com.hfad2.projectmanagmentapplication.repositories.OperationCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// Mock Implementation
public class MockNotificationRepository implements NotificationRepository {
    private final MockDataGenerator mockData;
    private final Handler mainHandler;
    private final List<Notification> mockNotifications;

    public MockNotificationRepository() {
        this.mockData = new MockDataGenerator();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.mockNotifications = generateMockNotifications();
    }

    private List<Notification> generateMockNotifications() {
        List<Notification> notifications = new ArrayList<>();
        // Generate notifications from mock data tasks and comments
        for (Task task : mockData.getTasks()) {
            for (TaskComment comment : task.getComments()) {
                notifications.add(new Notification(
                        UUID.randomUUID().toString(),
                        NotificationType.COMMENT,
                        comment.getAuthor().getFullName(),
                        "New comment on task: " + task.getTitle(),
                        comment.getTimestamp()
                ));
            }
        }
        return notifications;
    }

    @Override
    public void getNotifications(String userId, OperationCallback<List<Notification>> callback) {
        mainHandler.postDelayed(() -> {
            try {
                callback.onSuccess(mockNotifications);
            } catch (Exception e) {
                callback.onError("Failed to load notifications: " + e.getMessage());
            }
        }, 500);
    }

    @Override
    public void searchNotifications(String userId, String query, OperationCallback<List<Notification>> callback) {
        mainHandler.postDelayed(() -> {
            try {
                List<Notification> filtered = mockNotifications.stream()
                        .filter(n -> n.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                                n.getSenderName().toLowerCase().contains(query.toLowerCase()))
                        .collect(Collectors.toList());
                callback.onSuccess(filtered);
            } catch (Exception e) {
                callback.onError("Search failed: " + e.getMessage());
            }
        }, 300);
    }

    @Override
    public void filterByTimeRange(String userId, Date startDate, Date endDate,
                                  OperationCallback<List<Notification>> callback) {
        mainHandler.postDelayed(() -> {
            try {
                List<Notification> filtered = mockNotifications.stream()
                        .filter(n -> !n.getTimestamp().before(startDate) &&
                                !n.getTimestamp().after(endDate))
                        .collect(Collectors.toList());
                callback.onSuccess(filtered);
            } catch (Exception e) {
                callback.onError("Filter failed: " + e.getMessage());
            }
        }, 300);
    }

    @Override
    public void removeNotification(String notificationId, OperationCallback<Boolean> callback) {
        mainHandler.postDelayed(() -> {
            try {
                mockNotifications.removeIf(n -> n.getId().equals(notificationId));
                callback.onSuccess(true);
            } catch (Exception e) {
                callback.onError("Remove failed: " + e.getMessage());
            }
        }, 500);
    }
}
