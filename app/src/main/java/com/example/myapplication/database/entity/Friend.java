package com.example.myapplication.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "friends",
    foreignKeys = {
        @ForeignKey(
            entity = User.class,
            parentColumns = "id",
            childColumns = "userId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = User.class,
            parentColumns = "id",
            childColumns = "friendId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index("userId"), @Index("friendId")}
)
public class Friend {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private int friendId;

    public Friend() {
    }

    public Friend(int userId, int friendId) {
        this.userId = userId;
        this.friendId = friendId;
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

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }
}
