package com.example.myapplication.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myapplication.database.entity.UserFavourite;

import java.util.List;

@Dao
public interface UserFavouriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserFavourite userFavourite);

    @Delete
    void delete(UserFavourite userFavourite);

    @Query("SELECT * FROM user_favourite WHERE userId = :userId AND movieId = :movieId LIMIT 1")
    UserFavourite get(int userId, int movieId);

    @Query("SELECT movieId FROM user_favourite WHERE userId = :userId")
    List<Integer> getMovieIds(int userId);

    @Query("SELECT COUNT(*) FROM user_favourite WHERE userId = :userId AND movieId = :movieId")
    int isFavourite(int userId, int movieId);
}

