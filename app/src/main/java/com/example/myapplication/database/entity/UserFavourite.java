package com.example.myapplication.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "user_favourite",
    foreignKeys = {
        @ForeignKey(
            entity = User.class,
            parentColumns = "id",
            childColumns = "userId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = Movie.class,
            parentColumns = "id",
            childColumns = "movieId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index(value = {"userId", "movieId"}, unique = true),
        @Index("userId"),
        @Index("movieId")
    }
)
public class UserFavourite {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private int movieId;

    public UserFavourite() {}

    public UserFavourite(int userId, int movieId) {
        this.userId = userId;
        this.movieId = movieId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }
}

