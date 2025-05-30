package com.example.quizapp;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        recyclerView = findViewById(R.id.recyclerViewLeaderboard);
        btnBack = findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LeaderboardAdapter(leaderboardList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadLeaderboard();

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadLeaderboard() {
        db.collection("leaderboard")
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        leaderboardList.clear();
                        for(QueryDocumentSnapshot doc : task.getResult()) {
                            Score.LeaderboardEntry entry = doc.toObject(Score.LeaderboardEntry.class);
                            leaderboardList.add(entry);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}