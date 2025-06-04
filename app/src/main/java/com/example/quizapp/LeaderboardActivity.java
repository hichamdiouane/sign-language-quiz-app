package com.example.quizapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;
    private List<Score.LeaderboardEntry> leaderboardList = new ArrayList<>();
    private FirebaseFirestore db;
    private Button btnBack;
    private ProgressBar progressBar; // Added a progress bar for loading indicator
    private SwipeRefreshLayout swipeRefreshLayout; // Added for pull-to-refresh functionality

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();

        db = FirebaseFirestore.getInstance();
        loadLeaderboard();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewLeaderboard);
        btnBack = findViewById(R.id.btnBack);

        // Add a progress bar to your layout and initialize it here
        progressBar = findViewById(R.id.progressBar);

        // Add a SwipeRefreshLayout to your layout and initialize it here
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setColorSchemeResources(
                    android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light
            );

            swipeRefreshLayout.setOnRefreshListener(this::refreshLeaderboard);
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LeaderboardAdapter(leaderboardList);
        recyclerView.setAdapter(adapter);

        // Add item decoration for better spacing
        recyclerView.addItemDecoration(new androidx.recyclerview.widget.DividerItemDecoration(
                this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL));

        // Add scroll listener for infinite scrolling (if needed for large leaderboards)
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // You can implement infinite scrolling logic here if needed
                // Example: if last item is visible, load more data
            }
        });
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            // Add smooth finish animation
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    private void loadLeaderboard() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        db.collection("leaderboard")
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnCompleteListener(task -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);

                    if (task.isSuccessful()) {
                        leaderboardList.clear();

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            try {
                                // Get fields using the correct field names from your Firestore
                                String email = doc.getString("email");  // CHANGED: "playerEmail" to "email"
                                Long scoreObj = doc.getLong("score");
                                int score = (scoreObj != null) ? scoreObj.intValue() : 0;

                                // Create LeaderboardEntry with the retrieved data
                                Score.LeaderboardEntry entry = new Score.LeaderboardEntry(email, score);
                                leaderboardList.add(entry);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        adapter.updateLeaderboard(leaderboardList);
                    } else {
                        Toast.makeText(this, "Failed to load leaderboard", Toast.LENGTH_SHORT).show();
                        if (task.getException() != null) {
                            task.getException().printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    // Method to refresh leaderboard when pull-to-refresh is triggered
    public void refreshLeaderboard() {
        loadLeaderboard();
    }
}