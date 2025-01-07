package com.hfad2.projectmanagmentapplication.repositories;

public interface MessageRepository {
    void sendMessage(String senderId, String projectId, String subject, String content,
                     OperationCallback<Boolean> callback);
}
