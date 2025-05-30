package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    EditText email, pass;
    Button login;
    Button btn_register;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.sign_in);

        mAuth = FirebaseAuth.getInstance();

        // Edge-to-edge inset handling
        applyEdgeToEdgeInsetHandling();

        // Initialize UI components
        email = findViewById(R.id.et_email);
        pass = findViewById(R.id.et_password);
        login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);


        login.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            String userPassword = pass.getText().toString().trim();

            if (userEmail.isEmpty()) {
                email.setError("Email is required!");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                email.setError("Valid email required!");
                return;
            }
            if (userPassword.isEmpty()) {
                pass.setError("Password is required!");
                return;
            }

            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Log in success, update UI with the signed-in user's information
                            //FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(this, "Log in successful.",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, QuizActivity.class));
                            finish();
                        } else {
                            // If log in fails, display a message to the user.
                            Toast.makeText(this, "Log in failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });


        btn_register.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void applyEdgeToEdgeInsetHandling() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}