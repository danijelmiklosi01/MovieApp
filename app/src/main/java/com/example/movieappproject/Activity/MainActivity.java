package com.example.movieappproject.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieappproject.ApiClient;
import com.example.movieappproject.Interface.YtsApi;
import com.example.movieappproject.Movie;
import com.example.movieappproject.MovieResponse;
import com.example.movieappproject.MoviesAdapter;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.movieappproject.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    private EditText searchEditText;
    private RecyclerView recyclerView;
    private MoviesAdapter moviesAdapter;
    private List<Movie> movieList;
    private LinearLayoutManager layoutManager;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private static final int PAGE_SIZE = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEditText = findViewById(R.id.editTextText3);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ImageView imageViewUser = findViewById(R.id.imageView5);
        ImageView imageViewFavorites = findViewById(R.id.imageView3);

        movieList = new ArrayList<>();


        moviesAdapter = new MoviesAdapter(movieList, new MoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(moviesAdapter);

        loadMovies(currentPage);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1) && !isLoading && !isLastPage) {
                    currentPage++;
                    loadMovies(currentPage);
                }
            }
        });


        searchEditText.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchMovies(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        imageViewUser.setOnClickListener(view -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                Intent intent = new Intent(MainActivity.this, UserDetailActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "User is not logged in", Toast.LENGTH_SHORT).show();
            }
        });
        imageViewFavorites.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, FavoriteMoviesActivity.class);
            startActivity(intent);
        });
    }

    private void loadMovies(int page) {
        isLoading = true;
        YtsApi apiService = ApiClient.getClient().create(YtsApi.class);
        Call<MovieResponse> call = apiService.getMovies(page, PAGE_SIZE);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getData().getMovies();
                    if (movies.size() < PAGE_SIZE) {
                        isLastPage = true;
                    }
                    movieList.addAll(movies);
                    moviesAdapter.notifyDataSetChanged();
                    isLoading = false;
                } else {
                    Toast.makeText(MainActivity.this, "Failed to retrieve movies", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                isLoading = false;
            }
        });
    }

    private void searchMovies(String query) {
        if (query.isEmpty()) {
            moviesAdapter.filterList(movieList);
            return;
        }

        YtsApi apiService = ApiClient.getClient().create(YtsApi.class);
        Call<MovieResponse> call = apiService.searchMovies(query);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getData().getMovies();
                    moviesAdapter.filterList(movies);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to retrieve search results", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


