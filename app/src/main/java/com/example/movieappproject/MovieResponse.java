package com.example.movieappproject;

import com.example.movieappproject.Movie;

import java.util.List;

public class MovieResponse {
    private String status;
    private Data data;

    public Data getData() {
        return data;
    }

    public class Data {
        private List<Movie> movies;
        private int movie_count;

        public List<Movie> getMovies() {
            return movies;
        }

        public int getMovieCount() {
            return movie_count;
        }
    }
}
