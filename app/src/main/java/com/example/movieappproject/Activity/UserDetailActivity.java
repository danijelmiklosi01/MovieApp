package com.example.movieappproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movieappproject.User;
import com.example.movieappproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserDetailActivity extends AppCompatActivity {

    private TextView fullNameTextView, emailTextView, usernameTextView;
    private Button logoutButton, editButton;
    private ImageView imageView2, imageView3;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        fullNameTextView = findViewById(R.id.fullNameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        logoutButton = findViewById(R.id.logoutButton);
        editButton = findViewById(R.id.editButton);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserData();

        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(UserDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        imageView2.setOnClickListener(v -> {
            Intent intent = new Intent(UserDetailActivity.this, MainActivity.class);
            startActivity(intent);
        });

        imageView3.setOnClickListener(v -> {
            Intent intent = new Intent(UserDetailActivity.this, FavoriteMoviesActivity.class);
            startActivity(intent);
        });

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserDetailActivity.this, UserEditActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserData() {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            db.collection("users").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                User user = document.toObject(User.class);

                                fullNameTextView.setText(user.getFullName());
                                emailTextView.setText(user.getEmail());
                                usernameTextView.setText(user.getUsername());
                            } else {
                                Toast.makeText(UserDetailActivity.this, "No such user data", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(UserDetailActivity.this, "Failed to load user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(UserDetailActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
