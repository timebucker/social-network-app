package com.example.kokolo.socialnetwork.views.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.ChatContract;
import com.example.kokolo.socialnetwork.models.chat.Messages;
import com.example.kokolo.socialnetwork.presenters.chat.ChatPresenter;
import com.example.kokolo.socialnetwork.presenters.chat.MessagesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements ChatContract.View {

    Toolbar chatToolbar;
    ImageButton sendMessageButton, sendImageFileButton;
    EditText inputMessage;

    TextView receiverNameTextView;
    CircleImageView receiverProfileImage;

    RecyclerView usersMessageList;
    final List<Messages> messagesList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    MessagesAdapter messagesAdapter;

    ChatPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String receiverId = getIntent().getStringExtra("userId");
        String receiverName = getIntent().getStringExtra("userName");

        IntializeFields();

        presenter = new ChatPresenter(this, receiverId, receiverName);

        presenter.DisplayReceiverInfo();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.SendMessages();
            }
        });

        presenter.FetchMessages();
    }

    private void IntializeFields() {
        chatToolbar = findViewById(R.id.chat_bar_layout);
        setSupportActionBar(chatToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        receiverNameTextView = findViewById(R.id.custom_profile_name);
        receiverProfileImage = findViewById(R.id.custom_profile_image);

        sendMessageButton = findViewById(R.id.send_message_button);
        sendImageFileButton = findViewById(R.id.send_image_file_button);
        inputMessage = findViewById(R.id.input_message);
        usersMessageList = findViewById(R.id.messages_list_users);


        // Quang code 10/07/2018
        messagesAdapter = new MessagesAdapter(messagesList);
        linearLayoutManager = new LinearLayoutManager(this);
        usersMessageList.setHasFixedSize(true);
        usersMessageList.setLayoutManager(linearLayoutManager);
        usersMessageList.setAdapter(messagesAdapter);
    }

    @Override
    public void setTextForReceiverNameTextView(String text) {
        receiverNameTextView.setText(text);
    }

    @Override
    public void setTextForInputMessage(String text) {
        this.inputMessage.setText(text);
    }

    @Override
    public String getTextFormInputMessage() {
        return this.inputMessage.getText().toString();
    }

    @Override
    public void loadFriendProfileImage(String profileImage) {
        Picasso.get().load(profileImage).placeholder(R.drawable.profile).into(receiverProfileImage);
    }

    @Override
    public void addMessage(Messages messages) {
        messagesList.add(messages);
        messagesAdapter.notifyDataSetChanged();
    }
}
