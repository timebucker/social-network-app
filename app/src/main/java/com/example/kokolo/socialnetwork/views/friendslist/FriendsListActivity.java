package com.example.kokolo.socialnetwork.views.friendslist;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.kokolo.socialnetwork.R;
import com.example.kokolo.socialnetwork.contracts.FriendsListContract;
import com.example.kokolo.socialnetwork.models.friendslist.Friends;
import com.example.kokolo.socialnetwork.presenters.findfriend.FindFriendPresenter;
import com.example.kokolo.socialnetwork.presenters.friendslist.FriendsListPresenter;
import com.example.kokolo.socialnetwork.views.chat.ChatActivity;
import com.example.kokolo.socialnetwork.views.profile.ProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsListActivity extends AppCompatActivity implements FriendsListContract.View{

    RecyclerView friendsList;
    FriendsListPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        initViews();
        presenter = new FriendsListPresenter(this);

        presenter.displayAllFriends();
    }

    private void initViews() {
        friendsList = findViewById(R.id.friends_list);
        friendsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        friendsList.setLayoutManager(linearLayoutManager);
    }


    public void SendUserToProfileActivity(String userId) {
        Intent profileIntent = new Intent(FriendsListActivity.this, ProfileActivity.class);
        profileIntent.putExtra("userId", userId);
        startActivity(profileIntent);
    }

    public void SendUserToChatActivity(String userId, String userName) {
        Intent chatIntent = new Intent(FriendsListActivity.this, ChatActivity.class);
        chatIntent.putExtra("userId", userId);
        chatIntent.putExtra("userName", userName);
        startActivity(chatIntent);
    }

    @Override
    public void setFriendListAdapter(FirebaseRecyclerAdapter<Friends, FriendViewHolder> firebaseRecyclerAdapter) {
        this.friendsList.setAdapter(firebaseRecyclerAdapter);
    }
}
