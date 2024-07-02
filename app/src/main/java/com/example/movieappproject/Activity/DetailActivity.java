package com.example.movieappproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.movieappproject.Movie;
import com.example.movieappproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Movie movie = (Movie) getIntent().getSerializableExtra("movie");

        if (movie != null) {
            ImageView posterImageView = findViewById(R.id.posterNormaling);
            TextView movieNameTxt = findViewById(R.id.movieNameTxt);
            TextView movieRateTxt = findViewById(R.id.movieRateTxt);
            TextView movieTimeTxt = findViewById(R.id.movieTimeTxt);
            TextView movieDateTxt = findViewById(R.id.movieDateTxt);
            TextView movieSummaryInfo = findViewById(R.id.movieSummaryInfo);

            Glide.with(this).load(movie.getLarge_cover_image()).into(posterImageView);

            movieNameTxt.setText(movie.getTitle());
            movieRateTxt.setText(movie.getRating());
            movieTimeTxt.setText(String.valueOf(movie.getRuntime()));
            movieDateTxt.setText(movie.getYear());
            String summary = movie.getSummary();
            if (summary == null || summary.isEmpty()) {
                movieSummaryInfo.setText("This movie has no summary sorry :/");
            } else {
                movieSummaryInfo.setText(summary);
            }

            ImageView backButton = findViewById(R.id.imageView10);
            backButton.setOnClickListener(v -> onBackPressed());

            ImageView addToFavorites = findViewById(R.id.imageView9);
            addToFavorites.setOnClickListener(v -> addToFavorites(movie));
        } else {
            Toast.makeText(this, "Failed to load movie details", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToFavorites(Movie movie) {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            db.collection("users").document(userId)
                    .collection("favorites").document(movie.getId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Toast.makeText(DetailActivity.this, "Movie is already in favorites", Toast.LENGTH_SHORT).show();
                            } else {
                                db.collection("users").document(userId)
                                        .collection("favorites").document(movie.getId())
                                        .set(movie)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(DetailActivity.this, "Movie added to favorites", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(DetailActivity.this, "Failed to add movie to favorites: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Toast.makeText(DetailActivity.this, "Failed to check if movie is in favorites: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(DetailActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DetailActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
