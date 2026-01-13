package com.example.myapplication.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.api.TMDBApiService;
import com.example.myapplication.database.MovieDatabase;
import com.example.myapplication.database.entity.Favorite;
import com.example.myapplication.database.entity.Like;
import com.example.myapplication.database.entity.Movie;
import com.example.myapplication.util.SharedPreferencesHelper;

public class MovieDetailActivity extends AppCompatActivity {
    private ImageView posterImageView;
    private ImageView backdropImageView;
    private TextView titleTextView;
    private TextView ratingTextView;
    private TextView releaseDateTextView;
    private TextView descriptionTextView;
    private MovieDatabase database;
    private SharedPreferencesHelper prefsHelper;
    private int currentUserId;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        database = MovieDatabase.getInstance(this);
        prefsHelper = new SharedPreferencesHelper(this);
        currentUserId = prefsHelper.getUserId();

        int movieId = getIntent().getIntExtra("movie_id", -1);
        if (movieId == -1) {
            Toast.makeText(this, "Movie not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        movie = database.movieDao().getMovieById(movieId);
        if (movie == null) {
            Toast.makeText(this, "Movie not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        displayMovieDetails();
    }

    private void initViews() {
        posterImageView = findViewById(R.id.posterImageView);
        backdropImageView = findViewById(R.id.backdropImageView);
        titleTextView = findViewById(R.id.titleTextView);
        ratingTextView = findViewById(R.id.ratingTextView);
        releaseDateTextView = findViewById(R.id.releaseDateTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
    }

    private void displayMovieDetails() {
        titleTextView.setText(movie.getTitle());
        ratingTextView.setText(String.format("Rating: %.1f/10", movie.getVoteAverage()));
        
        if (movie.getReleaseDate() != null && !movie.getReleaseDate().isEmpty()) {
            releaseDateTextView.setText("Release Date: " + movie.getReleaseDate());
        } else {
            releaseDateTextView.setText("Release Date: N/A");
        }
        
        descriptionTextView.setText(movie.getOverview() != null && !movie.getOverview().isEmpty() 
            ? movie.getOverview() 
            : "No description available.");

        if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) {
            String posterUrl = TMDBApiService.IMAGE_BASE_URL + movie.getPosterPath();
            Glide.with(this)
                .load(posterUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(posterImageView);
        }

        if (movie.getBackdropPath() != null && !movie.getBackdropPath().isEmpty()) {
            String backdropUrl = TMDBApiService.IMAGE_BASE_URL + movie.getBackdropPath();
            Glide.with(this)
                .load(backdropUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(backdropImageView);
        }
    }
}
