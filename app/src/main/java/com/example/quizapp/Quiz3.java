package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Quiz3 extends AppCompatActivity {
    private static final int CORRECT_ANSWER_ID = R.id.option2;
    private Button btnNext;
    private RadioGroup rg;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz3);

        // Initialize UI components
        btnNext = findViewById(R.id.btnNext);
        rg = findViewById(R.id.radioGroup);

        // Edge-to-edge inset handling
        applyEdgeToEdgeInsetHandling();

        btnNext.setOnClickListener(v -> validateAnswerAndProceed());
    }

    private void applyEdgeToEdgeInsetHandling() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void validateAnswerAndProceed() {
        if (rg.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        updateScore();
        navigateToNextQuiz();
    }
    private void updateScore() {
        if (rg.getCheckedRadioButtonId() == CORRECT_ANSWER_ID) {
            score += 1;
        }
    }
    private void navigateToNextQuiz() {
        Intent intent = new Intent(this, Quiz4.class);
        intent.putExtra("score", score);
        startActivity(intent);
        finish();
    }
}