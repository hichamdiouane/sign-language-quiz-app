package com.example.quizapp;

import java.util.List;

public class Question {
    private String questionText;
    private List<String> options; // Use List<String> instead of String[]
    private int correctAnswerIndex;
    private String imageUrl;

    public Question() { }

    public Question(String questionText, List<String> options, int correctAnswerIndex, String imageUrl) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.imageUrl = imageUrl;
    }

    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public int getCorrectAnswerIndex() { return correctAnswerIndex; }
    public String getImageUrl() { return imageUrl; }
}