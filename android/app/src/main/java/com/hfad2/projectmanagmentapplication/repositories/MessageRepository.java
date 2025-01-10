package com.hfad2.projectmanagmentapplication.repositories;

import com.hfad2.projectmanagmentapplication.models.Notification;
import com.hfad2.projectmanagmentapplication.models.Project;
import com.hfad2.projectmanagmentapplication.models.User;

import java.util.List;

public interface MessageRepository {
    void sendMessage(String senderId, String reciptentId, String projectId, String subject, String content,
                     OperationCallback<Boolean> callback);

    void getEmailContent(String notificationId, OperationCallback<Notification> callback);
    void getUserProjects(String userId, OperationCallback<List<Project>> callback);
    void getProjectMembers(String projectId, OperationCallback<List<User>> callback);
}
