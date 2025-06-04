package com.example.quizapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private List<Score.LeaderboardEntry> leaderboardList;
    private Context context;

    public LeaderboardAdapter(List<Score.LeaderboardEntry> leaderboardList) {
        this.leaderboardList = leaderboardList;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_leaderboard, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        Score.LeaderboardEntry entry = leaderboardList.get(position);
        int rank = position + 1;

        // Set rank with appropriate styling
        holder.rankTextView.setText(String.valueOf(rank));

        // Set rank background and emoji based on position
        if (rank == 1) {
            holder.rankTextView.setBackgroundColor(0xFFFFD700); // Gold
            holder.rankEmoji.setText("🥇");
            holder.rankEmoji.setVisibility(View.VISIBLE);
        } else if (rank == 2) {
            holder.rankTextView.setBackgroundColor(0xFFC0C0C0); // Silver
            holder.rankEmoji.setText("🥈");
            holder.rankEmoji.setVisibility(View.VISIBLE);
        } else if (rank == 3) {
            holder.rankTextView.setBackgroundColor(0xFFCD7F32); // Bronze
            holder.rankEmoji.setText("🥉");
            holder.rankEmoji.setVisibility(View.VISIBLE);
        } else {
            holder.rankTextView.setBackgroundColor(0xFF00796B); // Teal
            holder.rankEmoji.setVisibility(View.GONE);
        }

        // Set player email - IMPORTANT CHANGE HERE
        String playerEmail = entry.getPlayerEmail();
        // Log the email to see what's coming from Firebase
        System.out.println("Player email from Firebase: " + playerEmail);
        holder.playerEmailTextView.setText(playerEmail != null ? playerEmail : "Guest");

        // Set score
        holder.scoreTextView.setText(String.valueOf(entry.getScore()));

        // Add alternating row colors for better readability
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(0xFFF8F9FA); // Light gray
        } else {
            holder.itemView.setBackgroundColor(0xFFFFFFFF); // White
        }

        // Add subtle animation
        holder.itemView.setAlpha(0f);
        holder.itemView.animate()
                .alpha(1f)
                .setDuration(300)
                .setStartDelay(position * 50)
                .start();
    }

    @Override
    public int getItemCount() {
        return leaderboardList != null ? leaderboardList.size() : 0;
    }

    // Inner ViewHolder class
    public static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        public TextView rankTextView;
        public TextView rankEmoji;
        public TextView playerEmailTextView;
        public TextView scoreTextView;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize all views
            rankTextView = itemView.findViewById(R.id.rankTextView);
            rankEmoji = itemView.findViewById(R.id.rankEmoji);
            playerEmailTextView = itemView.findViewById(R.id.playerEmailTextView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
        }
    }

    // Method to update the list
    public void updateLeaderboard(List<Score.LeaderboardEntry> newList) {
        this.leaderboardList = newList;
        notifyDataSetChanged();
    }
}