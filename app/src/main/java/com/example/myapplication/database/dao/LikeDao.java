package com.example.myapplication.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myapplication.database.entity.Like;

@Dao
public interface LikeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLike(Like like);

    @Delete
    void deleteLike(Like like);

    @Query("SELECT * FROM likes WHERE userId = :userId AND movieId = :movieId")
    Like getLike(int userId, int movieId);

    @Query("SELECT COUNT(*) FROM likes WHERE userId = :userId AND movieId = :movieId")
    int isLiked(int userId, int movieId);
}
