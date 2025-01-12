package com.hfad2.projectmanagmentapplication.repositories;

import com.hfad2.projectmanagmentapplication.models.Notification;

import java.util.List;

public interface TaskCommentRepository {
    void getTaskComments(String taskId, OperationCallback<List<Notification>> callback);
    void addTaskComment(String taskId, String senderId, String projectId, String receiverId,
                        String content, OperationCallback<Boolean> callback);
}