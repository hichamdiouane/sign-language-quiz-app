# Sign Language Quiz App

A feature-rich Android quiz application built with Java and Firebase integration, offering an engaging quiz experience focused on sign languages with real-time scoring and leaderboard functionality.

## Features

- 🔐 User Authentication
  - Email/Password login
  - User registration
  - Secure Firebase authentication

- 🎯 Quiz Features
  - Questions focused on sign languages
  - Multiple choice questions with one valid answer
  - Image-based questions
  - 30-second timer per question
  - Progress tracking
  - Real-time score calculation
  - Immediate feedback on answers

- 📊 Leaderboard System
  - Global score tracking
  - User ranking
  - Performance history

- 🎨 Modern UI/UX
  - Clean and intuitive interface
  - Progress bar visualization
  - Responsive design
  - Edge-to-edge display support

## Technical Architecture

### Built With

- Java
- Firebase
  - Authentication
  - Firestore Database
- Android SDK
- Picasso (Image Loading)
- Material Design Components

### Project Structure

```
app/src/main/java/com/example/quizapp/
├── MainActivity.java           # Login screen
├── RegisterActivity.java       # User registration
├── QuizActivity.java          # Main quiz interface
├── Score.java                 # Score display and leaderboard
├── LeaderboardActivity.java   # Leaderboard display
├── LeaderboardAdapter.java    # Leaderboard list adapter
├── Question.java              # Question data model
└── Quiz1-5.java              # Quiz question sets
```

## Screenshots

Here are some screenshots of the Quiz App interfaces:

### Login Screen

![Login Screen](screens/login.jpeg)

### Registration Screen

![Registration Screen](screens/register.jpeg)

This screen allows users to log in to an existing account or create a new one.

### Quiz Screen

![Quiz Screen](screens/1.jpeg)

This is an example of quiz interface where users answer questions, see their progress, and the timer.

### Score Screen

![Score Screen](screens/score.jpeg)

After completing the quiz, the user's score is displayed on this screen, with options to log out, try again, or view the leaderboard.

### Leaderboard Screen

![Leaderboard Screen](screens/leaderboard.jpeg)

This screen shows the ranking of users based on their quiz scores.

## Getting Started

### Prerequisites

- Android Studio
- Firebase account
- Android SDK 21 or higher
- Google Play Services

### Firebase Setup

1. Create a new Firebase project
2. Add your Android app to the project
3. Download and add `google-services.json`
4. Enable Email/Password authentication
5. Set up Firestore database

### Installation

1. Clone the repository
```bash
git clone https://github.com/hichamdiouane/sign-language-quiz-app.git
```

2. Open the project in Android Studio

3. Add your `google-services.json` file to the app directory

4. Sync the project with Gradle files

5. Run the app on an emulator or physical device

## Usage

1. Launch the app
2. Create an account or log in
3. Start the quiz
4. Answer questions within the time limit
5. View your score and position on the leaderboard
6. Option to retry or exit

## Firebase Database Structure

### Collections

- `users`: User profiles and authentication data
- `questions`: Quiz questions with options and correct answers
- `scores`: User scores and leaderboard data