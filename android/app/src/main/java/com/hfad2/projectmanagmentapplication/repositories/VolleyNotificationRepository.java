package com.hfad2.projectmanagmentapplication.repositories;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hfad2.projectmanagmentapplication.config.APIConfig;
import com.hfad2.projectmanagmentapplication.models.Notification;
import com.hfad2.projectmanagmentapplication.models.NotificationType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A repository class that handles notification-related operations using the Volley library.
 * This class implements the NotificationRepository interface.
 */
public class VolleyNotificationRepository implements NotificationRepository {
    private final RequestQueue queue;
    private final SimpleDateFormat dateFormat;

    public VolleyNotificationRepository(Context context) {
        this.queue = Volley.newRequestQueue(context);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Searches for notifications for a specific user based on a query string.
     *
     * @param userId   The ID of the user whose notifications are to be searched.
     * @param query    The search query string.
     * @param callback The callback to handle the list of notifications or an error.
     */
    @Override
    public void searchNotifications(String userId, String query, OperationCallback<List<Notification>> callback) {
        String url = APIConfig.SEARCH_NOTIFICATIONS + "?user_id=" + userId +
                "&" + APIConfig.PARAM_QUERY + "=" + Uri.encode(query);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> parseNotificationList(response, callback),
                error -> handleVolleyError(error, callback));
        queue.add(request);
    }

    /**
     * Removes a notification by its ID.
     *
     * @param notificationId The ID of the notification to be removed.
     * @param callback       The callback to handle the success or failure of the operation.
     */
    @Override
    public void removeNotification(String notificationId, OperationCallback<Boolean> callback) {
        StringRequest request = new StringRequest(Request.Method.POST, APIConfig.REMOVE_NOTIFICATION,
                response -> parseBasicResponse(response, callback),
                error -> handleVolleyError(error, callback)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(APIConfig.PARAM_NOTIFICATION_ID, notificationId);
                return params;
            }
        };
        queue.add(request);
    }

    /**
     * Archives a notification by its ID.
     *
     * @param notificationId The ID of the notification to be archived.
     * @param callback       The callback to handle the success or failure of the operation.
     */
    @Override
    public void archiveNotification(String notificationId, OperationCallback<Boolean> callback) {
        StringRequest request = new StringRequest(Request.Method.POST, APIConfig.ARCHIVE_NOTIFICATION,
                response -> parseBasicResponse(response, callback),
                error -> handleVolleyError(error, callback)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(APIConfig.PARAM_NOTIFICATION_ID, notificationId);
                return params;
            }
        };
        queue.add(request);
    }

    /**
     * Toggles the visibility of archived notifications.
     *
     * @param showArchived A boolean indicating whether to show archived notifications.
     * @param callback     The callback to handle the list of notifications or an error.
     */
    @Override
    public void toggleArchivedVisibility(boolean showArchived,
                                         OperationCallback<List<Notification>> callback) {
        String url = APIConfig.GET_NOTIFICATIONS + "?" +
                APIConfig.PARAM_SHOW_ARCHIVED + "=" + showArchived;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> parseNotificationList(response, callback),
                error -> handleVolleyError(error, callback));
        queue.add(request);
    }

    /**
     * Parses the response to create a list of notifications.
     *
     * @param response The JSON response string containing the notifications.
     * @param callback The callback to handle the list of notifications or an error.
     */
    private void parseNotificationList(String response, OperationCallback<List<Notification>> callback) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            List<Notification> notifications = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                notifications.add(parseNotificationFromJson(jsonArray.getJSONObject(i)));
            }
            callback.onSuccess(notifications);
        } catch (JSONException e) {
            callback.onError(APIConfig.ERROR_PARSE + ": " + e.getMessage());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Parses a JSON object to create a Notification object.
     *
     * @param obj The JSON object containing notification data.
     * @return A Notification object created from the JSON data.
     * @throws JSONException If there is an error parsing the JSON data.
     * @throws ParseException If there is an error parsing the date.
     */
    private Notification parseNotificationFromJson(JSONObject obj) throws JSONException, ParseException {
        return new Notification(
                obj.getString("notification_id"),
                NotificationType.valueOf(obj.getString("type")),
                obj.getString("sender_name"),
                obj.getString("title"),
                dateFormat.parse(obj.getString("timestamp")),
                obj.getString("sender_id"),
                obj.getString("task_id")
        );
    }


    /**
     * Parses a basic response to determine success or failure.
     *
     * @param response The JSON response string.
     * @param callback The callback to handle the success or failure of the operation.
     */
    private void parseBasicResponse(String response, OperationCallback<Boolean> callback) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            callback.onSuccess(!jsonResponse.getBoolean("error"));
        } catch (JSONException e) {
            callback.onError(APIConfig.ERROR_PARSE + ": " + e.getMessage());
        }
    }

    /**
     * Handles a Volley error by passing an error message to the callback.
     *
     * @param error    The Volley error that occurred.
     * @param callback The callback to handle the error.
     */
    private void handleVolleyError(VolleyError error, OperationCallback<?> callback) {
        callback.onError(APIConfig.ERROR_NETWORK + ": " + error.getMessage());
    }
}