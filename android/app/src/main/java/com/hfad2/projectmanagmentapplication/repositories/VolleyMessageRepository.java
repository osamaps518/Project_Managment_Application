package com.hfad2.projectmanagmentapplication.repositories;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hfad2.projectmanagmentapplication.config.APIConfig;
import com.hfad2.projectmanagmentapplication.models.Notification;
import com.hfad2.projectmanagmentapplication.models.NotificationType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository class for handling message-related operations using Volley for network requests.
 * This class provides methods to retrieve email content and send messages.
 */
public class VolleyMessageRepository implements MessageRepository {
    private final RequestQueue queue;
    private final Context context;

    /**
     * Constructs a new VolleyMessageRepository with the specified context.
     *
     * @param context The context to use for creating the request queue.
     */
    public VolleyMessageRepository(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }


    /**
     * Retrieves the email content for a specific notification.
     *
     * @param notificationId The ID of the notification to retrieve the email content for.
     * @param callback       The callback to handle the response or error.
     */
    @Override
    public void getEmailContent(String notificationId, OperationCallback<Notification> callback) {
        String url = APIConfig.GET_EMAIL_CONTENT + "?" + APIConfig.PARAM_NOTIFICATION_ID + "=" + notificationId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> parseNotification(response, callback),
                error -> handleVolleyError(error, callback));

        queue.add(request);
    }

    /**
     * Parses the notification response from the server and invokes the callback with the result.
     *
     * @param response The response string from the server.
     * @param callback The callback to handle the parsed notification or error.
     */
    private void parseNotification(String response, OperationCallback<Notification> callback) {
        try {
            JSONObject obj = new JSONObject(response);

            if (obj.has("error")) {
                callback.onError(obj.getString("message"));
                return;
            }

            Notification notification = new Notification(
                    obj.getString("notification_id"),
                    NotificationType.EMAIL,
                    obj.getString("sender_name"),
                    obj.getString("title"),
                    new Date(obj.getLong("timestamp") * 1000), // Convert Unix timestamp
                    obj.getString("sender_id"),
                    obj.getString("task_id")
            );
            notification.setContent(obj.getString("content"));
            notification.setArchived(obj.getBoolean("is_archived"));

            callback.onSuccess(notification);
        } catch (JSONException e) {
            callback.onError(APIConfig.ERROR_PARSE + ": " + e.getMessage());
        }
    }

    /**
     * Handles Volley errors and invokes the callback with an appropriate error message.
     *
     * @param error    The VolleyError encountered during the request.
     * @param callback The callback to handle the error.
     */
    private void handleVolleyError(VolleyError error, OperationCallback<?> callback) {
        String message = APIConfig.ERROR_NETWORK;
        if (error instanceof TimeoutError) {
            message = APIConfig.ERROR_TIMEOUT;
        }
        callback.onError(message + ": " + error.getMessage());
    }

    /**
     * Sends a message with the specified details.
     *
     * @param senderId  The ID of the sender.
     * @param projectId The ID of the project.
     * @param subject   The subject of the message.
     * @param content   The content of the message.
     * @param callback  The callback to handle the response or error.
     */
    @Override
    public void sendMessage(String senderId, String projectId, String subject, String content,
                            OperationCallback<Boolean> callback) {
        StringRequest request = new StringRequest(Request.Method.POST, APIConfig.SEND_MESSAGE,
                response -> parseBasicResponse(response, callback),
                error -> handleVolleyError(error, callback)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sender_id", senderId);
                params.put("project_id", projectId);
                params.put("title", subject);
                params.put("content", content);
                params.put("type", "EMAIL");
                return params;
            }
        };

        queue.add(request);
    }

    /**
     * Parses a basic response from the server and invokes the callback with the result.
     *
     * @param response The response string from the server.
     * @param callback The callback to handle the parsed result or error.
     */
    private void parseBasicResponse(String response, OperationCallback<Boolean> callback) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            callback.onSuccess(!jsonResponse.getBoolean("error"));
        } catch (JSONException e) {
            callback.onError(APIConfig.ERROR_PARSE + ": " + e.getMessage());
        }
    }
}