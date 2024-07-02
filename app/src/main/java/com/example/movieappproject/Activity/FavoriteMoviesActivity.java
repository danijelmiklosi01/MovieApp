package com.example.movieappproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieappproject.FavoriteMoviesAdapter;
import com.example.movieappproject.Movie;
import com.example.movieappproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FavoriteMoviesActivity extends AppCompatActivity implements FavoriteMoviesAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private FavoriteMoviesAdapter adapter;
    private List<Movie> favoriteMoviesList;
    private List<Movie> fullMovieList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_movies);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoriteMoviesList = new ArrayList<>();
        fullMovieList = new ArrayList<>();
        adapter = new FavoriteMoviesAdapter(this, favoriteMoviesList, this);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        searchEditText = findViewById(R.id.editTextText3);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Ne treba ništa ovde
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMovies(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        findViewById(R.id.imageView2).setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteMoviesActivity.this, MainActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.imageView5).setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteMoviesActivity.this, UserDetailActivity.class);
            startActivity(intent);
        });

        loadFavoriteMovies();
    }

    @Override
    public void onDeleteClick(int position) {
        Movie movie = favoriteMoviesList.get(position);
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            db.collection("users").document(userId)
                    .collection("favorites").document(movie.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        favoriteMoviesList.remove(position);
                        adapter.notifyItemRemoved(position);

                        if (favoriteMoviesList.isEmpty()) {
                            Toast.makeText(FavoriteMoviesActivity.this, "You don't have any favorite movies.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(FavoriteMoviesActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(FavoriteMoviesActivity.this, "Failed to delete favorite movie: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(FavoriteMoviesActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFavoriteMovies() {
        if (auth.getCurrentUser() != null && db != null) {
            String userId = auth.getCurrentUser().getUid();

            db.collection("users").document(userId)
                    .collection("favorites")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        favoriteMoviesList.clear();
                        fullMovieList.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Movie movie = documentSnapshot.toObject(Movie.class);
                            if (movie != null) {
                                favoriteMoviesList.add(movie);
                                fullMovieList.add(movie);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        if (favoriteMoviesList.isEmpty()) {
                            Toast.makeText(FavoriteMoviesActivity.this, "You don't have any favorite movies yet. Go to the explore page and find some!", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(FavoriteMoviesActivity.this, "Failed to load favorite movies: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(FavoriteMoviesActivity.this, "User not logged in or Firestore instance not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void filterMovies(String query) {
        List<Movie> filteredList = fullMovieList.stream()
                .filter(movie -> movie.getTitle().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        favoriteMoviesList.clear();
        favoriteMoviesList.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }
}
