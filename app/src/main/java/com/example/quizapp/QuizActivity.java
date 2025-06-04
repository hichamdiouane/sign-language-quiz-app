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
    private static final long TIMER_DURATION = 30000; // 30 seconds per question
    private long timeLeftInMillis = TIMER_DURATION;
    private boolean isAnswerValidated = false; // Flag to prevent multiple validations

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
        btnNext.setOnClickListener(v -> {
            if (!isAnswerValidated) {
                validateAnswerAndProceed();
            }
        });
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
        // Reset validation flag
        isAnswerValidated = false;

        // Update question counter
        questionCounterTextView.setText(String.format("Question %d/%d", index + 1, totalQuestions));

        // Update progress bar
        int progress = (int) (((index + 1) / (float) totalQuestions) * 100);
        progressBar.setProgress(progress);

        // Get current question
        Question currentQuestion = questions.get(index);

        // Load image from URL (imageUrl field)
        if (currentQuestion.getImageUrl() != null && !currentQuestion.getImageUrl().isEmpty()) {
            questionImageView.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(currentQuestion.getImageUrl())
                    .into(questionImageView);
        } else {
            questionImageView.setVisibility(View.GONE);
        }

        // Set question text
        questionTextView.setText(currentQuestion.getQuestionText());

        // Set options (with null checks)
        List<String> options = currentQuestion.getOptions();
        if (options != null && options.size() >= 4) {
            option1.setText(options.get(0));
            option2.setText(options.get(1));
            option3.setText(options.get(2));
            option4.setText(options.get(3));
        }

        // Clear any selected options
        radioGroup.clearCheck();

        // Start fresh timer for this question
        startTimer();

        // Update button text for last question
        if (index == totalQuestions - 1) {
            btnNext.setText("Finish");
        } else {
            btnNext.setText("Next");
        }
    }

    private void startTimer() {
        // Cancel existing timer if running
        if (timer != null) {
            timer.cancel();
        }

        // Reset time for new question
        timeLeftInMillis = TIMER_DURATION;

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

                // Only auto-proceed if answer hasn't been validated yet
                if (!isAnswerValidated) {
                    Toast.makeText(QuizActivity.this, "Time's up!", Toast.LENGTH_SHORT).show();
                    validateAnswerAndProceed();
                }
            }
        }.start();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerTextView.setText(timeFormatted);

        // Optional: Change timer color when time is running low
        if (timeLeftInMillis <= 10000) { // Last 10 seconds
            timerTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            timerTextView.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    private void validateAnswerAndProceed() {
        // Prevent multiple validations
        if (isAnswerValidated) {
            return;
        }

        isAnswerValidated = true;

        // Stop the timer
        if (timer != null) {
            timer.cancel();
        }

        // Check if an answer was selected
        int selectedOptionId = radioGroup.getCheckedRadioButtonId();
        if (selectedOptionId == -1) {
            // No answer selected - treat as incorrect
            Toast.makeText(this, "No answer selected - moving to next question", Toast.LENGTH_SHORT).show();
        } else {
            // Check if answer is correct
            int selectedOptionIndex = getSelectedOptionIndex(selectedOptionId);

            if (selectedOptionIndex != -1 &&
                    selectedOptionIndex == questions.get(currentQuestionIndex).getCorrectAnswerIndex()) {
                score++;
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show();
            }
        }

        // Move to next question or finish quiz after a short delay
        new CountDownTimer(1500, 1500) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                proceedToNextQuestion();
            }
        }.start();
    }

    private int getSelectedOptionIndex(int selectedOptionId) {
        if (selectedOptionId == R.id.option1) return 0;
        else if (selectedOptionId == R.id.option2) return 1;
        else if (selectedOptionId == R.id.option3) return 2;
        else if (selectedOptionId == R.id.option4) return 3;
        return -1;
    }

    private void proceedToNextQuestion() {
        if (currentQuestionIndex < totalQuestions - 1) {
            currentQuestionIndex++;
            displayQuestion(currentQuestionIndex);
        } else {
            finishQuiz();
        }
    }

    private void finishQuiz() {
        // Make sure timer is cancelled
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
    protected void onPause() {
        super.onPause();
        // Pause timer when activity is paused
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restart timer when activity resumes (if quiz is still active)
        if (!isAnswerValidated && questions != null && !questions.isEmpty()) {
            startTimer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}