package com.hfad2.projectmanagmentapplication.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.models.TaskComment;

import java.text.DateFormat;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<TaskComment> comments;
    private Context context;

    public CommentAdapter(Context context, List<TaskComment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TaskComment comment = comments.get(position);
        holder.contentView.setText(comment.getContent());
        holder.timeView.setText(DateFormat.getDateTimeInstance()
                .format(comment.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void addComment(TaskComment comment) {
        comments.add(comment);
        notifyItemInserted(comments.size() - 1);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView contentView;
        TextView timeView;

        ViewHolder(View view) {
            super(view);
            contentView = view.findViewById(R.id.text_content);
            timeView = view.findViewById(R.id.text_time);
        }
    }
}