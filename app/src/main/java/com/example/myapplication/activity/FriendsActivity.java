package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.database.MovieDatabase;
import com.example.myapplication.database.entity.Friend;
import com.example.myapplication.database.entity.User;
import com.example.myapplication.util.SharedPreferencesHelper;

import java.util.List;

public class FriendsActivity extends AppCompatActivity {
    private RecyclerView friendsRecyclerView;
    private FriendsAdapter friendsAdapter;
    private MovieDatabase database;
    private SharedPreferencesHelper prefsHelper;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        database = MovieDatabase.getInstance(this);
        prefsHelper = new SharedPreferencesHelper(this);
        currentUserId = prefsHelper.getUserId();

        friendsRecyclerView = findViewById(R.id.friendsRecyclerView);
        friendsAdapter = new FriendsAdapter();
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendsRecyclerView.setAdapter(friendsAdapter);

        loadFriends();
    }

    private void loadFriends() {
        List<User> allUsers = database.userDao().getAllUsersExcept(currentUserId);
        TextView emptyTextView = findViewById(R.id.emptyTextView);
        
        if (allUsers.isEmpty()) {
            if (emptyTextView != null) {
                emptyTextView.setVisibility(android.view.View.VISIBLE);
            }
            friendsRecyclerView.setVisibility(android.view.View.GONE);
        } else {
            if (emptyTextView != null) {
                emptyTextView.setVisibility(android.view.View.GONE);
            }
            friendsRecyclerView.setVisibility(android.view.View.VISIBLE);
            friendsAdapter.updateFriends(allUsers, false);
        }
    }

    private void loadMyFriends() {
        List<User> friends = database.friendDao().getFriends(currentUserId);
        friendsAdapter.updateFriends(friends, true);
    }

    private class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {
        private List<User> friends;
        private boolean isFriendsList;

        public FriendsAdapter() {
            this.friends = new java.util.ArrayList<>();
            this.isFriendsList = false;
        }

        public void updateFriends(List<User> friends, boolean isFriendsList) {
            this.friends = friends;
            this.isFriendsList = isFriendsList;
            notifyDataSetChanged();
        }

        @Override
        public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
            return new FriendViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FriendViewHolder holder, int position) {
            User friend = friends.get(position);
            holder.bind(friend);
        }

        @Override
        public int getItemCount() {
            return friends != null ? friends.size() : 0;
        }

        class FriendViewHolder extends RecyclerView.ViewHolder {
            private TextView usernameTextView;
            private TextView emailTextView;
            private Button actionButton;

            public FriendViewHolder(View itemView) {
                super(itemView);
                usernameTextView = itemView.findViewById(R.id.usernameTextView);
                emailTextView = itemView.findViewById(R.id.emailTextView);
                actionButton = itemView.findViewById(R.id.actionButton);
            }

            public void bind(User user) {
                usernameTextView.setText(user.getUsername());
                emailTextView.setText(user.getEmail());

                // Check if already a friend
                Friend existingFriend = database.friendDao().getFriend(currentUserId, user.getId());
                boolean isFriend = existingFriend != null;

                if (isFriend || isFriendsList) {
                    actionButton.setText("Message");
                    actionButton.setOnClickListener(v -> {
                        Intent intent = new Intent(FriendsActivity.this, ChatActivity.class);
                        intent.putExtra("friend_id", user.getId());
                        intent.putExtra("friend_username", user.getUsername());
                        startActivity(intent);
                    });
                } else {
                    actionButton.setText("Add Friend");
                    actionButton.setOnClickListener(v -> {
                        Friend friend = new Friend(currentUserId, user.getId());
                        database.friendDao().insertFriend(friend);
                        Toast.makeText(FriendsActivity.this, "Friend added!", Toast.LENGTH_SHORT).show();
                        actionButton.setText("Message");
                        actionButton.setOnClickListener(v2 -> {
                            Intent intent = new Intent(FriendsActivity.this, ChatActivity.class);
                            intent.putExtra("friend_id", user.getId());
                            intent.putExtra("friend_username", user.getUsername());
                            startActivity(intent);
                        });
                    });
                }

                itemView.setOnClickListener(v -> {
                    if (isFriend || isFriendsList) {
                        Intent intent = new Intent(FriendsActivity.this, ChatActivity.class);
                        intent.putExtra("friend_id", user.getId());
                        intent.putExtra("friend_username", user.getUsername());
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
