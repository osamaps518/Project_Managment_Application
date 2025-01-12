package com.hfad2.projectmanagmentapplication.activities.manager;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.models.Project;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {
    // Define our own interface for click events
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public interface OnAddMemberClickListener {
        void onAddMemberClick(Project project);
    }
    private List<Project> projects;
    private OnItemClickListener listener;  // Note: Now using our own interface
    private OnAddMemberClickListener addMemberListener;

    public ProjectAdapter(List<Project> projects) {
        this.projects = projects;
    }


//    @Override
//    public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.activity_project_adapter, parent, false);
//        return new ProjectViewHolder(view, listener);  // Pass listener to ViewHolder
//    }
    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_project_adapter, parent, false);
        return new ProjectViewHolder(view, listener, addMemberListener, projects);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(ProjectViewHolder holder, int position) {
        Project project = projects.get(position);
        holder.titleTextView.setText(project.getTitle());
        holder.descriptionTextView.setText(project.getDescription());
        holder.startDateTextView.setText(project.getStartDate().toString());
        holder.dueDateTextView.setText(project.getDueDate().toString());
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    // Modified ViewHolder to handle clicks
    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, startDateTextView, dueDateTextView;
        Button btnAddMember;

        public ProjectViewHolder(View itemView, final OnItemClickListener listener, final OnAddMemberClickListener addMemberListener,
                                 final List<Project> projects) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            startDateTextView = itemView.findViewById(R.id.startDateTextView);
            dueDateTextView = itemView.findViewById(R.id.dueDateTextView);
            btnAddMember = itemView.findViewById(R.id.btnAddMember);


            // Set up click listener for the entire item view
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
            btnAddMember.setOnClickListener(v -> {
                if (addMemberListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    addMemberListener.onAddMemberClick(projects.get(getAdapterPosition()));
                }
            });
        }
    }

    // Method to set click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnAddMemberClickListener(OnAddMemberClickListener listener) {
        this.addMemberListener = listener;
    }
}