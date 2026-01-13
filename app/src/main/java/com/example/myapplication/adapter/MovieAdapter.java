package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.api.TMDBApiService;
import com.example.myapplication.database.entity.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> movies;
    private OnMovieClickListener listener;
    private int currentUserId;

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
        void onLikeClick(Movie movie, boolean isLiked);
        void onFavoriteClick(Movie movie, boolean isFavorite);
    }

    public MovieAdapter(List<Movie> movies, OnMovieClickListener listener, int currentUserId) {
        this.movies = movies;
        this.listener = listener;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    public void updateMovies(List<Movie> newMovies) {
        this.movies = newMovies;
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView posterImageView;
        private TextView titleTextView;
        private TextView ratingTextView;
        private ImageButton likeImageView;
        private ImageButton favoriteImageView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.posterImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            likeImageView = itemView.findViewById(R.id.likeImageView);
            favoriteImageView = itemView.findViewById(R.id.favoriteImageView);
        }

        public void bind(Movie movie) {
            titleTextView.setText(movie.getTitle());
            ratingTextView.setText(String.format("%.1f", movie.getVoteAverage()));

            if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) {
                String imageUrl = TMDBApiService.IMAGE_BASE_URL + movie.getPosterPath();
                Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(posterImageView);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMovieClick(movie);
                }
            });

            likeImageView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLikeClick(movie, false);
                }
            });

            favoriteImageView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFavoriteClick(movie, false);
                }
            });
        }
    }
}
