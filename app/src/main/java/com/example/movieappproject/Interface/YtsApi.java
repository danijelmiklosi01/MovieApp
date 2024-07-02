package com.example.movieappproject.Interface;

import com.example.movieappproject.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YtsApi {
    @GET("list_movies.json")
    Call<MovieResponse> getMovies(@Query("page") int page, @Query("limit") int limit);

    @GET("list_movies.json")
    Call<MovieResponse> searchMovies(@Query("query_term") String query);
}
