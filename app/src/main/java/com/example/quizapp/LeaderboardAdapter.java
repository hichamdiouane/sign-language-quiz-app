package com.example.quizapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private List<Score.LeaderboardEntry> leaderboardList;

    public LeaderboardAdapter(List<Score.LeaderboardEntry> list) {
        this.leaderboardList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Score.LeaderboardEntry entry = leaderboardList.get(position);
        holder.txtEmail.setText(entry.email);
        holder.txtScore.setText("Score: " + entry.score);
    }

    @Override
    public int getItemCount() {
        return leaderboardList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtEmail, txtScore;
        public ViewHolder(View itemView) {
            super(itemView);
            txtEmail = itemView.findViewById(android.R.id.text1);
            txtScore = itemView.findViewById(android.R.id.text2);
        }
    }
}