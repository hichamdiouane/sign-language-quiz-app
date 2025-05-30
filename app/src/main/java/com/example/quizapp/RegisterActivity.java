package com.example.quizapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private CircleImageView profileImage;
    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private Button btn_signin;
    private Uri selectedImageUri;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        profileImage = findViewById(R.id.profile_image);
        etEmail = findViewById(R.id.sign_up_email);
        etPassword = findViewById(R.id.sign_up_password);
        etConfirmPassword = findViewById(R.id.conf_password);
        btnSignUp = findViewById(R.id.sign_up_button);
        btn_signin = findViewById(R.id.btn_signin);
    }

    private void setupClickListeners() {
        profileImage.setOnClickListener(v -> openImagePicker());

        btnSignUp.setOnClickListener(v -> attemptRegistration());

        btn_signin.setOnClickListener(v -> navigateToLogin());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            profileImage.setImageURI(selectedImageUri);
        }
    }

    private void attemptRegistration() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!validateInputs(email, password, confirmPassword)) {
            return;
        }

        performRegistration(email, password);
    }

    private boolean validateInputs(String email, String password, String confirmPassword) {
        if (email.isEmpty()) {
            etEmail.setError("Email is required!");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Valid email required!");
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required!");
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters!");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords don't match!");
            return false;
        }

        return true;
    }

    private void performRegistration(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign up success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Sign up successful.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // If sign up fails, display a message to the user.
                        Toast.makeText(this, "Sign up failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
        navigateToLogin();
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}