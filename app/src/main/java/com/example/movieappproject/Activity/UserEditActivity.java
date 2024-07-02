package com.example.movieappproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.movieappproject.User;
import com.example.movieappproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserEditActivity extends AppCompatActivity {

    private EditText fullNameEditText, usernameEditText, passwordEditText;
    private Button saveButton, deleteButton;
    private ImageView imageView2, imageView5, imageView10, imageView3;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        imageView2 = findViewById(R.id.imageView2);
        imageView5 = findViewById(R.id.imageView5);
        imageView3 = findViewById(R.id.imageView3);
        imageView10 = findViewById(R.id.imageView10);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserData();

        saveButton.setOnClickListener(v -> {
            new AlertDialog.Builder(UserEditActivity.this)
                    .setTitle("Confirm Changes")
                    .setMessage("Are you sure you want to save all the changes?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> updateUser())
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(UserEditActivity.this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete your account?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteUser())
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

        imageView2.setOnClickListener(v -> {
            Intent intent = new Intent(UserEditActivity.this, MainActivity.class);
            startActivity(intent);
        });

        imageView3.setOnClickListener(v -> {
            Intent intent = new Intent(UserEditActivity.this, FavoriteMoviesActivity.class);
            startActivity(intent);
        });

        imageView10.setOnClickListener(v -> {
            backToUserDetails();
        });

        imageView5.setOnClickListener(v -> {
            backToUserDetails();
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

                                fullNameEditText.setText(user.getFullName());
                                usernameEditText.setText(user.getUsername());
                            } else {
                                Toast.makeText(UserEditActivity.this, "No such user data", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(UserEditActivity.this, "Failed to load user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(UserEditActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserEditActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void updateUser() {
        String fullName = fullNameEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String newPassword = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            fullNameEditText.setError("Please enter your full name");
            fullNameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Please enter a username");
            usernameEditText.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(newPassword) && newPassword.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters long");
            passwordEditText.requestFocus();
            return;
        }

        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            db.collection("users").document(userId)
                    .update("fullName", fullName,
                            "username", username)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(UserEditActivity.this, "User data updated successfully", Toast.LENGTH_SHORT).show();

                        if (!TextUtils.isEmpty(newPassword)) {
                            auth.getCurrentUser().updatePassword(newPassword)
                                    .addOnSuccessListener(aVoid1 -> {
                                        Toast.makeText(UserEditActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(UserEditActivity.this, "Failed to update password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                        backToUserDetails();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(UserEditActivity.this, "Failed to update user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(UserEditActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserEditActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void deleteUser() {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            db.collection("users").document(userId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Delete user authentication
                        auth.getCurrentUser().delete()
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(UserEditActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(UserEditActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(UserEditActivity.this, "Failed to delete account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(UserEditActivity.this, "Failed to delete user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(UserEditActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserEditActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void backToUserDetails() {
        Intent intent = new Intent(UserEditActivity.this, UserDetailActivity.class);
        startActivity(intent);
        finish();
    }
}
