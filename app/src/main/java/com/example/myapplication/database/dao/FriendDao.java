package com.example.myapplication.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myapplication.database.entity.Friend;
import com.example.myapplication.database.entity.User;

import java.util.List;

@Dao
public interface FriendDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFriend(Friend friend);

    @Delete
    void deleteFriend(Friend friend);

    @Query("SELECT users.* FROM users INNER JOIN friends ON users.id = friends.friendId WHERE friends.userId = :userId")
    List<User> getFriends(int userId);

    @Query("SELECT * FROM friends WHERE userId = :userId AND friendId = :friendId")
    Friend getFriend(int userId, int friendId);
}
