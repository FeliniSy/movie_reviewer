package com.example.myapplication.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myapplication.database.entity.Favorite;
import com.example.myapplication.database.entity.Movie;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavorite(Favorite favorite);

    @Delete
    void deleteFavorite(Favorite favorite);

    @Query("SELECT * FROM favorites WHERE userId = :userId AND movieId = :movieId")
    Favorite getFavorite(int userId, int movieId);

    @Query("SELECT movies.* FROM movies INNER JOIN favorites ON movies.id = favorites.movieId WHERE favorites.userId = :userId")
    List<Movie> getFavoriteMovies(int userId);

    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId AND movieId = :movieId")
    int isFavorite(int userId, int movieId);
}
