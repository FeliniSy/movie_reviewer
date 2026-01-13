package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.MovieAdapter;
import com.example.myapplication.database.MovieDatabase;
import com.example.myapplication.database.entity.Favorite;
import com.example.myapplication.database.entity.Like;
import com.example.myapplication.database.entity.Movie;
import com.example.myapplication.util.SharedPreferencesHelper;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {
    private RecyclerView favoritesRecyclerView;
    private MovieAdapter movieAdapter;
    private MovieDatabase database;
    private SharedPreferencesHelper prefsHelper;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        database = MovieDatabase.getInstance(this);
        prefsHelper = new SharedPreferencesHelper(this);
        currentUserId = prefsHelper.getUserId();

        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);

        movieAdapter = new MovieAdapter(null, new MovieAdapter.OnMovieClickListener() {
            @Override
            public void onMovieClick(Movie movie) {
                Intent intent = new Intent(FavoritesActivity.this, MovieDetailActivity.class);
                intent.putExtra("movie_id", movie.getId());
                startActivity(intent);
            }

            @Override
            public void onLikeClick(Movie movie, boolean isLiked) {
                toggleLike(movie);
            }

            @Override
            public void onFavoriteClick(Movie movie, boolean isFavorite) {
                toggleFavorite(movie);
            }
        }, currentUserId);

        favoritesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        favoritesRecyclerView.setAdapter(movieAdapter);

        loadFavorites();
    }

    private void loadFavorites() {
        List<Movie> favoriteMovies = database.favoriteDao().getFavoriteMovies(currentUserId);
        movieAdapter.updateMovies(favoriteMovies);
    }

    private void toggleLike(Movie movie) {
        Like existingLike = database.likeDao().getLike(currentUserId, movie.getId());
        if (existingLike != null) {
            database.likeDao().deleteLike(existingLike);
            Toast.makeText(this, "Unliked", Toast.LENGTH_SHORT).show();
        } else {
            Like like = new Like(currentUserId, movie.getId());
            database.likeDao().insertLike(like);
            Toast.makeText(this, "Liked!", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleFavorite(Movie movie) {
        Favorite existingFavorite = database.favoriteDao().getFavorite(currentUserId, movie.getId());
        if (existingFavorite != null) {
            database.favoriteDao().deleteFavorite(existingFavorite);
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            loadFavorites();
        } else {
            Favorite favorite = new Favorite(currentUserId, movie.getId());
            database.favoriteDao().insertFavorite(favorite);
            Toast.makeText(this, "Added to favorites!", Toast.LENGTH_SHORT).show();
        }
    }
}
