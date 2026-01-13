package com.example.myapplication.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.database.MovieDatabase;
import com.example.myapplication.database.entity.Message;
import com.example.myapplication.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;
    private EditText messageEditText;
    private ImageButton sendButton;
    private MovieDatabase database;
    private SharedPreferencesHelper prefsHelper;
    private int currentUserId;
    private int friendId;
    private String friendUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = MovieDatabase.getInstance(this);
        prefsHelper = new SharedPreferencesHelper(this);
        currentUserId = prefsHelper.getUserId();

        friendId = getIntent().getIntExtra("friend_id", -1);
        friendUsername = getIntent().getStringExtra("friend_username");

        if (friendId == -1) {
            Toast.makeText(this, "Invalid friend", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setTitle("Chat with " + friendUsername);

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        messagesAdapter = new MessagesAdapter();
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messagesAdapter);

        sendButton.setOnClickListener(v -> sendMessage());

        loadMessages();
    }

    private void sendMessage() {
        String content = messageEditText.getText().toString().trim();
        if (content.isEmpty()) {
            return;
        }

        Message message = new Message(currentUserId, friendId, content, System.currentTimeMillis());
        database.messageDao().insertMessage(message);
        messageEditText.setText("");
        loadMessages();
    }

    private void loadMessages() {
        List<Message> messages = database.messageDao().getMessagesBetweenUsers(currentUserId, friendId);
        messagesAdapter.updateMessages(messages);
        messagesRecyclerView.scrollToPosition(messages.size() - 1);
    }

    private class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
        private List<Message> messages;

        public MessagesAdapter() {
            this.messages = new ArrayList<>();
        }

        public void updateMessages(List<Message> messages) {
            this.messages = messages;
            notifyDataSetChanged();
        }

        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MessageViewHolder holder, int position) {
            Message message = messages.get(position);
            holder.bind(message);
        }

        @Override
        public int getItemCount() {
            return messages != null ? messages.size() : 0;
        }

        class MessageViewHolder extends RecyclerView.ViewHolder {
            private TextView messageTextView;
            private TextView timestampTextView;

            public MessageViewHolder(View itemView) {
                super(itemView);
                messageTextView = itemView.findViewById(R.id.messageTextView);
                timestampTextView = itemView.findViewById(R.id.timestampTextView);
            }

            public void bind(Message message) {
                messageTextView.setText(message.getContent());
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
                timestampTextView.setText(sdf.format(new java.util.Date(message.getTimestamp())));

                if (message.getSenderId() == currentUserId) {
                    itemView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                } else {
                    itemView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                }
            }
        }
    }
}
