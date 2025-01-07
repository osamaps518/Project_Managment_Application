package com.hfad2.projectmanagmentapplication.repositories;


import com.hfad2.projectmanagmentapplication.models.Notification;

import java.util.Date;
import java.util.List;

public interface NotificationRepository {
    void searchNotifications(String userId, String query, OperationCallback<List<Notification>> callback);

    void removeNotification(String notificationId, OperationCallback<Boolean> callback);

    void archiveNotification(String notificationId, OperationCallback<Boolean> callback);

    void toggleArchivedVisibility(boolean showArchived,
                                  OperationCallback<List<Notification>> callback);

}
