package com.hfad2.projectmanagmentapplication.activities;

import android.content.Context;
import android.os.Bundle;import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;import android.widget.ArrayAdapter;import android.widget.DatePicker;import android.widget.Spinner;import android.widget.TextView;import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.activities.manager.AddTaskActivity;import com.hfad2.projectmanagmentapplication.config.APIConfig;import com.hfad2.projectmanagmentapplication.models.Notification;
import com.hfad2.projectmanagmentapplication.models.Project;import com.hfad2.projectmanagmentapplication.models.TaskPriority;import com.hfad2.projectmanagmentapplication.models.User;import com.hfad2.projectmanagmentapplication.repositories.MessageRepository;import com.hfad2.projectmanagmentapplication.repositories.OperationCallback;import com.hfad2.projectmanagmentapplication.repositories.ProgressTrackingRepository;import com.hfad2.projectmanagmentapplication.repositories.VolleyMessageRepository;import com.hfad2.projectmanagmentapplication.repositories.VolleyProgressTrackingRepository;import com.hfad2.projectmanagmentapplication.utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;import java.util.Date;
import java.util.List;import java.util.stream.Collectors;
// Modified CommentAdapter.java to match existing XML layout
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private final Context context;
    private List<Notification> comments;

    public CommentAdapter(Context context, List<Notification> comments) {
        this.context = context;
        this.comments = comments;
    }

    public void setComments(List<Notification> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Notification comment = comments.get(position);

        // Format time and author together
        String timeAndAuthor = String.format("%s - %s",
                DateUtils.formatTimestamp(comment.getTimestamp()),
                comment.getSenderName());
        holder.timeView.setText(timeAndAuthor);

        // Set comment content
        holder.contentView.setText(comment.getContent());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView timeView;
        TextView contentView;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            timeView = itemView.findViewById(R.id.text_time);
            contentView = itemView.findViewById(R.id.text_content);
        }
    }

    private String formatTimestamp(String timestamp) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = inputFormat.parse(timestamp);
            return DateUtils.formatTimestamp(date);
        } catch (ParseException e) {
            return timestamp;
        }
    }
}