package com.example.myapplication.api;

import com.example.myapplication.api.model.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TMDBApiService {
    String BASE_URL = "https://api.themoviedb.org/3/";
    String API_KEY = "f8f2226c42d08ad9c4519618b6304b92";
    String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(
        @Query("api_key") String apiKey,
        @Query("page") int page
    );

    @GET("search/movie")
    Call<MovieResponse> searchMovies(
        @Query("api_key") String apiKey,
        @Query("query") String query,
        @Query("page") int page
    );
}
