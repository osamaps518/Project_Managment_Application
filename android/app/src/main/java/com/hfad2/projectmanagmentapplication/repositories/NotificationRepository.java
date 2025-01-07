package com.hfad2.projectmanagmentapplication.repositories;


import com.hfad2.projectmanagmentapplication.models.Notification;

import java.util.Date;
import java.util.List;

public interface NotificationRepository {
    void getNotifications(String userId, OperationCallback<List<Notification>> callback);

    void searchNotifications(String userId, String query, OperationCallback<List<Notification>> callback);

    void filterByTimeRange(String userId, Date startDate, Date endDate, OperationCallback<List<Notification>> callback);

    void removeNotification(String notificationId, OperationCallback<Boolean> callback);

    void archiveNotification(String notificationId, OperationCallback<Boolean> callback);

    void toggleArchivedVisibility(boolean showArchived,
                                  OperationCallback<List<Notification>> callback);

    void getEmailContent(String notificationId, OperationCallback<Notification> callback);
}
