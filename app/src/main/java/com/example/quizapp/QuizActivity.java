package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    // UI Components
    private TextView questionCounterTextView;
    private TextView timerTextView;
    private ProgressBar progressBar;
    private ImageView questionImageView;
    private TextView questionTextView;
    private RadioGroup radioGroup;
    private RadioButton option1, option2, option3, option4;
    private Button btnNext;

    // Quiz variables
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int totalQuestions = 5;
    private int score = 0;
    private CountDownTimer timer;
    private long timeLeftInMillis = 30000; // 30 seconds per question

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        // Initialize UI components
        initializeUI();

        // Edge-to-edge inset handling
        applyEdgeToEdgeInsetHandling();

        // Fetch Questions
        db = FirebaseFirestore.getInstance();
        questions = new ArrayList<>();
        fetchQuestionsFromFirestore();

        // Set click listener for the Next button
        btnNext.setOnClickListener(v -> validateAnswerAndProceed());
    }

    private void fetchQuestionsFromFirestore() {
        db.collection("questions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        questions.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Question q = document.toObject(Question.class);
                            questions.add(q);
                        }
                        totalQuestions = questions.size();
                        if (totalQuestions > 0) {
                            displayQuestion(currentQuestionIndex);
                            startTimer();
                        } else {
                            Toast.makeText(this, "No questions found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch questions.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initializeUI() {
        questionCounterTextView = findViewById(R.id.questionCounter);
        timerTextView = findViewById(R.id.timer);
        progressBar = findViewById(R.id.progressBar);
        questionImageView = findViewById(R.id.questionImage);
        questionTextView = findViewById(R.id.question);
        radioGroup = findViewById(R.id.radioGroup);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        btnNext = findViewById(R.id.btnNext);
    }

    private void applyEdgeToEdgeInsetHandling() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void displayQuestion(int index) {
        // Update question counter
        questionCounterTextView.setText(String.format("Question %d/%d", index + 1, totalQuestions));

        // Update progress bar
        int progress = (int) (((index + 1) / (float) totalQuestions) * 100);
        progressBar.setProgress(progress);

        // Get current question
        Question currentQuestion = questions.get(index);

        // Load image from URL (imageUrl field)
        Picasso.get()
                .load(currentQuestion.getImageUrl())
                .into(questionImageView);

        // Set question text
        questionTextView.setText(currentQuestion.getQuestionText());

        // Set options
        option1.setText(currentQuestion.getOptions().get(0));
        option2.setText(currentQuestion.getOptions().get(1));
        option3.setText(currentQuestion.getOptions().get(2));
        option4.setText(currentQuestion.getOptions().get(3));

        // Clear any selected options
        radioGroup.clearCheck();

        // Reset timer
        if (timer != null) {
            timer.cancel();
        }
        timeLeftInMillis = 30000;
        startTimer();

        // Update button text for last question
        if (index == totalQuestions - 1) {
            btnNext.setText("Finish");
        } else {
            btnNext.setText("Next");
        }
    }

    private void startTimer() {
        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateTimerText();
                validateAnswerAndProceed();
            }
        }.start();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerTextView.setText(timeFormatted);
    }

    private void validateAnswerAndProceed() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if answer is correct
        int selectedOptionId = radioGroup.getCheckedRadioButtonId();
        int selectedOptionIndex = -1;

        if (selectedOptionId == R.id.option1) selectedOptionIndex = 0;
        else if (selectedOptionId == R.id.option2) selectedOptionIndex = 1;
        else if (selectedOptionId == R.id.option3) selectedOptionIndex = 2;
        else if (selectedOptionId == R.id.option4) selectedOptionIndex = 3;

        if (selectedOptionIndex == questions.get(currentQuestionIndex).getCorrectAnswerIndex()) {
            score++;
        }

        // Move to next question or finish quiz
        if (currentQuestionIndex < totalQuestions - 1) {
            currentQuestionIndex++;
            displayQuestion(currentQuestionIndex);
        } else {
            finishQuiz();
        }
    }

    private void finishQuiz() {
        if (timer != null) {
            timer.cancel();
        }

        // Navigate to results screen
        Intent intent = new Intent(this, Score.class);
        intent.putExtra("score", score);
        intent.putExtra("totalQuestions", totalQuestions);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}