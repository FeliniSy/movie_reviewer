package com.example.myapplication.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myapplication.database.dao.FavoriteDao;
import com.example.myapplication.database.dao.FriendDao;
import com.example.myapplication.database.dao.LikeDao;
import com.example.myapplication.database.dao.MessageDao;
import com.example.myapplication.database.dao.MovieDao;
import com.example.myapplication.database.dao.UserDao;
import com.example.myapplication.database.entity.Favorite;
import com.example.myapplication.database.entity.Friend;
import com.example.myapplication.database.entity.Like;
import com.example.myapplication.database.entity.Message;
import com.example.myapplication.database.entity.Movie;
import com.example.myapplication.database.entity.User;

@Database(
    entities = {Movie.class, User.class, Favorite.class, Like.class, Message.class, Friend.class},
    version = 1,
    exportSchema = false
)
public abstract class MovieDatabase extends RoomDatabase {
    private static MovieDatabase instance;

    public abstract MovieDao movieDao();
    public abstract UserDao userDao();
    public abstract FavoriteDao favoriteDao();
    public abstract LikeDao likeDao();
    public abstract MessageDao messageDao();
    public abstract FriendDao friendDao();

    public static synchronized MovieDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                MovieDatabase.class,
                "movie_database"
            ).allowMainThreadQueries().build();
        }
        return instance;
    }
}
