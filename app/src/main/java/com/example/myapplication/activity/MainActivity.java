package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.MovieAdapter;
import com.example.myapplication.api.RetrofitClient;
import com.example.myapplication.api.TMDBApiService;
import com.example.myapplication.api.model.MovieResult;
import com.example.myapplication.api.model.MovieResponse;
import com.example.myapplication.database.MovieDatabase;
import com.example.myapplication.database.entity.Favorite;
import com.example.myapplication.database.entity.Like;
import com.example.myapplication.database.entity.Movie;
import com.example.myapplication.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView moviesRecyclerView;
    private MovieAdapter movieAdapter;
    private List<Movie> movies;
    private MovieDatabase database;
    private SharedPreferencesHelper prefsHelper;
    private int currentUserId;
    private EditText searchEditText;
    private android.widget.Button searchButton;
    private boolean isSearchMode = false;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = MovieDatabase.getInstance(this);
        prefsHelper = new SharedPreferencesHelper(this);

        if (!prefsHelper.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        currentUserId = prefsHelper.getUserId();
        movies = new ArrayList<>();

        // Setup toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Movie App");
        }

        moviesRecyclerView = findViewById(R.id.moviesRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);

        movieAdapter = new MovieAdapter(movies, new MovieAdapter.OnMovieClickListener() {
            @Override
            public void onMovieClick(Movie movie) {
                Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
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

        moviesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        moviesRecyclerView.setAdapter(movieAdapter);

        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                searchMovies(query);
            } else {
                loadPopularMovies();
            }
        });

        // Allow search on Enter key
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchMovies(query);
                } else {
                    loadPopularMovies();
                }
                return true;
            }
            return false;
        });

        loadPopularMovies();
    }

    private void loadPopularMovies() {
        isSearchMode = false;
        currentPage = 1;
        loadMoviesPage(1);
    }

    private void loadMoviesPage(int page) {
        RetrofitClient.getInstance().getApiService()
            .getPopularMovies(TMDBApiService.API_KEY, page)
            .enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<MovieResult> results = response.body().getResults();
                        if (page == 1) {
                            movies.clear();
                        }
                        for (MovieResult result : results) {
                            Movie movie = convertToMovie(result);
                            database.movieDao().insertMovie(movie);
                            movies.add(movie);
                        }
                        movieAdapter.updateMovies(movies);
                        
                        // Load more pages if available (up to 5 pages = ~100 movies)
                        if (page < 5 && response.body().getTotalPages() > page) {
                            loadMoviesPage(page + 1);
                        }
                    }
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    if (page == 1) {
                        Toast.makeText(MainActivity.this, "Failed to load movies", Toast.LENGTH_SHORT).show();
                        loadMoviesFromDatabase();
                    }
                }
            });
    }

    private void searchMovies(String query) {
        isSearchMode = true;
        RetrofitClient.getInstance().getApiService()
            .searchMovies(TMDBApiService.API_KEY, query, 1)
            .enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<MovieResult> results = response.body().getResults();
                        movies.clear();
                        for (MovieResult result : results) {
                            Movie movie = convertToMovie(result);
                            database.movieDao().insertMovie(movie);
                            movies.add(movie);
                        }
                        movieAdapter.updateMovies(movies);
                    }
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Search failed", Toast.LENGTH_SHORT).show();
                    List<Movie> dbMovies = database.movieDao().searchMovies(query);
                    movies.clear();
                    movies.addAll(dbMovies);
                    movieAdapter.updateMovies(movies);
                }
            });
    }

    private void loadMoviesFromDatabase() {
        movies.clear();
        movies.addAll(database.movieDao().getAllMovies());
        movieAdapter.updateMovies(movies);
    }

    private Movie convertToMovie(MovieResult result) {
        return new Movie(
            result.getId(),
            result.getTitle(),
            result.getOverview(),
            result.getPosterPath(),
            result.getVoteAverage(),
            result.getReleaseDate(),
            result.getBackdropPath()
        );
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
        } else {
            Favorite favorite = new Favorite(currentUserId, movie.getId());
            database.favoriteDao().insertFavorite(favorite);
            Toast.makeText(this, "Added to favorites!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_favorites) {
            startActivity(new Intent(this, FavoritesActivity.class));
            return true;
        } else if (id == R.id.menu_friends) {
            startActivity(new Intent(this, FriendsActivity.class));
            return true;
        } else if (id == R.id.menu_logout) {
            prefsHelper.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
