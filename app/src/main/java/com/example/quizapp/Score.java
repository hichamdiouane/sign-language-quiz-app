package com.example.quizapp;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Score extends AppCompatActivity {

    private static final int TOTAL_QUESTIONS = 5;
    private TextView txtScore;
    private ProgressBar progressBar;
    private Button btnLogout, btnTryAgain, btnLeaderboard;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Edge-to-edge inset handling
        applyEdgeToEdgeInsetHandling();

        // Initialize views
        txtScore = findViewById(R.id.txtScorePercent);
        progressBar = findViewById(R.id.progressBar);
        btnLogout = findViewById(R.id.btnLogout);
        btnTryAgain = findViewById(R.id.btnTryAgain);
        btnLeaderboard = findViewById(R.id.btnLeaderboard);

        // Get score from previous activity
        int finalScore = getIntent().getIntExtra("score", 0);

        // Calculate percentage
        int percentage = calculatePercentage(finalScore);

        // UI
        updateProgressVisuals(progressBar, txtScore, percentage);

        // Setup button click listeners
        setupButtonActions();

        // Save high Score
        saveHighScore(finalScore);
    }

    private void applyEdgeToEdgeInsetHandling() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private int calculatePercentage(int score) {
        return (int) (((float) score / TOTAL_QUESTIONS) * 100);
    }

    private void setupButtonActions() {
        btnLogout.setOnClickListener(v -> {
            // Clear back stack and return to login
            mAuth.signOut();
            Toast.makeText(this, "Logged out successfully.",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnTryAgain.setOnClickListener(v -> {
            // Restart quiz flow
            Intent intent = new Intent(this, QuizActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        btnLeaderboard.setOnClickListener(v -> {
            startActivity(new Intent(this, LeaderboardActivity.class));
        });
    }

    private void saveHighScore(int score) {
        String uid = mAuth.getCurrentUser().getUid();
        String email = mAuth.getCurrentUser().getEmail();

        DocumentReference docRef = db.collection("leaderboard").document(uid);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            Integer previousHigh = null;
            if(documentSnapshot.exists()) {
                previousHigh = documentSnapshot.getLong("score") != null ? documentSnapshot.getLong("score").intValue() : null;
            }
            if (previousHigh == null || score > previousHigh) {
                // Only update if the new score is higher
                docRef.set(new LeaderboardEntry(email, score));
            }
        });
    }

    private void updateProgressVisuals(ProgressBar progressBar, TextView txtScore, int percentage) {
        ValueAnimator animator = ValueAnimator.ofInt(0, percentage);
        animator.setDuration(1500);
        animator.addUpdateListener(valueAnimator -> {
            int animatedValue = (int) valueAnimator.getAnimatedValue();
            progressBar.setProgress(animatedValue);
            txtScore.setText(String.format("%d%%", animatedValue));
        });
        animator.start();
    }

    // Create a POJO class for leaderboard entries
    static class LeaderboardEntry {
        public String email;
        public int score;

        public LeaderboardEntry() {} // Needed for Firestore
        public LeaderboardEntry(String email, int score) {
            this.email = email;
            this.score = score;
        }
    }
}