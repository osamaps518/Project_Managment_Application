package com.hfad2.projectmanagmentapplication.repositories;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hfad2.projectmanagmentapplication.config.APIConfig;
import com.hfad2.projectmanagmentapplication.models.Notification;
import com.hfad2.projectmanagmentapplication.models.NotificationType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolleyTaskCommentRepository implements TaskCommentRepository {
    private final RequestQueue queue;
    private final Context context;

    public VolleyTaskCommentRepository(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    @Override
    public void getTaskComments(String taskId, OperationCallback<List<Notification>> callback) {
        String url = APIConfig.GET_NOTIFICATIONS + "?task_id=" + taskId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        List<Notification> comments = new ArrayList<>();

                        // Filter notifications that are comments for this task
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            if (obj.getString("type").equals("COMMENT") &&
                                    obj.getString("task_id").equals(taskId)) {
                                comments.add(parseNotification(obj));
                            }
                        }
                        callback.onSuccess(comments);
                    } catch (JSONException | ParseException e) {
                        callback.onError("Error parsing comments: " + e.getMessage());
                    }
                },
                error -> callback.onError("Network error: " + error.getMessage()));

        queue.add(request);
    }

    @Override
    public void addTaskComment(String taskId, String senderId, String projectId,
                               String receiverId, String content,
                               OperationCallback<Boolean> callback) {
        StringRequest request = new StringRequest(Request.Method.POST, APIConfig.SEND_MESSAGE,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        callback.onSuccess(!jsonResponse.getBoolean("error"));
                    } catch (JSONException e) {
                        callback.onError("Error parsing response: " + e.getMessage());
                    }
                },
                error -> callback.onError("Network error: " + error.getMessage())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sender_id", senderId);
                params.put("project_id", projectId);
                params.put("receiver_id", receiverId);
                params.put("task_id", taskId);
                params.put("title", "New comment on task");
                params.put("content", content);
                params.put("type", "COMMENT");
                return params;
            }
        };

        queue.add(request);
    }

    private Notification parseNotification(JSONObject obj) throws JSONException, ParseException {
        Notification notification = new Notification(
                obj.getString("notification_id"),
                NotificationType.COMMENT,
                obj.getString("sender_name"),
                obj.getString("title"),
                obj.getString("timestamp"),
                obj.getString("sender_id"),
                obj.getString("receiver_id"),
                obj.getString("task_id")
        );
        notification.setContent(obj.getString("content"));
        return notification;
    }
}
