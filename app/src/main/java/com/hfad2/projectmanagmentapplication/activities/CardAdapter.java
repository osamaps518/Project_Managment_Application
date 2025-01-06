package com.hfad2.projectmanagmentapplication.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.hfad2.projectmanagmentapplication.R;
import com.hfad2.projectmanagmentapplication.models.CardData;

import java.util.List;

/**
 * Adapter for displaying card items in a RecyclerView. Supports team members, notifications,
 * and progress tracking displays using a common card layout.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private Context context;
    private List<CardData> items;
    private OnItemClickListener listener;
    private OnMoreClickListener moreListener;

    public CardAdapter(Context context, List<CardData> items) {
        this.items = items;
        this.context = context;
    }

    /**
     * Creates new ViewHolder by inflating the card layout.
     *
     * @param parent   The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View (not used in this implementation)
     * @return A new ViewHolder that holds a View of the given view type
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MaterialCardView cardView = (MaterialCardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item, parent, false);
        return new ViewHolder(cardView);
    }

    /**
     * Binds data from a CardData item to the ViewHolder's views.
     * Handles image loading via Glide and sets up click listeners.
     *
     * @param holder   The ViewHolder to update
     * @param position The position of the item in the list
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CardData item = items.get(position);
        MaterialCardView cardView = holder.cardView;

        ImageView circleContent = cardView.findViewById(R.id.circle_content);
        TextView textLine1 = cardView.findViewById(R.id.text_line1);
        TextView textLine2 = cardView.findViewById(R.id.text_line2);
        TextView textLine3 = cardView.findViewById(R.id.text_line3);
        ImageButton btnMore = cardView.findViewById(R.id.button_more);

        // Use Glide for image loading
        if (item.getImageUrl() != null) {
            Glide.with(context).load(item.getImageUrl()).into(circleContent);
        } else if (item.getImage() != null) {
            circleContent.setImageDrawable(item.getImage());
        }

        textLine1.setText(item.getLine1());
        textLine2.setText(item.getLine2());
        textLine3.setText(item.getLine3());

        cardView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });

        btnMore.setOnClickListener(v -> {
            if (moreListener != null) moreListener.onMoreClick(item, v);
        });
    }

    /**
     * Returns the total number of items in the data set.
     *
     * @return The total number of items
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * ViewHolder class for caching view references to improve RecyclerView performance.
     * Holds a MaterialCardView and provides access to its child views.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;

        public ViewHolder(MaterialCardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnMoreClickListener(OnMoreClickListener listener) {
        this.moreListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(CardData item);
    }

    public interface OnMoreClickListener {
        void onMoreClick(CardData item, View view);
    }
}