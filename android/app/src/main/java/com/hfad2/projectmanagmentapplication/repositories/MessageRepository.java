package com.hfad2.projectmanagmentapplication.repositories;

import com.hfad2.projectmanagmentapplication.models.Notification;

public interface MessageRepository {
    void sendMessage(String senderId, String projectId, String subject, String content,
                     OperationCallback<Boolean> callback);

    void getEmailContent(String notificationId, OperationCallback<Notification> callback);
}
